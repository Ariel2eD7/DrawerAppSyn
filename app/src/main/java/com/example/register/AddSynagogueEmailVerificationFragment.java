package com.example.register;

import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddSynagogueEmailVerificationFragment
        extends Fragment {

    private FirebaseAuth auth;

    private TextView emailText;

    private MaterialButton checkButton;
    private MaterialButton resendButton;
    private MaterialButton backButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_email_verification,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupProgress(view, 3);

        auth = FirebaseAuth.getInstance();

        emailText =
                view.findViewById(R.id.emailText);

        checkButton =
                view.findViewById(R.id.checkButton);

        resendButton =
                view.findViewById(R.id.resendButton);

        backButton =
                view.findViewById(R.id.backButton);

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null &&
                user.getEmail() != null) {

            emailText.setText(
                    user.getEmail()
            );
        }

        checkButton.setOnClickListener(v ->
                checkEmailVerification()
        );

        resendButton.setOnClickListener(v ->
                resendVerificationEmail()
        );

        backButton.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }

    private void checkEmailVerification() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "לא נמצא חשבון פעיל."
            );

            return;
        }

        checkButton.setEnabled(false);
        checkButton.setText("בודק...");

        user.reload()
                .addOnCompleteListener(task -> {

                    checkButton.setEnabled(true);
                    checkButton.setText(
                            "✓ בדקתי את האימייל"
                    );

                    if (!task.isSuccessful()) {

                        showMessage(
                                "לא הצלחנו לבדוק את האימות. נסו שוב."
                        );

                        return;
                    }

                    FirebaseUser refreshedUser =
                            auth.getCurrentUser();

                    if (refreshedUser != null &&
                            refreshedUser.isEmailVerified()) {

                        continueToNextStep();

                    } else {

                        showMessage(
                                "עדיין לא זיהינו אימות. פתחו את האימייל ולחצו על קישור האימות."
                        );
                    }
                });
    }

    private void resendVerificationEmail() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "לא נמצא חשבון פעיל."
            );

            return;
        }

        resendButton.setEnabled(false);
        resendButton.setText("שולח...");

        user.sendEmailVerification()
                .addOnCompleteListener(task -> {

                    resendButton.setEnabled(true);
                    resendButton.setText(
                            "שלח שוב"
                    );

                    if (task.isSuccessful()) {

                        showMessage(
                                "אימייל אימות חדש נשלח."
                        );

                    } else {

                        showMessage(
                                "לא הצלחנו לשלוח את האימייל. נסו שוב."
                        );
                    }
                });
    }

    private void continueToNextStep() {

        Bundle bundle = getArguments();

        if (bundle == null) {
            bundle = new Bundle();
        }

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null) {

            bundle.putString(
                    "ownerId",
                    user.getUid()
            );

            bundle.putString(
                    "email",
                    user.getEmail()
            );
        }

        AddSynagogue02BasicFragment nextFragment =
                new AddSynagogue02BasicFragment();

        nextFragment.setArguments(bundle);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        nextFragment
                )
                .addToBackStack(null)
                .commit();
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