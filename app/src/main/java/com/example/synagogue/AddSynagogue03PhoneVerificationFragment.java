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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.TimeUnit;

public class AddSynagogue03PhoneVerificationFragment extends Fragment {

    private EditText editPhone;
    private EditText editCode;

    private MaterialButton buttonSendCode;
    private MaterialButton buttonVerify;

    private FirebaseAuth auth;

    private String verificationId;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    public AddSynagogue03PhoneVerificationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_03_phone_verification,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        editPhone =
                view.findViewById(R.id.editPhone);

        editCode =
                view.findViewById(R.id.editCode);

        buttonSendCode =
                view.findViewById(R.id.buttonSendCode);

        buttonVerify =
                view.findViewById(R.id.buttonVerify);

        auth =
                FirebaseAuth.getInstance();

        editCode.setVisibility(View.GONE);
        buttonVerify.setVisibility(View.GONE);

        buttonSendCode.setOnClickListener(v ->
                sendVerificationCode()
        );

        buttonVerify.setOnClickListener(v ->
                verifyCode()
        );
    }

    private void sendVerificationCode() {

        String phone =
                editPhone.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(phone)) {

            showError(
                    "יש להזין מספר טלפון"
            );

            return;
        }

        /*
         * Firebase מצפה למספר בפורמט בינלאומי.
         *
         * לדוגמה:
         * 0501234567
         *
         * יהפוך ל:
         * +972501234567
         */

        phone = normalizeIsraeliPhone(phone);

        if (phone == null) {

            showError(
                    "מספר הטלפון אינו תקין"
            );

            return;
        }

        buttonSendCode.setEnabled(false);

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(
                                60L,
                                TimeUnit.SECONDS
                        )
                        .setActivity(
                                requireActivity()
                        )
                        .setCallbacks(
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(
                                            @NonNull PhoneAuthCredential credential) {

                                        /*
                                         * במקרים מסוימים Firebase
                                         * יכול לבצע אימות אוטומטי.
                                         */

                                        signInWithCredential(
                                                credential
                                        );
                                    }

                                    @Override
                                    public void onVerificationFailed(
                                            @NonNull FirebaseException e) {

                                        buttonSendCode.setEnabled(true);

                                        showError(
                                                "שליחת קוד האימות נכשלה:\n"
                                                        + e.getMessage()
                                        );
                                    }

                                    @Override
                                    public void onCodeSent(
                                            @NonNull String id,
                                            @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                        verificationId = id;

                                        resendToken = token;

                                        buttonSendCode.setEnabled(true);

                                        editCode.setVisibility(
                                                View.VISIBLE
                                        );

                                        buttonVerify.setVisibility(
                                                View.VISIBLE
                                        );

                                        Toast.makeText(
                                                requireContext(),
                                                "קוד אימות נשלח לטלפון",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        )
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(
                options
        );
    }

    private void verifyCode() {

        String code =
                editCode.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(code)) {

            showError(
                    "יש להזין את קוד האימות"
            );

            return;
        }

        if (verificationId == null) {

            showError(
                    "יש לשלוח קוד אימות קודם"
            );

            return;
        }

        buttonVerify.setEnabled(false);

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        code
                );

        signInWithCredential(
                credential
        );
    }

    private void signInWithCredential(
            PhoneAuthCredential credential) {

        auth.signInWithCredential(
                credential
        ).addOnCompleteListener(
                requireActivity(),
                task -> {

                    if (!task.isSuccessful()) {

                        buttonVerify.setEnabled(true);

                        String message =
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "קוד האימות שגוי";

                        showError(
                                "האימות נכשל:\n"
                                        + message
                        );

                        return;
                    }

                    goToBasicDetails();
                }
        );
    }



     private void goToBasicDetails() {

        Bundle data = getArguments();

        if (data == null) {
            data = new Bundle();
        }

        data.putBoolean(
                "phoneVerified",
                true
        );

        AddSynagogue02BasicFragment nextFragment =
                new AddSynagogue02BasicFragment();

        nextFragment.setArguments(
                data
        );

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





   private String normalizeIsraeliPhone(
            String phone) {

        phone =
                phone.replace(
                        " ",
                        ""
                );

        phone =
                phone.replace(
                        "-",
                        ""
                );

        /*
         * 0501234567
         * ->
         * +972501234567
         */

        if (phone.startsWith("0")) {

            phone =
                    "+972"
                            + phone.substring(1);
        }

        /*
         * 972501234567
         * ->
         * +972501234567
         */

        if (phone.startsWith("972")) {

            phone =
                    "+"
                            + phone;
        }

        if (!phone.startsWith("+972")) {
            return null;
        }

        if (phone.length() != 13) {
            return null;
        }

        return phone;
    }

    private void showError(
            String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}














