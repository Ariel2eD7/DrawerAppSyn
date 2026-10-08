package com.example.register;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.example.drawerappsyn.MainActivity;

public class AddSynagogue04PrayerFragment extends Fragment {

    private LinearLayout prayerContainer;
    private TextView selectedDayTitle;

    private final String[] dayKeys = {
            "sunday",
            "monday",
            "tuesday",
            "wednesday",
            "thursday",
            "friday",
            "shabbat"
    };

    private final String[] dayNames = {
            "ראשון",
            "שני",
            "שלישי",
            "רביעי",
            "חמישי",
            "שישי",
            "שבת"
    };

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

    private int selectedDay = 0;

    /*
     * מבנה זמני של שעות התפילה.
     *
     * day -> prayer -> list of times
     *
     * לדוגמה:
     * sunday -> shacharit -> ["07:00", "08:30"]
     */
    private final Map<String, Map<String, ArrayList<String>>> prayerData =
            new HashMap<>();

    public AddSynagogue04PrayerFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_04_prayer,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupProgress(view, 4);

        prayerContainer = view.findViewById(R.id.prayerContainer);
        selectedDayTitle = view.findViewById(R.id.selectedDayTitle);

        MaterialButton buttonBack =
                view.findViewById(R.id.buttonBack);

        MaterialButton buttonNext =
                view.findViewById(R.id.buttonNext);

        setupPrayerData();
        setupDayButtons(view);

        buttonBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        buttonNext.setOnClickListener(v ->
                continueToNextStep()
        );

        showSelectedDay();
    }

    private void setupPrayerData() {

        for (String day : dayKeys) {

            Map<String, ArrayList<String>> prayers =
                    new HashMap<>();

            prayers.put("shacharit", new ArrayList<>());
            prayers.put("mincha", new ArrayList<>());
            prayers.put("maariv", new ArrayList<>());
            prayers.put("kabbalatShabbat", new ArrayList<>());
            prayers.put("musaf", new ArrayList<>());
            prayers.put("havdalah", new ArrayList<>());

            prayerData.put(day, prayers);
        }
    }

    private void setupDayButtons(View view) {

        TextView[] buttons = {
                view.findViewById(R.id.daySunday),
                view.findViewById(R.id.dayMonday),
                view.findViewById(R.id.dayTuesday),
                view.findViewById(R.id.dayWednesday),
                view.findViewById(R.id.dayThursday),
                view.findViewById(R.id.dayFriday),
                view.findViewById(R.id.dayShabbat)
        };

        for (int i = 0; i < buttons.length; i++) {

            final int dayIndex = i;

            buttons[i].setOnClickListener(v -> {

                selectedDay = dayIndex;

                updateDaySelection(buttons);

                showSelectedDay();
            });
        }

        updateDaySelection(buttons);
    }

    private void updateDaySelection(TextView[] buttons) {

        for (int i = 0; i < buttons.length; i++) {

            if (i == selectedDay) {

                buttons[i].setBackgroundResource(
                        R.drawable.bg_day_selected
                );

                buttons[i].setTextColor(
                        getResources().getColor(
                                android.R.color.white
                        )
                );

            } else {

                buttons[i].setBackgroundResource(
                        R.drawable.bg_day_unselected
                );

                buttons[i].setTextColor(
                        getResources().getColor(
                                R.color.primary
                        )
                );
            }
        }
    }



    private void showSelectedDay() {

        prayerContainer.removeAllViews();

        String dayKey = dayKeys[selectedDay];

        selectedDayTitle.setText(
                "תפילות ביום " + dayNames[selectedDay]
        );

        addPrayerRow(
                "shacharit",
                "שחרית",
                true
        );

        addPrayerRow(
                "mincha",
                "מנחה",
                true
        );

        addPrayerRow(
                "maariv",
                "ערבית",
                true
        );

        if (selectedDay == 5 || selectedDay == 6) {

            addPrayerRow(
                    "kabbalatShabbat",
                    "קבלת שבת",
                    false
            );

            addPrayerRow(
                    "musaf",
                    "מוסף",
                    false
            );

            addPrayerRow(
                    "havdalah",
                    "הבדלה",
                    false
            );
        }

        // ==========================================
        // כפתורי הוספת שעה - תמיד מתחת לכל הכרטיסים
        // ==========================================

        addBottomAddButton(
                "shacharit",
                "שחרית"
        );

        addBottomAddButton(
                "mincha",
                "מנחה"
        );

        addBottomAddButton(
                "maariv",
                "ערבית"
        );

        if (selectedDay == 5 || selectedDay == 6) {

            addBottomAddButton(
                    "kabbalatShabbat",
                    "קבלת שבת"
            );

            addBottomAddButton(
                    "musaf",
                    "מוסף"
            );

            addBottomAddButton(
                    "havdalah",
                    "הבדלה"
            );
        }
    }



    private void addBottomAddButton(
            String prayerKey,
            String prayerName) {

        TextView addButton =
                new TextView(requireContext());

        addButton.setText(
                "+ הוסף שעה ל" + prayerName
        );

        addButton.setTextSize(15);

        addButton.setTextColor(
                getResources().getColor(
                        R.color.primary
                )
        );

        addButton.setGravity(
                android.view.Gravity.CENTER
        );

        addButton.setPadding(
                12,
                14,
                12,
                14
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                0,
                0,
                10
        );

        addButton.setLayoutParams(params);

        addButton.setOnClickListener(v -> {

            String dayKey =
                    dayKeys[selectedDay];

            ArrayList<String> times =
                    prayerData
                            .get(dayKey)
                            .get(prayerKey);

            showTimePicker(
                    null,
                    times
            );
        });

        prayerContainer.addView(addButton);
    }



    private void addPrayerRow(
            String prayerKey,
            String prayerName,
            boolean regularPrayer) {

        String dayKey = dayKeys[selectedDay];

        ArrayList<String> times =
                prayerData
                        .get(dayKey)
                        .get(prayerKey);

        MaterialCardView card =
                new MaterialCardView(requireContext());

        card.setRadius(20);
        card.setCardElevation(2);
        card.setUseCompatPadding(true);

        LinearLayout layout =
                new LinearLayout(requireContext());

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                20,
                18,
                20,
                18
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                14
        );

        card.setLayoutParams(cardParams);

        LinearLayout titleRow =
                new LinearLayout(requireContext());

        titleRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        titleRow.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        CheckBox checkBox =
                new CheckBox(requireContext());

        checkBox.setText(prayerName);
        checkBox.setTextSize(18);
        checkBox.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        checkBox.setChecked(
                !times.isEmpty()
        );

        titleRow.addView(
                checkBox,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );






        layout.addView(titleRow);

        LinearLayout timesContainer =
                new LinearLayout(requireContext());

        timesContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.addView(timesContainer);

        for (String time : times) {
            addTimeRow(
                    timesContainer,
                    times,
                    time
            );
        }

        checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (!isChecked) {

                        times.clear();
                        timesContainer.removeAllViews();

                    } else if (times.isEmpty()) {

                        showTimePicker(
                                timesContainer,
                                times
                        );
                    }
                }
        );



        card.addView(layout);

        prayerContainer.addView(card);
    }

    private void showTimePicker(
            LinearLayout container,
            ArrayList<String> times) {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(Calendar.HOUR_OF_DAY);

        int minute =
                calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog =
                new TimePickerDialog(
                        requireContext(),
                        (view, selectedHour, selectedMinute) -> {

                            String time =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                    );

                            if (!times.contains(time)) {

                                times.add(time);

                                showSelectedDay();
                            }

                        },
                        hour,
                        minute,
                        true
                );

        dialog.show();
    }

    private void addTimeRow(
            LinearLayout container,
            ArrayList<String> times,
            String time) {

        LinearLayout row =
                new LinearLayout(requireContext());

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        TextView timeText =
                new TextView(requireContext());

        timeText.setText("🕐  " + time);
        timeText.setTextSize(17);
        timeText.setTextColor(
                getResources().getColor(
                        android.R.color.black
                )
        );

        row.addView(
                timeText,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView delete =
                new TextView(requireContext());

        delete.setText("✕");
        delete.setTextSize(20);
        delete.setTextColor(
                getResources().getColor(
                        android.R.color.holo_red_dark
                )
        );

        delete.setPadding(
                20,
                8,
                8,
                8
        );

        row.addView(delete);

        delete.setOnClickListener(v -> {

            times.remove(time);

            showSelectedDay();
        });

        container.addView(row);
    }





    private void continueToNextStep() {

        /*
         * לוקחים את הנתונים שהגיעו מהמסכים הקודמים.
         */
        Bundle oldData = getArguments();

        /*
         * יוצרים Bundle חדש כדי לא לשנות ישירות
         * את ה-Bundle של המסך הקודם.
         */
        final Bundle data;

        if (oldData == null) {
            data = new Bundle();
        } else {
            data = new Bundle(oldData);
        }

        /*
         * ==========================================
         * שמירת נתוני התפילות
         * ==========================================
         *
         * המבנה:
         *
         * day
         *   -> prayer
         *       -> list of times
         */

        HashMap<String, HashMap<String, ArrayList<String>>>
                bundlePrayerData =
                new HashMap<>();

        for (Map.Entry<String, Map<String, ArrayList<String>>> dayEntry
                : prayerData.entrySet()) {

            HashMap<String, ArrayList<String>> prayers =
                    new HashMap<>();

            for (Map.Entry<String, ArrayList<String>> prayerEntry
                    : dayEntry.getValue().entrySet()) {

                prayers.put(
                        prayerEntry.getKey(),
                        new ArrayList<>(
                                prayerEntry.getValue()
                        )
                );
            }

            bundlePrayerData.put(
                    dayEntry.getKey(),
                    prayers
            );
        }

        data.putSerializable(
                "prayerData",
                bundlePrayerData
        );

        data.putBoolean(
                "prayersCompleted",
                true
        );

        /*
         * ==========================================
         * מעבר למסך המאפיינים
         * ==========================================
         */

        AddSynagogue05FeaturesFragment nextFragment =
                new AddSynagogue05FeaturesFragment();

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
