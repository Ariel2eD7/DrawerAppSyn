package com.example.synagogue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private EditText editEmail, editPassword;
    private Button buttonLogin;
    private TextView textError;
    private FirebaseAuth mAuth;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle b) {
        return i.inflate(R.layout.fragment_login, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        super.onViewCreated(v, b);

        editEmail = v.findViewById(R.id.editEmail);
        editPassword = v.findViewById(R.id.editPassword);
        buttonLogin = v.findViewById(R.id.buttonLogin);
        textError = v.findViewById(R.id.textLoginError);
        mAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(x -> login());
    }

    private void login() {
        String username = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        textError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(username)) {
            showError("נא להזין שם משתמש");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError("נא להזין סיסמה");
            return;
        }

        buttonLogin.setEnabled(false);
        mAuth.signInWithEmailAndPassword(
                username.toLowerCase() + "@myapp.local", password
        ).addOnCompleteListener(requireActivity(), task -> {
            buttonLogin.setEnabled(true);

            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "התחברת בהצלחה", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new EditSynagogueFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                showError("שם המשתמש או הסיסמה אינם נכונים");
            }
        });
    }

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }
}
