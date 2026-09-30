package com.example.synagogue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSynagogue05FeaturesFragment extends Fragment {

    private LinearLayout featuresContainer;

    private final Map<String, Boolean> features =
            new HashMap<>();

    private final String[] featureKeys = {
            "womenSection",
            "wheelchairAccess",
            "parking",
            "airConditioning",
            "heating",
            "mikveh",
            "torahLessons",
            "childrenActivities",
            "onlineBroadcast",
            "library",
            "kiddush",
            "security"
    };

    private final String[] featureNames = {
            "עזרת נשים",
            "נגישות לכיסאות גלגלים",
            "חניה",
            "מיזוג",
            "חימום",
            "מקווה",
            "שיעורי תורה",
            "פעילות לילדים",
            "שידורים ושיעורים אונליין",
            "ספרייה / ספרי קודש",
            "קידוש",
            "אבטחה"
    };

    public AddSynagogue05FeaturesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_05_features,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        featuresContainer =
                view.findViewById(R.id.featuresContainer);

        MaterialButton buttonBack =
                view.findViewById(R.id.buttonBack);

        MaterialButton buttonNext =
                view.findViewById(R.id.buttonNext);

        initializeFeatures();
        buildFeatureCards();

        buttonBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        buttonNext.setOnClickListener(v ->
                continueToNextStep()
        );
    }

    private void initializeFeatures() {

        for (String key : featureKeys) {
            features.put(key, false);
        }

        Bundle data = getArguments();

        if (data == null) {
            return;
        }

        ArrayList<String> savedFeatures =
                data.getStringArrayList(
                        "synagogueFeatures"
                );

        if (savedFeatures == null) {
            return;
        }

        for (String key : savedFeatures) {
            if (features.containsKey(key)) {
                features.put(key, true);
            }
        }
    }

    private void buildFeatureCards() {

        featuresContainer.removeAllViews();

        for (int i = 0;
             i < featureKeys.length;
             i++) {

            addFeatureCard(
                    featureKeys[i],
                    featureNames[i]
            );
        }
    }

    private void addFeatureCard(
            String key,
            String name) {

        MaterialCardView card =
                new MaterialCardView(requireContext());

        card.setRadius(20);
        card.setCardElevation(2);
        card.setUseCompatPadding(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                12
        );

        card.setLayoutParams(cardParams);

        CheckBox checkBox =
                new CheckBox(requireContext());

        checkBox.setText(name);
        checkBox.setTextSize(17);

        checkBox.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        checkBox.setPadding(
                16,
                14,
                16,
                14
        );

        Boolean selected =
                features.get(key);

        checkBox.setChecked(
                selected != null && selected
        );

        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        features.put(
                                key,
                                isChecked
                        )
        );

        card.addView(checkBox);

        featuresContainer.addView(card);
    }

    private void continueToNextStep() {

        ArrayList<String> selectedFeatures =
                new ArrayList<>();

        for (String key : featureKeys) {

            Boolean selected =
                    features.get(key);

            if (selected != null && selected) {
                selectedFeatures.add(key);
            }
        }

        /*
         * משמרים את כל הנתונים שהגיעו
         * מהשלבים הקודמים.
         */
        Bundle oldData = getArguments();

        Bundle data;

        if (oldData != null) {
            data = new Bundle(oldData);
        } else {
            data = new Bundle();
        }

        /*
         * שומרים את המאפיינים שנבחרו.
         */
        data.putStringArrayList(
                "synagogueFeatures",
                selectedFeatures
        );

        data.putBoolean(
                "featuresCompleted",
                true
        );

        AddSynagogue06SummaryFragment nextFragment =
                new AddSynagogue06SummaryFragment();

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
}
