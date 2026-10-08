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

        buttonBack.setOnClickListener(
                v ->
                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack()
        );

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        buttonSave.setOnClickListener(
                v ->
                        saveSynagogue()
        );
    }

    // =========================================================
    // Summary
    // =========================================================

    private void showSummary() {

        Bundle data =
                getArguments();

        if (data == null) {

            summaryText.setText(
                    "לא נמצאו נתונים להרשמה."
            );

            buttonSave.setEnabled(false);

            return;
        }

        StringBuilder summary =
                new StringBuilder();

        // =====================================================
        // Basic Data
        // =====================================================

        String name =
                data.getString(
                        "synagogueName",
                        ""
                );

        String description =
                data.getString(
                        "synagogueDescription",
                        ""
                );

        String address =
                data.getString(
                        "synagogueAddress",
                        ""
                );

        String phone =
                data.getString(
                        "synagoguePhone",
                        ""
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

        summary.append(
                "שם בית הכנסת\n"
        );

        summary.append(
                emptyOrValue(name)
        );

        summary.append(
                "\n\n"
        );

        // =====================================================
        // Description
        // =====================================================

        summary.append(
                "תיאור\n"
        );

        summary.append(
                emptyOrValue(description)
        );

        summary.append(
                "\n\n"
        );

        // =====================================================
        // Address
        // =====================================================

        summary.append(
                "כתובת\n"
        );

        summary.append(
                emptyOrValue(address)
        );

        summary.append(
                "\n\n"
        );

        // =====================================================
        // Phone
        // =====================================================

        summary.append(
                "טלפון\n"
        );

        summary.append(
                emptyOrValue(phone)
        );

        summary.append(
                "\n\n"
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
        // Opening Hours / Prayers
        //
        // IMPORTANT:
        //
        // Firebase field:
        //
        // openingHours
        //
        // =====================================================

        summary.append(
                "זמני תפילות\n"
        );

        appendOpeningHoursSummary(
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
        // Display
        // =====================================================

        summaryText.setText(
                summary.toString()
        );
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
    // Opening Hours Summary
    //
    // Reads:
    //
    // openingHours
    //
    // Each item:
    //
    // Header:
    // type = header
    // text = "תפילות יום חול"
    //
    // Normal:
    // type = normal
    // title = "שחרית"
    // content = "05:45"
    //
    // =========================================================

    private void appendOpeningHoursSummary(
            StringBuilder summary,
            Bundle data) {

        Object openingHoursObject =
                data.getSerializable(
                        "openingHours"
                );

        // -----------------------------------------------------
        // No data
        // -----------------------------------------------------

        if (!(openingHoursObject instanceof ArrayList)) {

            summary.append(
                    "שעות הפתיחה לא הוגדרו.\n"
            );

            return;
        }

        ArrayList<?> rows =
                (ArrayList<?>) openingHoursObject;

        // -----------------------------------------------------
        // Empty
        // -----------------------------------------------------

        if (rows.isEmpty()) {

            summary.append(
                    "שעות הפתיחה לא הוגדרו.\n"
            );

            return;
        }

        boolean foundAny =
                false;

        // =====================================================
        // Rows
        // =====================================================

        for (Object rowObject :
                rows) {

            // -------------------------------------------------
            // Must be Map
            // -------------------------------------------------

            if (!(rowObject instanceof Map)) {
                continue;
            }

            Map<?, ?> row =
                    (Map<?, ?>) rowObject;

            // -------------------------------------------------
            // Type
            // -------------------------------------------------

            String type =
                    valueFromMap(
                            row,
                            "type"
                    );

            // =================================================
            // HEADER
            //
            // Firebase:
            //
            // {
            //     type: "header",
            //     text: "תפילות יום חול"
            // }
            // =================================================

            if ("header".equalsIgnoreCase(type)) {

                String text =
                        valueFromMap(
                                row,
                                "text"
                        );

                if (TextUtils.isEmpty(text)) {
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
                        text
                );

                summary.append(
                        "\n"
                );

                foundAny = true;

                continue;
            }

            // =================================================
            // NORMAL
            //
            // Firebase:
            //
            // {
            //     type: "normal",
            //     title: "שחרית",
            //     content: "05:45"
            // }
            // =================================================

            if ("normal".equalsIgnoreCase(type)
                    || TextUtils.isEmpty(type)) {

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

                // ---------------------------------------------
                // Skip completely empty row
                // ---------------------------------------------

                if (TextUtils.isEmpty(title)
                        && TextUtils.isEmpty(content)) {

                    continue;
                }

                if (foundAny) {

                    summary.append(
                            "\n"
                    );
                }

                // ---------------------------------------------
                // Prayer title
                // ---------------------------------------------

                if (!TextUtils.isEmpty(title)) {

                    summary.append(
                            title
                    );
                }

                // ---------------------------------------------
                // Time
                // ---------------------------------------------

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

                foundAny = true;
            }
        }

        // =====================================================
        // Nothing found
        // =====================================================

        if (!foundAny) {

            summary.append(
                    "שעות הפתיחה לא הוגדרו.\n"
            );
        }
    }

    // =========================================================
    // Read Map Value
    // =========================================================

    private String valueFromMap(
            Map<?, ?> map,
            String key) {

        Object value =
                map.get(key);

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

        for (String key :
                selected) {

            String name =
                    names.get(
                            key
                    );

            if (name == null) {
                name = key;
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
        }
    }

    // =========================================================
    // Save Synagogue
    // =========================================================

    private void saveSynagogue() {

        Bundle data =
                getArguments();

        // -----------------------------------------------------
        // Data validation
        // -----------------------------------------------------

        if (data == null) {

            showError(
                    "לא נמצאו נתוני הרשמה"
            );

            return;
        }

        // -----------------------------------------------------
        // Name
        // -----------------------------------------------------

        String name =
                data.getString(
                        "synagogueName",
                        ""
                ).trim();

        if (TextUtils.isEmpty(name)) {

            showError(
                    "חסר שם בית הכנסת"
            );

            return;
        }

        // -----------------------------------------------------
        // Address
        // -----------------------------------------------------

        String address =
                data.getString(
                        "synagogueAddress",
                        ""
                ).trim();

        if (TextUtils.isEmpty(address)) {

            showError(
                    "חסרה כתובת"
            );

            return;
        }

        // -----------------------------------------------------
        // Firebase user
        // -----------------------------------------------------

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            showError(
                    "לא נמצא חשבון מחובר. נא להתחבר מחדש."
            );

            return;
        }

        // -----------------------------------------------------
        // Email verification
        // -----------------------------------------------------

        if (!currentUser.isEmailVerified()) {

            showError(
                    "יש לאמת את כתובת האימייל לפני שמירת בית הכנסת."
            );

            return;
        }

        // -----------------------------------------------------
        // Disable buttons
        // -----------------------------------------------------

        buttonSave.setEnabled(false);
        buttonBack.setEnabled(false);

        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        saveToFirestore(
                currentUser.getUid(),
                data
        );
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
                    currentUser.getEmail();
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
                data.getString(
                        "synagogueName",
                        ""
                )
        );

        synagogue.put(
                "address",
                data.getString(
                        "synagogueAddress",
                        ""
                )
        );

        synagogue.put(
                "phone",
                data.getString(
                        "synagoguePhone",
                        ""
                )
        );

        synagogue.put(
                "description",
                data.getString(
                        "synagogueDescription",
                        ""
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
        // OPENING HOURS
        //
        // IMPORTANT:
        //
        // The Firestore field is:
        //
        // openingHours
        //
        // NOT:
        //
        // prayers
        //
        // =====================================================

        synagogue.put(
                "openingHours",
                buildOpeningHours(data)
        );

        // =====================================================
        // FEATURES
        // =====================================================

        synagogue.put(
                "features",
                buildFeatures(data)
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
                .document(uid)
                .set(synagogue)
                .addOnSuccessListener(
                        unused -> {

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

                            buttonSave.setEnabled(true);
                            buttonBack.setEnabled(true);

                            String message =
                                    e.getMessage();

                            if (TextUtils.isEmpty(message)) {

                                message =
                                        "שגיאה לא ידועה";
                            }

                            showError(
                                    "שמירת בית הכנסת נכשלה:\n"
                                            + message
                            );
                        }
                );
    }

    // =========================================================
    // Build Opening Hours
    //
    // Converts the registration Bundle data into the exact
    // Firestore structure:
    //
    // openingHours
    //
    // [
    //   {
    //      type: "header",
    //      text: "תפילות יום חול"
    //   },
    //   {
    //      type: "normal",
    //      title: "שחרית",
    //      content: "05:45"
    //   }
    // ]
    //
    // =========================================================

    private ArrayList<Map<String, Object>> buildOpeningHours(
            Bundle data) {

        ArrayList<Map<String, Object>> result =
                new ArrayList<>();

        // -----------------------------------------------------
        // Read from Bundle
        // -----------------------------------------------------

        Object openingHoursObject =
                data.getSerializable(
                        "openingHours"
                );

        // -----------------------------------------------------
        // IMPORTANT BACKWARD COMPATIBILITY
        //
        // If the previous fragment still sends
        // "prayerData", we can read it as fallback.
        //
        // This prevents the summary from breaking while
        // transitioning the previous fragment.
        // -----------------------------------------------------

        if (!(openingHoursObject instanceof ArrayList)) {

            openingHoursObject =
                    data.getSerializable(
                            "prayerData"
                    );
        }

        // -----------------------------------------------------
        // Validate
        // -----------------------------------------------------

        if (!(openingHoursObject instanceof ArrayList)) {

            return result;
        }

        ArrayList<?> originalRows =
                (ArrayList<?>) openingHoursObject;

        // =====================================================
        // Convert each row
        // =====================================================

        for (Object rowObject :
                originalRows) {

            if (!(rowObject instanceof Map)) {
                continue;
            }

            Map<?, ?> originalRow =
                    (Map<?, ?>) rowObject;

            Map<String, Object> row =
                    new HashMap<>();

            // -------------------------------------------------
            // Type
            // -------------------------------------------------

            String type =
                    valueFromMap(
                            originalRow,
                            "type"
                    );

            // =================================================
            // HEADER
            //
            // Exact Firestore structure:
            //
            // type = header
            // text = ...
            // =================================================

            if ("header".equalsIgnoreCase(type)) {

                String text =
                        valueFromMap(
                                originalRow,
                                "text"
                        );

                // ---------------------------------------------
                // Backward compatibility:
                //
                // Old structure may have stored the header
                // text in "title".
                // ---------------------------------------------

                if (TextUtils.isEmpty(text)) {

                    text =
                            valueFromMap(
                                    originalRow,
                                    "title"
                            );
                }

                // ---------------------------------------------
                // Skip empty header
                // ---------------------------------------------

                if (TextUtils.isEmpty(text)) {
                    continue;
                }

                row.put(
                        "type",
                        "header"
                );

                row.put(
                        "text",
                        text
                );

                result.add(
                        row
                );

                continue;
            }

            // =================================================
            // NORMAL
            //
            // Exact Firestore structure:
            //
            // type = normal
            // title = ...
            // content = ...
            // =================================================

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
            // Skip completely empty row
            // -------------------------------------------------

            if (TextUtils.isEmpty(title)
                    && TextUtils.isEmpty(content)) {

                continue;
            }

            row.put(
                    "type",
                    "normal"
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

            result.put(
                    key,
                    selected != null
                            && selected.contains(key)
            );
        }

        return result;
    }

    // =========================================================
    // Finish Registration
    // =========================================================

    private void finishRegistration() {

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

            }

            // -------------------------------------------------
            // Active
            // -------------------------------------------------

            else if (stepNumber == currentStep) {

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

            }

            // -------------------------------------------------
            // Inactive
            // -------------------------------------------------

            else {

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
