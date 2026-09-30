package com.example.synagogue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;

public class AddSynagogue02AccountFragment extends Fragment {

    private EditText editUsername;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editPhone;

    public AddSynagogue02AccountFragment() {
        // Required empty public constructor
    }

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

        editUsername =
                view.findViewById(R.id.editUsername);

        editPassword =
                view.findViewById(R.id.editPassword);

        editConfirmPassword =
                view.findViewById(R.id.editConfirmPassword);

        editPhone =
                view.findViewById(R.id.editPhone);

        MaterialButton buttonBack =
                view.findViewById(R.id.buttonBack);

        MaterialButton buttonNext =
                view.findViewById(R.id.buttonNext);

        buttonBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        buttonNext.setOnClickListener(v ->
                continueToVerification()
        );
    }

    private void continueToVerification() {

        String username =
                editUsername.getText()
                        .toString()
                        .trim();

        String password =
                editPassword.getText()
                        .toString();

        String confirmPassword =
                editConfirmPassword.getText()
                        .toString();

        String phone =
                editPhone.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(username)) {
            showError("נא להזין שם משתמש");
            return;
        }

        if (username.length() < 3) {
            showError("שם המשתמש חייב להכיל לפחות 3 תווים");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError("נא להזין סיסמה");
            return;
        }

        if (password.length() < 6) {
            showError("הסיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            showError("נא לאמת את הסיסמה");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("הסיסמאות אינן תואמות");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showError("נא להזין מספר טלפון");
            return;
        }

        Bundle data = getArguments();

        if (data == null) {
            data = new Bundle();
        }

        data.putBoolean(
                "accountStepCompleted",
                true
        );

        data.putString(
                "username",
                username
        );

        data.putString(
                "password",
                password
        );

        data.putString(
                "phone",
                phone
        );

        AddSynagogue03PhoneVerificationFragment nextFragment =
                new AddSynagogue03PhoneVerificationFragment();

        nextFragment.setArguments(data);

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

    private void showError(String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}

