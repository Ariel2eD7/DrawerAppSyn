package com.example.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.MainActivity;
import com.example.drawerappsyn.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // =========================================================
    // Firebase
    // =========================================================

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // =========================================================
    // Main fields
    // =========================================================

    private EditText noticeInput;
    private EditText lessonsInput;

    private EditText nameInput;
    private EditText addressInput;
    private EditText phoneInput;
    private EditText descriptionInput;

    private TextView profileStatus;
    private TextView todayTitle;

    private LinearLayout openingHoursContainer;
    private LinearLayout featuresContainer;

    private ProgressBar progressBar;
    private MaterialButton saveButton;

    // =========================================================
    // Opening hours
    // =========================================================

    private final ArrayList<View> openingRows =
            new ArrayList<>();

    // =========================================================
    // Features
    // =========================================================

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

    private final Map<String, Boolean> featureData =
            new HashMap<>();

    // =========================================================
    // Constructor
    // =========================================================

    public ProfileFragment() {
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
                R.layout.fragment_profile,
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

        bindViews(view);

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        initializeFeatureData();

        setupOpeningHoursButtons();

        setupSaveButton();

        updateTodayTitle();

        loadProfile();
    }

    // =========================================================
    // Bind Views
    // =========================================================

    private void bindViews(View view) {

        noticeInput =
                view.findViewById(
                        R.id.profileNoticeInput
                );

        lessonsInput =
                view.findViewById(
                        R.id.profileLessonsInput
                );

        nameInput =
                view.findViewById(
                        R.id.profileNameInput
                );

        addressInput =
                view.findViewById(
                        R.id.profileAddressInput
                );

        phoneInput =
                view.findViewById(
                        R.id.profilePhoneInput
                );

        descriptionInput =
                view.findViewById(
                        R.id.profileDescriptionInput
                );

        profileStatus =
                view.findViewById(
                        R.id.profileStatus
                );

        todayTitle =
                view.findViewById(
                        R.id.profileTodayTitle
                );

        openingHoursContainer =
                view.findViewById(
                        R.id.profileOpeningHoursContainer
                );

        featuresContainer =
                view.findViewById(
                        R.id.profileFeaturesContainer
                );

        progressBar =
                view.findViewById(
                        R.id.profileProgressBar
                );

        saveButton =
                view.findViewById(
                        R.id.profileSaveButton
                );
    }

    // =========================================================
    // Opening Hours Buttons
    // =========================================================

    private void setupOpeningHoursButtons() {

        MaterialButton addHourButton =
                requireView().findViewById(
                        R.id.profileAddOpeningHour
                );

        MaterialButton addHeaderButton =
                requireView().findViewById(
                        R.id.profileAddHeader
                );

        addHourButton.setOnClickListener(
                v -> addHour(
                        "",
                        ""
                )
        );

        addHeaderButton.setOnClickListener(
                v -> addHeader(
                        ""
                )
        );
    }

    // =========================================================
    // Load Profile
    // =========================================================

    private void loadProfile() {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            showError(
                    "לא נמצא משתמש מחובר."
            );

            return;
        }

        showLoading(true);

        db.collection("synagogues_v2")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(
                        document -> {

                            showLoading(false);

                            if (!document.exists()) {

                                showError(
                                        "לא נמצא בית כנסת המשויך לחשבון."
                                );

                                return;
                            }

                            displayProfile(
                                    document
                            );
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showLoading(false);

                            showError(
                                    "טעינת הפרופיל נכשלה:\n"
                                            + error.getMessage()
                            );
                        }
                );
    }

    // =========================================================
    // Display Profile
    // =========================================================

    private void displayProfile(
            DocumentSnapshot document) {

        nameInput.setText(
                valueOrDefault(
                        document.getString("name"),
                        ""
                )
        );

        addressInput.setText(
                valueOrDefault(
                        document.getString("address"),
                        ""
                )
        );

        phoneInput.setText(
                valueOrDefault(
                        document.getString("phone"),
                        ""
                )
        );

        descriptionInput.setText(
                valueOrDefault(
                        document.getString("description"),
                        ""
                )
        );

        noticeInput.setText(
                valueOrDefault(
                        document.getString("notice"),
                        ""
                )
        );

        lessonsInput.setText(
                valueOrDefault(
                        document.getString("lessons"),
                        ""
                )
        );

        String status =
                document.getString("status");

        if ("active".equals(status)) {

            profileStatus.setText(
                    "● פעיל"
            );

        } else {

            profileStatus.setText(
                    valueOrDefault(
                            status,
                            "● לא ידוע"
                    )
            );
        }

        // -----------------------------------------------------
        // Opening Hours
        // -----------------------------------------------------

        Object openingHours =
                document.get("openingHours");

        loadOpeningHours(
                openingHours
        );

        // -----------------------------------------------------
        // Features
        // -----------------------------------------------------

        Object features =
                document.get("features");

        loadFeatureData(
                features
        );

        renderFeatureEditor();
    }

    // =========================================================
    // Load Opening Hours
    // =========================================================

    private void loadOpeningHours(
            Object data) {

        openingHoursContainer.removeAllViews();
        openingRows.clear();

        if (!(data instanceof List)) {
            return;
        }

        List<?> list =
                (List<?>) data;

        for (Object item : list) {

            if (!(item instanceof Map)) {
                continue;
            }

            Map<?, ?> map =
                    (Map<?, ?>) item;

            String type =
                    stringValue(
                            map.get("type")
                    );

            if ("header".equals(type)) {

                String text =
                        stringValue(
                                map.get("text")
                        );

                if (text.isEmpty()) {

                    text =
                            stringValue(
                                    map.get("content")
                            );
                }

                addHeader(
                        text
                );

            } else {

                String title =
                        stringValue(
                                map.get("title")
                        );

                String content =
                        stringValue(
                                map.get("content")
                        );

                addHour(
                        title,
                        content
                );
            }
        }
    }

    // =========================================================
    // Add Hour Row
    // =========================================================

    private void addHour(
            String titleText,
            String contentText) {

        LinearLayout row =
                createBaseRow();

        LinearLayout moves =
                createMoveButtons(
                        row
                );

        EditText title =
                createEditText(
                        "תפילה / פעילות",
                        15
                );

        title.setText(
                titleText
        );

        title.setLayoutParams(
                createWeightParams()
        );

        EditText content =
                createEditText(
                        "שעה",
                        15
                );

        content.setText(
                contentText
        );

        content.setLayoutParams(
                createWeightParams()
        );

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

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                );

        delete.setLayoutParams(
                deleteParams
        );

        delete.setOnClickListener(
                v -> removeRow(
                        row
                )
        );

        moves.getChildAt(0)
                .setOnClickListener(
                        v -> moveRow(
                                row,
                                -1
                        )
                );

        moves.getChildAt(1)
                .setOnClickListener(
                        v -> moveRow(
                                row,
                                1
                        )
                );

        // RTL:
        // move buttons | title | time | delete

        row.addView(
                moves
        );

        row.addView(
                title
        );

        row.addView(
                content
        );

        row.addView(
                delete
        );

        addRow(
                row
        );
    }

    // =========================================================
    // Add Header Row
    // =========================================================

    private void addHeader(
            String textValue) {

        LinearLayout row =
                createBaseRow();

        LinearLayout moves =
                createMoveButtons(
                        row
                );

        EditText header =
                createEditText(
                        "כותרת / הפרדה",
                        17
                );

        header.setText(
                textValue
        );

        header.setTextColor(
                Color.rgb(
                        40,
                        40,
                        40
                )
        );

        header.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        header.setGravity(
                Gravity.CENTER
        );

        header.setTextAlignment(
                View.TEXT_ALIGNMENT_CENTER
        );

        header.setLayoutParams(
                createWeightParams()
        );

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

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(48)
                );

        delete.setLayoutParams(
                deleteParams
        );

        delete.setOnClickListener(
                v -> removeRow(
                        row
                )
        );

        moves.getChildAt(0)
                .setOnClickListener(
                        v -> moveRow(
                                row,
                                -1
                        )
                );

        moves.getChildAt(1)
                .setOnClickListener(
                        v -> moveRow(
                                row,
                                1
                        )
                );

        row.addView(
                moves
        );

        row.addView(
                header
        );

        row.addView(
                delete
        );

        addRow(
                row
        );
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
    // Row Background
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

    private LinearLayout createMoveButtons(
            View row) {

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
    // Add Row
    // =========================================================

    private void addRow(
            View row) {

        openingHoursContainer.addView(
                row
        );

        openingRows.add(
                row
        );

        updateMoveButtons();
    }

    // =========================================================
    // Remove Row
    // =========================================================

    private void removeRow(
            View row) {

        openingHoursContainer.removeView(
                row
        );

        openingRows.remove(
                row
        );

        updateMoveButtons();
    }

    // =========================================================
    // Move Row
    // =========================================================

    private void moveRow(
            View row,
            int direction) {

        int currentIndex =
                openingHoursContainer.indexOfChild(
                        row
                );

        int newIndex =
                currentIndex + direction;

        if (currentIndex < 0) {
            return;
        }

        if (newIndex < 0) {
            return;
        }

        if (newIndex >=
                openingHoursContainer.getChildCount()) {

            return;
        }

        openingHoursContainer.removeView(
                row
        );

        openingHoursContainer.addView(
                row,
                newIndex
        );

        openingRows.remove(
                row
        );

        openingRows.add(
                newIndex,
                row
        );

        updateMoveButtons();
    }

    // =========================================================
    // Update Move Buttons
    // =========================================================

    private void updateMoveButtons() {

        int count =
                openingHoursContainer.getChildCount();

        for (int i = 0; i < count; i++) {

            View view =
                    openingHoursContainer.getChildAt(
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
    // Get Opening Hours
    // =========================================================

    private ArrayList<Map<String, String>>
    getOpeningHours() {

        ArrayList<Map<String, String>> list =
                new ArrayList<>();

        for (int i = 0;
             i < openingHoursContainer.getChildCount();
             i++) {

            View view =
                    openingHoursContainer.getChildAt(
                            i
                    );

            if (!(view instanceof LinearLayout)) {
                continue;
            }

            LinearLayout row =
                    (LinearLayout) view;

            int childCount =
                    row.getChildCount();

            // -------------------------------------------------
            // Header
            // -------------------------------------------------

            if (childCount == 3) {

                View field =
                        row.getChildAt(1);

                if (!(field instanceof EditText)) {
                    continue;
                }

                String text =
                        ((EditText) field)
                                .getText()
                                .toString()
                                .trim();

                if (TextUtils.isEmpty(text)) {
                    continue;
                }

                Map<String, String> item =
                        new HashMap<>();

                item.put(
                        "type",
                        "header"
                );

                item.put(
                        "text",
                        text
                );

                list.add(
                        item
                );
            }

            // -------------------------------------------------
            // Normal hour
            // -------------------------------------------------

            else if (childCount == 4) {

                View titleView =
                        row.getChildAt(1);

                View contentView =
                        row.getChildAt(2);

                if (!(titleView instanceof EditText)
                        || !(contentView instanceof EditText)) {

                    continue;
                }

                String title =
                        ((EditText) titleView)
                                .getText()
                                .toString()
                                .trim();

                String content =
                        ((EditText) contentView)
                                .getText()
                                .toString()
                                .trim();

                if (TextUtils.isEmpty(title)
                        && TextUtils.isEmpty(content)) {

                    continue;
                }

                Map<String, String> item =
                        new HashMap<>();

                item.put(
                        "type",
                        "normal"
                );

                item.put(
                        "title",
                        title
                );

                item.put(
                        "content",
                        content
                );

                list.add(
                        item
                );
            }
        }

        return list;
    }

    // =========================================================
    // Features
    // =========================================================

    private void initializeFeatureData() {

        featureData.clear();

        for (String key : featureKeys) {

            featureData.put(
                    key,
                    false
            );
        }
    }

    private void loadFeatureData(
            Object featuresObject) {

        initializeFeatureData();

        if (!(featuresObject instanceof Map)) {
            return;
        }

        Map<?, ?> features =
                (Map<?, ?>) featuresObject;

        for (String key : featureKeys) {

            Object value =
                    features.get(key);

            if (value instanceof Boolean) {

                featureData.put(
                        key,
                        (Boolean) value
                );
            }
        }
    }

    private void renderFeatureEditor() {

        featuresContainer.removeAllViews();

        for (int i = 0;
             i < featureKeys.length;
             i++) {

            String key =
                    featureKeys[i];

            String name =
                    featureNames[i];

            android.widget.CheckBox checkBox =
                    new android.widget.CheckBox(
                            requireContext()
                    );

            checkBox.setText(
                    name
            );

            checkBox.setTextSize(
                    15
            );

            checkBox.setChecked(
                    Boolean.TRUE.equals(
                            featureData.get(key)
                    )
            );

            checkBox.setPadding(
                    0,
                    dp(5),
                    0,
                    dp(5)
            );

            checkBox.setOnCheckedChangeListener(
                    (buttonView, isChecked) ->
                            featureData.put(
                                    key,
                                    isChecked
                            )
            );

            featuresContainer.addView(
                    checkBox
            );
        }
    }

    // =========================================================
    // Save
    // =========================================================

    private void setupSaveButton() {

        saveButton.setOnClickListener(
                v -> saveProfile()
        );
    }

    private void saveProfile() {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser == null) {

            showError(
                    "לא נמצא משתמש מחובר."
            );

            return;
        }

        String name =
                nameInput.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            nameInput.setError(
                    "יש להזין שם בית כנסת"
            );

            nameInput.requestFocus();

            return;
        }

        showLoading(true);

        String uid =
                currentUser.getUid();

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "name",
                name
        );

        updates.put(
                "address",
                addressInput.getText()
                        .toString()
                        .trim()
        );

        updates.put(
                "phone",
                phoneInput.getText()
                        .toString()
                        .trim()
        );

        updates.put(
                "description",
                descriptionInput.getText()
                        .toString()
                        .trim()
        );

        updates.put(
                "notice",
                noticeInput.getText()
                        .toString()
                        .trim()
        );

        updates.put(
                "lessons",
                lessonsInput.getText()
                        .toString()
                        .trim()
        );

        // -----------------------------------------------------
        // New modular opening hours
        // -----------------------------------------------------

        updates.put(
                "openingHours",
                getOpeningHours()
        );

        updates.put(
                "features",
                new HashMap<>(
                        featureData
                )
        );

        updates.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );

        db.collection("synagogues_v2")
                .document(uid)
                .update(updates)
                .addOnSuccessListener(
                        unused -> {

                            showLoading(false);

                            Toast.makeText(
                                    requireContext(),
                                    "השינויים נשמרו בהצלחה",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(
                        error -> {

                            showLoading(false);

                            showError(
                                    "שמירת השינויים נכשלה:\n"
                                            + error.getMessage()
                            );
                        }
                );
    }

    // =========================================================
    // Today
    // =========================================================

    private void updateTodayTitle() {

        if (todayTitle == null) {
            return;
        }

        CalendarHelper calendar =
                new CalendarHelper();

        todayTitle.setText(
                "היום · " + calendar.getHebrewDay()
        );
    }

    // =========================================================
    // Loading
    // =========================================================

    private void showLoading(
            boolean loading) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (saveButton != null) {

            saveButton.setEnabled(
                    !loading
            );
        }
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
    // String helper
    // =========================================================

    private String stringValue(
            Object value) {

        return value == null
                ? ""
                : String.valueOf(value);
    }

    private String valueOrDefault(
            String value,
            String defaultValue) {

        return TextUtils.isEmpty(value)
                ? defaultValue
                : value;
    }

    // =========================================================
    // DP
    // =========================================================

    private int dp(int value) {

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

    // =========================================================
    // Small calendar helper
    // =========================================================

    private static class CalendarHelper {

        String getHebrewDay() {

            java.util.Calendar calendar =
                    java.util.Calendar.getInstance();

            int day =
                    calendar.get(
                            java.util.Calendar.DAY_OF_WEEK
                    );

            switch (day) {

                case java.util.Calendar.SUNDAY:
                    return "יום ראשון";

                case java.util.Calendar.MONDAY:
                    return "יום שני";

                case java.util.Calendar.TUESDAY:
                    return "יום שלישי";

                case java.util.Calendar.WEDNESDAY:
                    return "יום רביעי";

                case java.util.Calendar.THURSDAY:
                    return "יום חמישי";

                case java.util.Calendar.FRIDAY:
                    return "יום שישי";

                case java.util.Calendar.SATURDAY:
                default:
                    return "יום שבת";
            }
        }
    }
}
