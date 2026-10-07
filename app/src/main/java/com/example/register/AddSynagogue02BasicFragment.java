package com.example.register;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.example.drawerappsyn.MainActivity;

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
    public void onResume() {
        super.onResume();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(true);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupProgress(view, 4);

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

        view.findViewById(R.id.buttonNext)
                .setOnClickListener(v ->
                        continueToNextStep()
                );
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
         * חשוב:
         *
         * שומרים את כל הנתונים שהגיעו
         * מהשלבים הקודמים.
         *
         * כך ownerId / email / נתוני
         * אימות האימייל לא הולכים לאיבוד.
         */

        Bundle oldData = getArguments();

        Bundle data;

        if (oldData != null) {
            data = new Bundle(oldData);
        } else {
            data = new Bundle();
        }

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

        data.putBoolean(
                "basicDetailsCompleted",
                true
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