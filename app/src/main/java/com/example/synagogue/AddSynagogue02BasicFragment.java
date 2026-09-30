package com.example.synagogue;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;

public class AddSynagogue02BasicFragment extends Fragment {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextDescription;

    public AddSynagogue02BasicFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_02_basic,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        editTextName =
                view.findViewById(R.id.editTextSynagogueName);

        editTextPhone =
                view.findViewById(R.id.editTextSynagoguePhone);

        editTextDescription =
                view.findViewById(R.id.editTextSynagogueDescription);

        view.findViewById(R.id.buttonBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack()
                );



         view.findViewById(R.id.buttonNext).setOnClickListener(v -> {

            String name =
                    editTextName.getText().toString().trim();

            String phone =
                    editTextPhone.getText().toString().trim();

            String description =
                    editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                editTextName.setError("יש להזין את שם בית הכנסת");
                editTextName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(phone)) {
                editTextPhone.setError("יש להזין מספר טלפון");
                editTextPhone.requestFocus();
                return;
            }

            // יוצרים Bundle חדש עם נתוני ההרשמה
            Bundle data = new Bundle();

            data.putString(
                    "synagogueName",
                    name
            );

            data.putString(
                    "synagoguePhone",
                    phone
            );

            data.putString(
                    "synagogueDescription",
                    description
            );

            AddSynagogue03LocationFragment nextFragment =
                    new AddSynagogue03LocationFragment();

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
        });


    }

    private void continueToNextStep() {

        String name =
                editTextName.getText()
                        .toString()
                        .trim();

        String phone =
                editTextPhone.getText()
                        .toString()
                        .trim();

        String description =
                editTextDescription.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(name)) {

            editTextName.setError(
                    "יש להזין את שם בית הכנסת"
            );

            editTextName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {

            editTextPhone.setError(
                    "יש להזין מספר טלפון"
            );

            editTextPhone.requestFocus();
            return;
        }

        /*
         * אם הגענו לכאן מהמסך הראשון,
         * כבר יכול להיות Bundle קיים.
         *
         * אנחנו משתמשים ב-Bundle אחד וממשיכים
         * להעביר אותו בין כל שלבי ההרשמה.
         */
        Bundle data = getArguments();

        final Bundle registrationData;

        if (data == null) {
            registrationData = new Bundle();
        } else {
            registrationData = new Bundle(data);
        }

        /*
         * חשוב:
         * אלה השמות שהמסכים הבאים והמסך 06
         * מצפים לקבל.
         */
        registrationData.putString(
                "synagogueName",
                name
        );

        registrationData.putString(
                "synagoguePhone",
                phone
        );

        registrationData.putString(
                "synagogueDescription",
                description
        );

        AddSynagogue03LocationFragment nextFragment =
                new AddSynagogue03LocationFragment();

        nextFragment.setArguments(
                registrationData
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
}
