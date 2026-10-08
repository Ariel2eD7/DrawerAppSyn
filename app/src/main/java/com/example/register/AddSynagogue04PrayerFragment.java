package com.example.register;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.MainActivity;
import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;

public class AddSynagogue04PrayerFragment extends Fragment {

    // =========================================================
    // Main
    // =========================================================

    private LinearLayout prayerContainer;

    // =========================================================
    // Prayer Data
    //
    // A single list of rows.
    //
    // Each row:
    //
    // type    = normal / header
    // title   = prayer name / header
    // content = time
    //
    // =========================================================

    private final ArrayList<PrayerRow> prayerData =
            new ArrayList<>();

    // =========================================================
    // Constructor
    // =========================================================

    public AddSynagogue04PrayerFragment() {
        // Required empty public constructor
    }

    // =========================================================
    // Prayer Row Model
    // =========================================================

    private static class PrayerRow {

        String type;
        String title;
        String content;

        PrayerRow(
                String type,
                String title,
                String content) {

            this.type = type;
            this.title = title;
            this.content = content;
        }
    }

    // =========================================================
    // Create View
    // =========================================================

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

    // =========================================================
    // View Created
    // =========================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        setupProgress(
                view,
                4
        );

        prayerContainer =
                view.findViewById(
                        R.id.prayerContainer
                );

        MaterialButton buttonBack =
                view.findViewById(
                        R.id.buttonBack
                );

        MaterialButton buttonNext =
                view.findViewById(
                        R.id.buttonNext
                );

        // -----------------------------------------------------
        // Restore existing data if available
        // -----------------------------------------------------

        restorePrayerData();

        // -----------------------------------------------------
        // Bottom buttons
        // -----------------------------------------------------

        setupBottomButtons(
                view
        );

        // -----------------------------------------------------
        // Back
        // -----------------------------------------------------

        buttonBack.setOnClickListener(
                v ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack()
        );

        // -----------------------------------------------------
        // Next
        // -----------------------------------------------------

        buttonNext.setOnClickListener(
                v ->
                        continueToNextStep()
        );

        // -----------------------------------------------------
        // Render existing rows
        // -----------------------------------------------------

        renderAllRows();
    }

    // =========================================================
    // Restore Prayer Data
    // =========================================================

    @SuppressWarnings("unchecked")
    private void restorePrayerData() {

        prayerData.clear();

        Bundle arguments = getArguments();

        if (arguments == null) {
            return;
        }

        Object savedObject =
                arguments.getSerializable(
                        "prayerData"
                );

        if (!(savedObject instanceof ArrayList)) {
            return;
        }

        ArrayList<?> savedRows =
                (ArrayList<?>) savedObject;

        for (Object object : savedRows) {

            if (!(object instanceof HashMap)) {
                continue;
            }

            HashMap<?, ?> item =
                    (HashMap<?, ?>) object;

            String type =
                    item.get("type") instanceof String
                            ? (String) item.get("type")
                            : "normal";

            String title =
                    item.get("title") instanceof String
                            ? (String) item.get("title")
                            : "";

            String content =
                    item.get("content") instanceof String
                            ? (String) item.get("content")
                            : "";

            prayerData.add(
                    new PrayerRow(
                            type,
                            title,
                            content
                    )
            );
        }
    }

    // =========================================================
    // Bottom Buttons
    // =========================================================

    private void setupBottomButtons(
            View view) {

        MaterialButton addPrayer =
                view.findViewById(
                        R.id.buttonAddPrayer
                );

        MaterialButton addHeader =
                view.findViewById(
                        R.id.buttonAddHeader
                );

        if (addPrayer != null) {

            addPrayer.setOnClickListener(
                    v ->
                            addPrayerRow(
                                    "",
                                    ""
                            )
            );
        }

        if (addHeader != null) {

            addHeader.setOnClickListener(
                    v ->
                            addHeaderRow(
                                    ""
                            )
            );
        }
    }

    // =========================================================
    // Add Prayer Row
    // =========================================================

    private void addPrayerRow(
            String titleText,
            String contentText) {

        PrayerRow model =
                new PrayerRow(
                        "normal",
                        titleText,
                        contentText
                );

        prayerData.add(
                model
        );

        renderAllRows();

        focusLastRow();
    }

    // =========================================================
    // Add Header Row
    // =========================================================

    private void addHeaderRow(
            String textValue) {

        PrayerRow model =
                new PrayerRow(
                        "header",
                        textValue,
                        ""
                );

        prayerData.add(
                model
        );

        renderAllRows();

        focusLastRow();
    }

    // =========================================================
    // Render All Rows
    // =========================================================

    private void renderAllRows() {

        if (prayerContainer == null) {
            return;
        }

        prayerContainer.removeAllViews();

        for (PrayerRow model : prayerData) {

            renderRow(
                    model
            );
        }

        updateMoveButtons();
    }

    // =========================================================
    // Render Row
    // =========================================================

    private void renderRow(
            PrayerRow model) {

        LinearLayout row =
                createBaseRow();

        LinearLayout moves =
                createMoveButtons();

        EditText title =
                createEditText(
                        "תפילה / פעילות",
                        15
                );

        title.setText(
                model.title
        );

        title.setLayoutParams(
                createWeightParams()
        );

        if ("header".equals(
                model.type
        )) {

            title.setHint(
                    "כותרת / הפרדה"
            );

            title.setTextSize(
                    17
            );

            title.setTextColor(
                    Color.rgb(
                            40,
                            40,
                            40
                    )
            );

            title.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );

            title.setGravity(
                    Gravity.CENTER
            );
        }

        row.addView(
                moves
        );

        row.addView(
                title
        );

        EditText content = null;

        if ("normal".equals(
                model.type
        )) {

            content =
                    createEditText(
                            "שעה",
                            15
                    );

            content.setText(
                    model.content
            );

            content.setLayoutParams(
                    createWeightParams()
            );

            row.addView(
                    content
            );
        }

        MaterialButton delete =
                createButton(
                        "×",
                        Color.rgb(
                                198,
                                40,
                                40
                        ),
                        22
                );

        delete.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                )
        );

        row.addView(
                delete
        );

        // -----------------------------------------------------
        // Delete
        // -----------------------------------------------------

        delete.setOnClickListener(
                v -> {

                    prayerData.remove(
                            model
                    );

                    renderAllRows();
                }
        );

        // -----------------------------------------------------
        // Move Up
        // -----------------------------------------------------

        moves.getChildAt(0)
                .setOnClickListener(
                        v ->
                                moveRow(
                                        model,
                                        -1
                                )
                );

        // -----------------------------------------------------
        // Move Down
        // -----------------------------------------------------

        moves.getChildAt(1)
                .setOnClickListener(
                        v ->
                                moveRow(
                                        model,
                                        1
                                )
                );

        // -----------------------------------------------------
        // Title
        // -----------------------------------------------------

        title.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus) {

                        model.title =
                                title.getText()
                                        .toString()
                                        .trim();
                    }
                }
        );

        // -----------------------------------------------------
        // Content
        // -----------------------------------------------------

        if (content != null) {

            EditText finalContent =
                    content;

            finalContent.setOnFocusChangeListener(
                    (v, hasFocus) -> {

                        if (!hasFocus) {

                            model.content =
                                    finalContent
                                            .getText()
                                            .toString()
                                            .trim();
                        }
                    }
            );
        }

        prayerContainer.addView(
                row
        );
    }

    // =========================================================
    // Focus Last Row
    // =========================================================

    private void focusLastRow() {

        if (prayerContainer == null) {
            return;
        }

        int count =
                prayerContainer.getChildCount();

        if (count == 0) {
            return;
        }

        View last =
                prayerContainer.getChildAt(
                        count - 1
                );

        if (!(last instanceof LinearLayout)) {
            return;
        }

        LinearLayout row =
                (LinearLayout) last;

        if (row.getChildCount() < 2) {
            return;
        }

        View title =
                row.getChildAt(1);

        if (title instanceof EditText) {

            title.requestFocus();

            title.post(
                    () -> {

                        title.requestFocus();

                        if (title instanceof EditText) {

                            EditText editText =
                                    (EditText) title;

                            editText.setSelection(
                                    editText.length()
                            );
                        }
                    }
            );
        }
    }

    // =========================================================
    // Base Row
    // =========================================================

    private LinearLayout createBaseRow() {

        LinearLayout row =
                new LinearLayout(
                        requireContext()
                );

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setPadding(
                dp(8),
                dp(8),
                dp(8),
                dp(8)
        );

        row.setBackground(
                createRowBackground()
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
                dp(10)
        );

        row.setLayoutParams(
                params
        );

        return row;
    }

    // =========================================================
    // Background
    // =========================================================

    private GradientDrawable createRowBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.WHITE
        );

        drawable.setCornerRadius(
                dp(18)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(
                        225,
                        229,
                        234
                )
        );

        return drawable;
    }

    // =========================================================
    // Move Buttons
    // =========================================================

    private LinearLayout createMoveButtons() {

        LinearLayout moves =
                new LinearLayout(
                        requireContext()
                );

        moves.setOrientation(
                LinearLayout.HORIZONTAL
        );

        moves.setGravity(
                Gravity.CENTER
        );

        moves.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(92),
                        dp(48)
                )
        );

        MaterialButton up =
                createButton(
                        "▲",
                        Color.DKGRAY,
                        17
                );

        MaterialButton down =
                createButton(
                        "▼",
                        Color.DKGRAY,
                        17
                );

        up.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(44),
                        dp(46)
                )
        );

        down.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(44),
                        dp(46)
                )
        );

        moves.addView(
                up
        );

        moves.addView(
                down
        );

        return moves;
    }

    // =========================================================
    // Edit Text
    // =========================================================

    private EditText createEditText(
            String hint,
            float size) {

        EditText editText =
                new EditText(
                        requireContext()
                );

        editText.setHint(
                hint
        );

        editText.setTextSize(
                size
        );

        editText.setSingleLine(
                true
        );

        editText.setPadding(
                dp(8),
                0,
                dp(8),
                0
        );

        editText.setTextDirection(
                View.TEXT_DIRECTION_RTL
        );

        return editText;
    }

    // =========================================================
    // Button
    // =========================================================

    private MaterialButton createButton(
            String text,
            int color,
            float size) {

        MaterialButton button =
                new MaterialButton(
                        requireContext()
                );

        button.setText(
                text
        );

        button.setTextSize(
                size
        );

        button.setTextColor(
                color
        );

        button.setGravity(
                Gravity.CENTER
        );

        button.setPadding(
                0,
                0,
                0,
                0
        );

        button.setMinWidth(
                0
        );

        button.setMinimumWidth(
                0
        );

        button.setMinHeight(
                0
        );

        button.setMinimumHeight(
                0
        );

        button.setAllCaps(
                false
        );

        return button;
    }

    // =========================================================
    // Weight Params
    // =========================================================

    private LinearLayout.LayoutParams createWeightParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        dp(48),
                        1
                );

        params.setMargins(
                dp(3),
                0,
                dp(3),
                0
        );

        return params;
    }

    // =========================================================
    // Move Row
    // =========================================================

    private void moveRow(
            PrayerRow model,
            int direction) {

        int currentIndex =
                prayerData.indexOf(
                        model
                );

        if (currentIndex < 0) {
            return;
        }

        int newIndex =
                currentIndex + direction;

        if (newIndex < 0
                || newIndex >= prayerData.size()) {

            return;
        }

        prayerData.remove(
                currentIndex
        );

        prayerData.add(
                newIndex,
                model
        );

        renderAllRows();
    }

    // =========================================================
    // Update Move Buttons
    // =========================================================

    private void updateMoveButtons() {

        if (prayerContainer == null) {
            return;
        }

        int count =
                prayerContainer.getChildCount();

        for (int i = 0;
             i < count;
             i++) {

            View view =
                    prayerContainer.getChildAt(
                            i
                    );

            if (!(view instanceof LinearLayout)) {
                continue;
            }

            LinearLayout row =
                    (LinearLayout) view;

            if (row.getChildCount() == 0) {
                continue;
            }

            View first =
                    row.getChildAt(0);

            if (!(first instanceof LinearLayout)) {
                continue;
            }

            LinearLayout moves =
                    (LinearLayout) first;

            if (moves.getChildCount() < 2) {
                continue;
            }

            moves.getChildAt(0)
                    .setEnabled(
                            i > 0
                    );

            moves.getChildAt(1)
                    .setEnabled(
                            i < count - 1
                    );
        }
    }

    // =========================================================
    // Continue
    // =========================================================

    private void continueToNextStep() {

        // -----------------------------------------------------
        // First update current visible fields
        // -----------------------------------------------------

        syncVisibleRows();

        // -----------------------------------------------------
        // Copy previous Bundle
        // -----------------------------------------------------

        Bundle oldData =
                getArguments();

        final Bundle data;

        if (oldData == null) {

            data =
                    new Bundle();

        } else {

            data =
                    new Bundle(
                            oldData
                    );
        }

        // -----------------------------------------------------
        // Build prayer data
        //
        // list
        //   -> type
        //   -> title
        //   -> content
        // -----------------------------------------------------

        ArrayList<HashMap<String, String>>
                bundlePrayerData =
                new ArrayList<>();

        for (PrayerRow prayerRow :
                prayerData) {

            HashMap<String, String> item =
                    new HashMap<>();

            item.put(
                    "type",
                    prayerRow.type
            );

            item.put(
                    "title",
                    prayerRow.title == null
                            ? ""
                            : prayerRow.title
            );

            item.put(
                    "content",
                    prayerRow.content == null
                            ? ""
                            : prayerRow.content
            );

            bundlePrayerData.add(
                    item
            );
        }

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        data.putSerializable(
                "prayerData",
                bundlePrayerData
        );

        data.putBoolean(
                "prayersCompleted",
                true
        );

        // -----------------------------------------------------
        // Next fragment
        // -----------------------------------------------------

        AddSynagogue05FeaturesFragment nextFragment =
                new AddSynagogue05FeaturesFragment();

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

    // =========================================================
    // Sync Visible Rows
    // =========================================================

    private void syncVisibleRows() {

        if (prayerContainer == null) {
            return;
        }

        int childCount =
                prayerContainer.getChildCount();

        for (int i = 0;
             i < childCount
                     && i < prayerData.size();
             i++) {

            View view =
                    prayerContainer.getChildAt(
                            i
                    );

            if (!(view instanceof LinearLayout)) {
                continue;
            }

            LinearLayout row =
                    (LinearLayout) view;

            PrayerRow model =
                    prayerData.get(
                            i
                    );

            // -------------------------------------------------
            // Title
            // -------------------------------------------------

            if (row.getChildCount() >= 2) {

                View titleView =
                        row.getChildAt(1);

                if (titleView instanceof EditText) {

                    model.title =
                            ((EditText) titleView)
                                    .getText()
                                    .toString()
                                    .trim();
                }
            }

            // -------------------------------------------------
            // Content / Time
            // -------------------------------------------------

            if ("normal".equals(
                    model.type
            )) {

                if (row.getChildCount() >= 4) {

                    View contentView =
                            row.getChildAt(2);

                    if (contentView instanceof EditText) {

                        model.content =
                                ((EditText) contentView)
                                        .getText()
                                        .toString()
                                        .trim();
                    }
                }
            }
        }
    }

    // =========================================================
    // Progress
    // =========================================================

    private void setupProgress(
            View view,
            int currentStep) {

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

        // -----------------------------------------------------
        // Steps
        // -----------------------------------------------------

        for (int i = 0;
             i < stepIds.length;
             i++) {

            TextView step =
                    view.findViewById(
                            stepIds[i]
                    );

            if (step == null) {
                continue;
            }

            int stepNumber =
                    i + 1;

            if (stepNumber < currentStep) {

                step.setText(
                        "✓"
                );

                step.setTextColor(
                        Color.WHITE
                );

                step.setBackgroundResource(
                        R.drawable.bg_progress_completed
                );

            } else if (stepNumber == currentStep) {

                step.setText(
                        String.valueOf(
                                stepNumber
                        )
                );

                step.setTextColor(
                        Color.WHITE
                );

                step.setBackgroundResource(
                        R.drawable.bg_progress_active
                );

            } else {

                step.setText(
                        String.valueOf(
                                stepNumber
                        )
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

        // -----------------------------------------------------
        // Lines
        // -----------------------------------------------------

        for (int i = 0;
             i < lineIds.length;
             i++) {

            View line =
                    view.findViewById(
                            lineIds[i]
                    );

            if (line == null) {
                continue;
            }

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

        // -----------------------------------------------------
        // Step text
        // -----------------------------------------------------

        TextView stepText =
                view.findViewById(
                        R.id.progressStepText
                );

        if (stepText != null) {

            stepText.setText(
                    "שלב "
                            + currentStep
                            + " מתוך 6"
            );
        }
    }

    // =========================================================
    // DP
    // =========================================================

    private int dp(
            int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                value * density
        );
    }

    // =========================================================
    // Toolbar
    // =========================================================

    @Override
    public void onResume() {

        super.onResume();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(
                false
        );
    }

    @Override
    public void onPause() {

        super.onPause();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(
                true
        );
    }
}
