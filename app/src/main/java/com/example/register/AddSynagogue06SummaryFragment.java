package com.example.register;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.MainActivity;
import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddSynagogue06SummaryFragment extends Fragment {

    // =========================================================
    // Views
    // =========================================================

    private TextView summaryText;

    private MaterialButton buttonBack;
    private MaterialButton buttonSave;

    // =========================================================
    // Firebase
    // =========================================================

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // =========================================================
    // Constructor
    // =========================================================

    public AddSynagogue06SummaryFragment() {
        // Required empty public constructor
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
                R.layout.fragment_add_synagogue_06_summary,
                container,
                false
        );
    }

    // =========================================================
    // Resume
    // =========================================================

    @Override
    public void onResume() {

        super.onResume();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(false);
    }

    // =========================================================
    // Pause
    // =========================================================

    @Override
    public void onPause() {

        super.onPause();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(true);
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

        // -----------------------------------------------------
        // Progress
        // -----------------------------------------------------

        setupProgress(
                view,
                6
        );

        // -----------------------------------------------------
        // Views
        // -----------------------------------------------------

        summaryText =
                view.findViewById(
                        R.id.summaryText
                );

        buttonBack =
                view.findViewById(
                        R.id.buttonBack
                );

        buttonSave =
                view.findViewById(
                        R.id.buttonSave
                );

        // -----------------------------------------------------
        // Firebase
        // -----------------------------------------------------

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        // -----------------------------------------------------
        // Summary
        // -----------------------------------------------------

        showSummary();

        // -----------------------------------------------------
        // Back
        // -----------------------------------------------------

        if (buttonBack != null) {

            buttonBack.setOnClickListener(
                    v -> {

                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                    }
            );
        }

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        if (buttonSave != null) {

            buttonSave.setOnClickListener(
                    v ->
                            saveSynagogue()
            );
        }
    }

    // =========================================================
    // Show Summary
    // =========================================================

    private void showSummary() {

        if (summaryText == null) {
            return;
        }

        Bundle data =
                getArguments();

        if (data == null) {

            summaryText.setText(
                    "לא נמצאו נתונים להרשמה."
            );

            if (buttonSave != null) {
                buttonSave.setEnabled(false);
            }

            return;
        }

        StringBuilder summary =
                new StringBuilder();

        // =====================================================
        // Basic Data
        // =====================================================

        String name =
                getStringValue(
                        data,
                        "synagogueName"
                );

        String description =
                getStringValue(
                        data,
                        "synagogueDescription"
                );

        String address =
                getStringValue(
                        data,
                        "synagogueAddress"
                );

        String phone =
                getStringValue(
                        data,
                        "synagoguePhone"
                );

        double latitude =
                data.getDouble(
                        "latitude",
                        0
                );

        double longitude =
                data.getDouble(
                        "longitude",
                        0
                );

        // =====================================================
        // Name
        // =====================================================

        appendSection(
                summary,
                "שם בית הכנסת",
                emptyOrValue(name)
        );

        // =====================================================
        // Description
        // =====================================================

        appendSection(
                summary,
                "תיאור",
                emptyOrValue(description)
        );

        // =====================================================
        // Address
        // =====================================================

        appendSection(
                summary,
                "כתובת",
                emptyOrValue(address)
        );

        // =====================================================
        // Phone
        // =====================================================

        appendSection(
                summary,
                "טלפון",
                emptyOrValue(phone)
        );

        // =====================================================
        // Location
        // =====================================================

        summary.append(
                "מיקום\n"
        );

        if (latitude != 0 || longitude != 0) {

            summary.append(
                    latitude
            );

            summary.append(
                    ", "
            );

            summary.append(
                    longitude
            );

        } else {

            summary.append(
                    "לא הוגדר"
            );
        }

        summary.append(
                "\n\n"
        );

        // =====================================================
        // Prayers
        // =====================================================

        summary.append(
                "תפילות\n"
        );

        appendPrayerSummary(
                summary,
                data
        );

        summary.append(
                "\n"
        );

        // =====================================================
        // Features
        // =====================================================

        summary.append(
                "מאפיינים\n"
        );

        appendFeaturesSummary(
                summary,
                data
        );

        // =====================================================
        // Set Text
        // =====================================================

        summaryText.setText(
                summary.toString().trim()
        );
    }

    // =========================================================
    // Append Section
    // =========================================================

    private void appendSection(
            StringBuilder summary,
            String title,
            String value) {

        summary.append(
                title
        );

        summary.append(
                "\n"
        );

        summary.append(
                value
        );

        summary.append(
                "\n\n"
        );
    }

    // =========================================================
    // Get String From Bundle
    // =========================================================

    private String getStringValue(
            Bundle data,
            String key) {

        if (data == null) {
            return "";
        }

        String value =
                data.getString(
                        key,
                        ""
                );

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    // =========================================================
    // Empty Value
    // =========================================================

    private String emptyOrValue(
            String value) {

        if (TextUtils.isEmpty(value)) {
            return "-";
        }

        return value;
    }

    // =========================================================
    // Prayer Summary
    //
    // Expected structure:
    //
    // prayerData
    //     ArrayList<HashMap<String,String>>
    //
    // Each row:
    //
    // type
    // title
    // content
    //
    // type:
    // normal
    // header
    // =========================================================

    private void appendPrayerSummary(
            StringBuilder summary,
            Bundle data) {

        Object prayerObject =
                data.getSerializable(
                        "prayerData"
                );

        if (!(prayerObject instanceof ArrayList)) {

            summary.append(
                    "לא הוגדרו תפילות\n"
            );

            return;
        }

        ArrayList<?> rows =
                (ArrayList<?>) prayerObject;

        if (rows.isEmpty()) {

            summary.append(
                    "לא הוגדרו תפילות\n"
            );

            return;
        }

        boolean foundAny =
                false;

        for (Object rowObject :
                rows) {

            if (!(rowObject instanceof Map)) {
                continue;
            }

            Map<?, ?> row =
                    (Map<?, ?>) rowObject;

            String type =
                    valueFromMap(
                            row,
                            "type"
                    );

            String title =
                    valueFromMap(
                            row,
                            "title"
                    );

            String content =
                    valueFromMap(
                            row,
                            "content"
                    );

            // =================================================
            // Header
            // =================================================

            if ("header".equalsIgnoreCase(type)) {

                if (TextUtils.isEmpty(title)) {
                    continue;
                }

                if (foundAny) {

                    summary.append(
                            "\n"
                    );
                }

                summary.append(
                        "• "
                );

                summary.append(
                        title
                );

                summary.append(
                        "\n"
                );

                foundAny =
                        true;

                continue;
            }

            // =================================================
            // Normal Prayer
            // =================================================

            if (TextUtils.isEmpty(title)
                    && TextUtils.isEmpty(content)) {

                continue;
            }

            if (foundAny) {

                summary.append(
                        "\n"
                );
            }

            if (!TextUtils.isEmpty(title)) {

                summary.append(
                        title
                );
            }

            if (!TextUtils.isEmpty(content)) {

                if (!TextUtils.isEmpty(title)) {

                    summary.append(
                            ": "
                    );
                }

                summary.append(
                        content
                );
            }

            summary.append(
                    "\n"
            );

            foundAny =
                    true;
        }

        if (!foundAny) {

            summary.append(
                    "לא הוגדרו תפילות\n"
            );
        }
    }

    // =========================================================
    // Read Map Value
    // =========================================================

    private String valueFromMap(
            Map<?, ?> map,
            String key) {

        if (map == null) {
            return "";
        }

        Object value =
                map.get(
                        key
                );

        if (value == null) {
            return "";
        }

        return String.valueOf(
                value
        ).trim();
    }

    // =========================================================
    // Features Summary
    // =========================================================

    private void appendFeaturesSummary(
            StringBuilder summary,
            Bundle data) {

        ArrayList<String> selected =
                data.getStringArrayList(
                        "synagogueFeatures"
                );

        if (selected == null
                || selected.isEmpty()) {

            summary.append(
                    "לא נבחרו מאפיינים\n"
            );

            return;
        }

        Map<String, String> names =
                new HashMap<>();

        names.put(
                "womenSection",
                "עזרת נשים"
        );

        names.put(
                "wheelchairAccess",
                "נגישות לכיסאות גלגלים"
        );

        names.put(
                "parking",
                "חניה"
        );

        names.put(
                "airConditioning",
                "מיזוג"
        );

        names.put(
                "heating",
                "חימום"
        );

        names.put(
                "mikveh",
                "מקווה"
        );

        names.put(
                "torahLessons",
                "שיעורי תורה"
        );

        names.put(
                "childrenActivities",
                "פעילות לילדים"
        );

        names.put(
                "onlineBroadcast",
                "שידורים ושיעורים אונליין"
        );

        names.put(
                "library",
                "ספרייה / ספרי קודש"
        );

        names.put(
                "kiddush",
                "קידוש"
        );

        names.put(
                "security",
                "אבטחה"
        );

        boolean foundAny =
                false;

        for (String key :
                selected) {

            if (TextUtils.isEmpty(key)) {
                continue;
            }

            String name =
                    names.get(
                            key
                    );

            if (TextUtils.isEmpty(name)) {

                name =
                        key;
            }

            summary.append(
                    "• "
            );

            summary.append(
                    name
            );

            summary.append(
                    "\n"
            );

            foundAny =
                    true;
        }

        if (!foundAny) {

            summary.append(
                    "לא נבחרו מאפיינים\n"
            );
        }
    }

    // =========================================================
    // Save Synagogue
    // =========================================================

    private void saveSynagogue() {

        Bundle data =
                getArguments();

        if (data == null) {

            showError(
                    "לא נמצאו נתוני הרשמה."
            );

            return;
        }

        // =====================================================
        // Required Fields
        // =====================================================

        String name =
                getStringValue(
                        data,
                        "synagogueName"
                );

        String address =
                getStringValue(
                        data,
                        "synagogueAddress"
                );

        if (TextUtils.isEmpty(name)) {

            showError(
                    "חסר שם בית הכנסת."
            );

            return;
        }

        if (TextUtils.isEmpty(address)) {

            showError(
                    "חסרה כתובת."
            );

            return;
        }

        // =====================================================
        // Firebase User
        // =====================================================

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            showError(
                    "לא נמצא חשבון מחובר. נא להתחבר מחדש."
            );

            return;
        }

        // =====================================================
        // Email Verification
        // =====================================================

        if (!currentUser.isEmailVerified()) {

            showError(
                    "יש לאמת את כתובת האימייל לפני שמירת בית הכנסת."
            );

            return;
        }

        // =====================================================
        // Disable Buttons
        // =====================================================

        setSavingState(
                true
        );

        // =====================================================
        // Save
        // =====================================================

        saveToFirestore(
                currentUser.getUid(),
                data
        );
    }

    // =========================================================
    // Saving State
    // =========================================================

    private void setSavingState(
            boolean saving) {

        if (buttonSave != null) {

            buttonSave.setEnabled(
                    !saving
            );

            if (saving) {

                buttonSave.setText(
                        "שומר..."
                );

            } else {

                buttonSave.setText(
                        "שמור"
                );
            }
        }

        if (buttonBack != null) {

            buttonBack.setEnabled(
                    !saving
            );
        }
    }

    // =========================================================
    // Save To Firestore
    // =========================================================

    private void saveToFirestore(
            String uid,
            Bundle data) {

        Map<String, Object> synagogue =
                new HashMap<>();

        // =====================================================
        // Current User
        // =====================================================

        FirebaseUser currentUser =
                auth.getCurrentUser();

        String email =
                "";

        if (currentUser != null
                && currentUser.getEmail() != null) {

            email =
                    currentUser.getEmail().trim();
        }

        // =====================================================
        // Owner
        // =====================================================

        synagogue.put(
                "ownerId",
                uid
        );

        synagogue.put(
                "ownerEmail",
                email
        );

        // =====================================================
        // Basic Information
        // =====================================================

        synagogue.put(
                "name",
                getStringValue(
                        data,
                        "synagogueName"
                )
        );

        synagogue.put(
                "address",
                getStringValue(
                        data,
                        "synagogueAddress"
                )
        );

        synagogue.put(
                "phone",
                getStringValue(
                        data,
                        "synagoguePhone"
                )
        );

        synagogue.put(
                "description",
                getStringValue(
                        data,
                        "synagogueDescription"
                )
        );

        // =====================================================
        // Location
        // =====================================================

        synagogue.put(
                "latitude",
                data.getDouble(
                        "latitude",
                        0
                )
        );

        synagogue.put(
                "longitude",
                data.getDouble(
                        "longitude",
                        0
                )
        );

        // =====================================================
        // Prayers
        // =====================================================

        synagogue.put(
                "prayers",
                buildPrayerData(
                        data
                )
        );

        // =====================================================
        // Features
        // =====================================================

        synagogue.put(
                "features",
                buildFeatures(
                        data
                )
        );

        // =====================================================
        // Status
        // =====================================================

        synagogue.put(
                "status",
                "active"
        );

        // =====================================================
        // Timestamps
        // =====================================================

        synagogue.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        synagogue.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );

        // =====================================================
        // Firestore
        // =====================================================

        db.collection(
                        "synagogues_v2"
                )
                .document(
                        uid
                )
                .set(
                        synagogue
                )
                .addOnSuccessListener(
                        unused -> {

                            if (!isAdded()) {
                                return;
                            }

                            Toast.makeText(
                                    requireContext(),
                                    "בית הכנסת נשמר בהצלחה!",
                                    Toast.LENGTH_LONG
                            ).show();

                            finishRegistration();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            if (!isAdded()) {
                                return;
                            }

                            setSavingState(
                                    false
                            );

                            String error =
                                    e.getMessage();

                            if (TextUtils.isEmpty(error)) {

                                error =
                                        "שגיאה לא ידועה";
                            }

                            showError(
                                    "שמירת בית הכנסת נכשלה:\n"
                                            + error
                            );
                        }
                );
    }

    // =========================================================
    // Build Prayer Data
    //
    // Input:
    //
    // ArrayList<HashMap<String,String>>
    //
    // Output:
    //
    // ArrayList<Map<String,Object>>
    //
    // Firestore compatible.
    // =========================================================

    private ArrayList<Map<String, Object>> buildPrayerData(
            Bundle data) {

        ArrayList<Map<String, Object>> result =
                new ArrayList<>();

        if (data == null) {
            return result;
        }

        Object prayerObject =
                data.getSerializable(
                        "prayerData"
                );

        if (!(prayerObject instanceof ArrayList)) {
            return result;
        }

        ArrayList<?> originalRows =
                (ArrayList<?>) prayerObject;

        for (Object rowObject :
                originalRows) {

            if (!(rowObject instanceof Map)) {
                continue;
            }

            Map<?, ?> originalRow =
                    (Map<?, ?>) rowObject;

            String type =
                    valueFromMap(
                            originalRow,
                            "type"
                    );

            String title =
                    valueFromMap(
                            originalRow,
                            "title"
                    );

            String content =
                    valueFromMap(
                            originalRow,
                            "content"
                    );

            // -------------------------------------------------
            // Ignore completely empty rows
            // -------------------------------------------------

            if (TextUtils.isEmpty(type)
                    && TextUtils.isEmpty(title)
                    && TextUtils.isEmpty(content)) {

                continue;
            }

            // -------------------------------------------------
            // Normalize type
            // -------------------------------------------------

            if (!"header".equalsIgnoreCase(type)
                    && !"normal".equalsIgnoreCase(type)) {

                type =
                        "normal";
            }

            Map<String, Object> row =
                    new HashMap<>();

            row.put(
                    "type",
                    type
            );

            row.put(
                    "title",
                    title
            );

            row.put(
                    "content",
                    content
            );

            result.add(
                    row
            );
        }

        return result;
    }

    // =========================================================
    // Build Features
    //
    // Saves ALL known feature keys as true/false.
    // =========================================================

    private Map<String, Boolean> buildFeatures(
            Bundle data) {

        Map<String, Boolean> result =
                new HashMap<>();

        ArrayList<String> selected =
                data.getStringArrayList(
                        "synagogueFeatures"
                );

        String[] keys = {

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

        for (String key :
                keys) {

            boolean enabled =
                    selected != null
                            && selected.contains(
                            key
                    );

            result.put(
                    key,
                    enabled
            );
        }

        return result;
    }

    // =========================================================
    // Finish Registration
    // =========================================================

    private void finishRegistration() {

        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager
                                .POP_BACK_STACK_INCLUSIVE
                );
    }

    // =========================================================
    // Error
    // =========================================================

    private void showError(
            String message) {

        if (!isAdded()) {
            return;
        }

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
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

        // =====================================================
        // Steps
        // =====================================================

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

            // -------------------------------------------------
            // Completed
            // -------------------------------------------------

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

                // -------------------------------------------------
                // Active
                // -------------------------------------------------

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

                // -------------------------------------------------
                // Inactive
                // -------------------------------------------------

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

        // =====================================================
        // Lines
        // =====================================================

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

        // =====================================================
        // Step Text
        // =====================================================

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
}
