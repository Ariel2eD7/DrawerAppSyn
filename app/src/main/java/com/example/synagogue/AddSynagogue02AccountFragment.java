package com.example.synagogue;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddSynagogue02AccountFragment extends Fragment {

    private FirebaseAuth auth;

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;

    private MaterialButton backButton;
    private MaterialButton continueButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_02_account,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupProgress(view, 2);

        auth = FirebaseAuth.getInstance();

        emailLayout = view.findViewById(R.id.emailLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        confirmPasswordLayout =
                view.findViewById(R.id.confirmPasswordLayout);

        emailEditText = view.findViewById(R.id.emailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        confirmPasswordEditText =
                view.findViewById(R.id.confirmPasswordEditText);

        backButton = view.findViewById(R.id.backButton);
        continueButton = view.findViewById(R.id.continueButton);

        setupListeners();
    }

    private void setupListeners() {

        backButton.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        continueButton.setOnClickListener(v ->
                validateAndCreateAccount()
        );
    }

    private void validateAndCreateAccount() {

        clearErrors();

        String email = getText(emailEditText);
        String password = getText(passwordEditText);
        String confirmPassword = getText(confirmPasswordEditText);

        if (TextUtils.isEmpty(email)) {

            emailLayout.setError(
                    "נא להזין כתובת אימייל"
            );

            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailLayout.setError(
                    "כתובת האימייל אינה תקינה"
            );

            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {

            passwordLayout.setError(
                    "נא להזין סיסמה"
            );

            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 8) {

            passwordLayout.setError(
                    "הסיסמה חייבת להכיל לפחות 8 תווים"
            );

            passwordEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {

            confirmPasswordLayout.setError(
                    "נא לאשר את הסיסמה"
            );

            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            confirmPasswordLayout.setError(
                    "הסיסמאות אינן תואמות"
            );

            confirmPasswordEditText.requestFocus();
            return;
        }

        createFirebaseAccount(
                email,
                password
        );
    }

    private void createFirebaseAccount(
            String email,
            String password) {

        setLoading(true);

        auth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(requireActivity(), task -> {

                    if (!task.isSuccessful()) {

                        setLoading(false);

                        showFirebaseError(
                                task.getException()
                        );

                        return;
                    }

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        setLoading(false);

                        showMessage(
                                "אירעה שגיאה ביצירת החשבון. נסו שוב."
                        );

                        return;
                    }

                    sendVerificationEmail(user);
                });
    }

    private void sendVerificationEmail(
            FirebaseUser user) {

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {

                    setLoading(false);

                    if (!task.isSuccessful()) {

                        showMessage(
                                "החשבון נוצר, אך לא הצלחנו לשלוח את אימייל האימות."
                        );

                        return;
                    }

                    Bundle bundle = new Bundle();

                    bundle.putString(
                            "ownerId",
                            user.getUid()
                    );

                    bundle.putString(
                            "email",
                            user.getEmail()
                    );

                    AddSynagogueEmailVerificationFragment
                            verificationFragment =
                            new AddSynagogueEmailVerificationFragment();

                    verificationFragment.setArguments(
                            bundle
                    );

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.fragment_container,
                                    verificationFragment
                            )
                            .addToBackStack(null)
                            .commit();
                });
    }

    private void setLoading(boolean loading) {

        continueButton.setEnabled(!loading);
        backButton.setEnabled(!loading);

        emailEditText.setEnabled(!loading);
        passwordEditText.setEnabled(!loading);
        confirmPasswordEditText.setEnabled(!loading);

        if (loading) {

            continueButton.setText(
                    "יוצר חשבון..."
            );

        } else {

            continueButton.setText(
                    "המשך ←"
            );
        }
    }

    private void clearErrors() {

        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    private String getText(
            TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString()
                .trim();
    }

    private void showFirebaseError(
            Exception exception) {

        if (exception == null) {

            showMessage(
                    "לא הצלחנו ליצור את החשבון. נסו שוב."
            );

            return;
        }

        String error = exception.getMessage();

        if (error != null &&
                error.contains("already in use")) {

            emailLayout.setError(
                    "כתובת האימייל הזו כבר רשומה במערכת."
            );

            emailEditText.requestFocus();

            return;
        }

        if (error != null &&
                error.contains("badly formatted")) {

            emailLayout.setError(
                    "כתובת האימייל אינה תקינה."
            );

            emailEditText.requestFocus();

            return;
        }

        if (error != null &&
                error.contains("network")) {

            showMessage(
                    "אין חיבור לאינטרנט. בדקו את החיבור ונסו שוב."
            );

            return;
        }

        showMessage(
                "לא הצלחנו ליצור את החשבון. נסו שוב."
        );
    }

    private void showMessage(
            String message) {

        if (getView() == null) {
            return;
        }

        Snackbar.make(
                getView(),
                message,
                Snackbar.LENGTH_LONG
        ).show();
    }




    private void setupProgress(View view, int currentStep)
    {

        int[] stepIds = {
                R.id.progressStep1,
                R.id.progressStep2,
                R.id.progressStep3,
                R.id.progressStep4,
                R.id.progressStep5,
                R.id.progressStep6
        };

        int[] lineIds = {
                R.id.progressLine1,
                R.id.progressLine2,
                R.id.progressLine3,
                R.id.progressLine4,
                R.id.progressLine5
        };

        for (int i = 0; i < stepIds.length; i++) {

            TextView step =
                    view.findViewById(stepIds[i]);

            int stepNumber = i + 1;

            if (stepNumber < currentStep) {

                // שלב שהושלם
                step.setText("✓");
                step.setTextColor(
                        Color.WHITE
                );

                step.setBackgroundResource(
                        R.drawable.bg_progress_completed
                );

            } else if (stepNumber == currentStep) {

                // השלב הנוכחי
                step.setText(
                        String.valueOf(stepNumber)
                );

                step.setTextColor(
                        Color.WHITE
                );

                step.setBackgroundResource(
                        R.drawable.bg_progress_active
                );

            } else {

                // שלב שעדיין לא הגיע
                step.setText(
                        String.valueOf(stepNumber)
                );

                step.setTextColor(
                        Color.rgb(
                                111,
                                123,
                                135
                        )
                );

                step.setBackgroundResource(
                        R.drawable.bg_progress_inactive
                );
            }
        }

        for (int i = 0; i < lineIds.length; i++) {

            View line =
                    view.findViewById(lineIds[i]);

            if (i + 1 < currentStep) {

                line.setBackgroundColor(
                        Color.rgb(
                                25,
                                118,
                                210
                        )
                );

            } else {

                line.setBackgroundColor(
                        Color.rgb(
                                213,
                                220,
                                229
                        )
                );
            }
        }

        TextView stepText =
                view.findViewById(
                        R.id.progressStepText
                );

        stepText.setText(
                "שלב "
                        + currentStep
                        + " מתוך 6"
        );
    }
}