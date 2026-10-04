        package com.example.synagogue;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    private EditText editEmail;
    private EditText editPassword;
    private Button buttonLogin;
    private TextView textError;

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        textError = view.findViewById(R.id.textLoginError);

        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(v -> login());
    }

    private void login() {

        String email = editEmail.getText()
                .toString()
                .trim()
                .toLowerCase();

        // לא עושים trim לסיסמה.
        // אם בעתיד תרצו לאפשר רווחים בסיסמה, הם צריכים להישמר כפי שהמשתמש הזין אותם.
        String password = editPassword.getText()
                .toString();

        hideError();

        // בדיקת אימייל
        if (TextUtils.isEmpty(email)) {
            showError("נא להזין כתובת אימייל");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("נא להזין כתובת אימייל תקינה");
            editEmail.requestFocus();
            return;
        }

        // בדיקת סיסמה
        if (TextUtils.isEmpty(password)) {
            showError("נא להזין סיסמה");
            editPassword.requestFocus();
            return;
        }

        setLoginEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {

                    setLoginEnabled(true);

                    if (!task.isSuccessful()) {
                        showFirebaseLoginError(task.getException());
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user == null) {
                        showError("אירעה שגיאה. לא ניתן לקבל את פרטי המשתמש.");
                        return;
                    }

                    // חשבון קיים אך האימייל עדיין לא אומת
                    if (!user.isEmailVerified()) {
                        showError(
                                "האימייל שלך עדיין לא אומת. " +
                                        "נא לבדוק את תיבת הדואר ולאשר את כתובת האימייל."
                        );
                        return;
                    }

                    Toast.makeText(
                            requireContext(),
                            "התחברת בהצלחה",
                            Toast.LENGTH_SHORT
                    ).show();

                    openMainScreen();
                });
    }

    /**
     * מציג הודעת שגיאה ידידותית לפי סוג השגיאה שקיבלנו מ-Firebase.
     */
    private void showFirebaseLoginError(Exception exception) {

        String message = "לא הצלחנו להתחבר. נא לבדוק את הפרטים ולנסות שוב.";

        if (exception != null) {

            String error = exception.getMessage();

            if (error != null) {

                if (error.contains("INVALID_LOGIN_CREDENTIALS")
                        || error.contains("INVALID_PASSWORD")
                        || error.contains("USER_NOT_FOUND")) {

                    message = "האימייל או הסיסמה אינם נכונים.";

                } else if (error.contains("TOO_MANY_ATTEMPTS")) {

                    message =
                            "בוצעו יותר מדי ניסיונות התחברות. " +
                                    "נא להמתין מעט ולנסות שוב.";

                } else if (error.contains("NETWORK_REQUEST_FAILED")) {

                    message =
                            "אין חיבור לאינטרנט. " +
                                    "נא לבדוק את החיבור ולנסות שוב.";

                } else if (error.contains("USER_DISABLED")) {

                    message =
                            "החשבון הזה אינו פעיל. " +
                                    "יש לפנות לתמיכה.";
                }
            }
        }

        showError(message);
    }

    /**
     * לאחר התחברות מוצלחת חוזרים למסך הראשי.
     *
     * בהמשך נחליף את זה ב-OwnerDashboardFragment
     * כאשר נבנה את מערכת ניהול בית הכנסת.
     */
    private void openMainScreen() {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        new UsersFragment()
                )
                .commit();
    }

    private void setLoginEnabled(boolean enabled) {

        buttonLogin.setEnabled(enabled);

        editEmail.setEnabled(enabled);
        editPassword.setEnabled(enabled);

        if (enabled) {
            buttonLogin.setText("התחברות");
        } else {
            buttonLogin.setText("מתחבר...");
        }
    }

    private void showError(String message) {

        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }

    private void hideError() {

        textError.setText("");
        textError.setVisibility(View.GONE);
    }
}
