package com.example.synagogue;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.MainActivity;
import com.example.drawerappsyn.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SynagogueDetailsFragment extends Fragment
        implements OnMapReadyCallback {

    private static final String ARG_SYNAGOGUE = "synagogue";

    private GoogleMap googleMap;
    private String phone = "";
    private double latitude = 0;
    private double longitude = 0;

    private LinearLayout prayersContainer;
    private LinearLayout featuresContainer;

    private FirebaseFirestore db;

    public SynagogueDetailsFragment() {
        // Required empty constructor
    }

    public static SynagogueDetailsFragment newInstance(
            Synagogue synagogue) {

        SynagogueDetailsFragment fragment =
                new SynagogueDetailsFragment();

        Bundle args = new Bundle();

        args.putSerializable(
                ARG_SYNAGOGUE,
                synagogue
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_synagogue_details,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        db =
                FirebaseFirestore.getInstance();

        TextView textName =
                view.findViewById(R.id.textDetailName);

        TextView textAddress =
                view.findViewById(R.id.textDetailAddress);

        TextView textDescription =
                view.findViewById(R.id.textDetailDescription);

        TextView textPhone =
                view.findViewById(R.id.textDetailPhone);

        MaterialButton buttonCall =
                view.findViewById(R.id.buttonCall);

        MaterialButton buttonNavigate =
                view.findViewById(R.id.buttonNavigate);

        View buttonBack =
                view.findViewById(R.id.buttonBack);

        prayersContainer =
                view.findViewById(R.id.prayersContainer);

        featuresContainer =
                view.findViewById(R.id.featuresContainer);

        db = FirebaseFirestore.getInstance();

        buttonBack.setOnClickListener(
                v -> goBack()
        );

        Synagogue synagogue =
                getSynagogue();

        if (synagogue == null) {

            Toast.makeText(
                    requireContext(),
                    "לא נמצאו פרטי בית הכנסת",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // =========================================================
        // Basic information
        // =========================================================

        textName.setText(
                valueOrDefault(
                        synagogue.getName(),
                        "בית כנסת"
                )
        );

        textAddress.setText(
                valueOrDefault(
                        synagogue.getAddress(),
                        "כתובת לא הוגדרה"
                )
        );

        // =========================================================
        // Description
        // =========================================================

        String description =
                synagogue.getDescription();

        if (TextUtils.isEmpty(description)) {

            textDescription.setVisibility(
                    View.GONE
            );

        } else {

            textDescription.setVisibility(
                    View.VISIBLE
            );

            textDescription.setText(
                    description.trim()
            );
        }

        // =========================================================
        // Phone
        // =========================================================

        phone =
                valueOrDefault(
                        synagogue.getPhone(),
                        ""
                ).trim();

        if (phone.isEmpty()) {

            textPhone.setText(
                    "טלפון לא הוגדר"
            );

            buttonCall.setEnabled(false);
            buttonCall.setAlpha(0.45f);

        } else {

            textPhone.setText(phone);

            buttonCall.setEnabled(true);
            buttonCall.setAlpha(1f);

            buttonCall.setOnClickListener(
                    v -> callPhone()
            );
        }

        // =========================================================
        // Location
        // =========================================================

        latitude =
                synagogue.getLatitude();

        longitude =
                synagogue.getLongitude();

        if (!hasLocation()) {

            buttonNavigate.setEnabled(false);
            buttonNavigate.setAlpha(0.45f);

        } else {

            buttonNavigate.setEnabled(true);
            buttonNavigate.setAlpha(1f);

            buttonNavigate.setOnClickListener(
                    v -> openNavigation()
            );
        }

        // =========================================================
        // Opening Hours
        //
        // IMPORTANT:
        // Do NOT use synagogue.getPrayers().
        //
        // The new source is:
        // synagogues_v2/{uid}/openingHours
        // =========================================================

        loadOpeningHours(
                synagogue
        );

        // =========================================================
        // Features
        // =========================================================

        buildFeatures(
                synagogue.getFeatures()
        );

        // =========================================================
        // Map
        // =========================================================

        SupportMapFragment map =
                (SupportMapFragment)
                        getChildFragmentManager()
                                .findFragmentById(
                                        R.id.detailMap
                                );

        if (map != null) {
            map.getMapAsync(this);
        }
    }

    // =============================================================
    // Load Opening Hours from Firestore
    // =============================================================



    private void loadOpeningHours(
            Synagogue synagogue) {

        prayersContainer.removeAllViews();

        if (synagogue == null) {
            addEmptyState(
                    prayersContainer,
                    "שעות הפתיחה לא הוגדרו"
            );
            return;
        }

        // Document ID של המסמך ב־synagogues_v2
        String synagogueId =
                synagogue.getId();

        if (TextUtils.isEmpty(synagogueId)) {

            addEmptyState(
                    prayersContainer,
                    "לא נמצא מזהה בית הכנסת"
            );

            return;
        }

        db.collection("synagogues_v2")
                .document(synagogueId)
                .get()
                .addOnSuccessListener(
                        document -> {

                            if (!document.exists()) {

                                addEmptyState(
                                        prayersContainer,
                                        "שעות הפתיחה לא הוגדרו"
                                );

                                return;
                            }

                            Object openingHours =
                                    document.get(
                                            "openingHours"
                                    );

                            renderOpeningHours(
                                    openingHours
                            );
                        }
                )
                .addOnFailureListener(
                        error -> {

                            addEmptyState(
                                    prayersContainer,
                                    "לא ניתן לטעון את שעות הפתיחה"
                            );
                        }
                );
    }


    // =============================================================
    // Render Opening Hours
    // =============================================================

    private void renderOpeningHours(
            Object data) {

        prayersContainer.removeAllViews();

        if (!(data instanceof List)) {

            addEmptyState(
                    prayersContainer,
                    "שעות הפתיחה לא הוגדרו"
            );

            return;
        }

        List<?> list =
                (List<?>) data;

        boolean found = false;

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

            // =====================================================
            // Header
            // =====================================================

            if ("header".equals(type)) {

                String text =
                        stringValue(
                                map.get("text")
                        );

                if (TextUtils.isEmpty(text)) {

                    text =
                            stringValue(
                                    map.get("content")
                            );
                }

                if (!TextUtils.isEmpty(text)) {

                    addHeaderView(
                            prayersContainer,
                            text
                    );

                    found = true;
                }

                continue;
            }

            // =====================================================
            // Normal opening hour
            // =====================================================

            String title =
                    stringValue(
                            map.get("title")
                    );

            String content =
                    stringValue(
                            map.get("content")
                    );

            if (TextUtils.isEmpty(title)
                    && TextUtils.isEmpty(content)) {

                continue;
            }

            addOpeningHourView(
                    prayersContainer,
                    title,
                    content
            );

            found = true;
        }

        if (!found) {

            addEmptyState(
                    prayersContainer,
                    "שעות הפתיחה לא הוגדרו"
            );
        }
    }

    // =============================================================
    // Opening Hour Row
    // =============================================================

    private void addOpeningHourView(
            LinearLayout container,
            String titleText,
            String contentText) {

        LinearLayout row =
                createOpeningRow();

        TextView title =
                new TextView(
                        requireContext()
                );

        title.setText(
                titleText == null
                        ? ""
                        : titleText
        );

        title.setTextSize(16);
        title.setTextColor(
                Color.rgb(
                        45,
                        45,
                        45
                )
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER_VERTICAL |
                        Gravity.START
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        titleParams.setMargins(
                0,
                0,
                dp(8),
                0
        );

        title.setLayoutParams(
                titleParams
        );

        TextView content =
                new TextView(
                        requireContext()
                );

        content.setText(
                contentText == null
                        ? ""
                        : contentText
        );

        content.setTextSize(16);

        content.setTextColor(
                Color.rgb(
                        80,
                        80,
                        80
                )
        );

        content.setGravity(
                Gravity.CENTER_VERTICAL |
                        Gravity.END
        );

        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        contentParams.setMargins(
                dp(8),
                0,
                0,
                0
        );

        content.setLayoutParams(
                contentParams
        );

        /*
         * RTL:
         *
         * title | content
         *
         * Android will place them according to the row direction.
         */

        row.addView(title);
        row.addView(content);

        container.addView(row);
    }

    // =============================================================
    // Opening Header
    // =============================================================

    private void addHeaderView(
            LinearLayout container,
            String textValue) {

        if (TextUtils.isEmpty(textValue)) {
            return;
        }

        View spacer =
                new View(
                        requireContext()
                );

        spacer.setLayoutParams(
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(5)
                )
        );

        container.addView(spacer);

        LinearLayout row =
                createHeaderRow();

        View line =
                new View(
                        requireContext()
                );

        line.setBackgroundColor(
                Color.rgb(
                        25,
                        118,
                        210
                )
        );

        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(
                        dp(4),
                        dp(24)
                );

        lineParams.setMargins(
                0,
                0,
                dp(8),
                0
        );

        row.addView(
                line,
                lineParams
        );

        TextView title =
                new TextView(
                        requireContext()
                );

        title.setText(
                textValue.trim()
        );

        title.setTextSize(15);

        title.setTextColor(
                Color.rgb(
                        25,
                        75,
                        120
                )
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        title.setGravity(
                Gravity.CENTER
        );

        title.setTextAlignment(
                View.TEXT_ALIGNMENT_CENTER
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        titleParams.setMargins(
                0,
                0,
                dp(12),
                0
        );

        title.setLayoutParams(
                titleParams
        );

        row.addView(title);

        container.addView(row);
    }

    // =============================================================
    // Opening Row Background
    // =============================================================

    private LinearLayout createOpeningRow() {

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
                dp(16),
                dp(14),
                dp(16),
                dp(14)
        );

        row.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dp(18)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        232,
                        234,
                        237
                )
        );

        row.setBackground(
                background
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
                dp(8)
        );

        row.setLayoutParams(params);

        return row;
    }

    // =============================================================
    // Header Background
    // =============================================================


    private LinearLayout createHeaderRow() {

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
                dp(12),
                dp(7),
                dp(12),
                dp(7)
        );

        row.setLayoutDirection(
                View.LAYOUT_DIRECTION_RTL
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.rgb(
                        227,
                        242,
                        253
                )
        );

        background.setCornerRadius(
                dp(12)
        );

        background.setStroke(
                dp(1),
                Color.rgb(
                        187,
                        222,
                        251
                )
        );

        row.setBackground(
                background
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
                dp(5)
        );

        row.setLayoutParams(params);

        return row;
    }

    // =============================================================
    // Navigation
    // =============================================================

    private void goBack() {

        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    // =============================================================
    // Synagogue
    // =============================================================

    private Synagogue getSynagogue() {

        Bundle args =
                getArguments();

        if (args == null) {
            return null;
        }

        Object object =
                args.getSerializable(
                        ARG_SYNAGOGUE
                );

        if (object instanceof Synagogue) {

            return (Synagogue) object;
        }

        return null;
    }

    // =============================================================
    // Toolbar
    // =============================================================

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

    // =============================================================
    // Helpers
    // =============================================================

    private String valueOrDefault(
            String value,
            String fallback) {

        if (TextUtils.isEmpty(value)) {
            return fallback;
        }

        return value.trim();
    }

    private String stringValue(
            Object value) {

        return value == null
                ? ""
                : String.valueOf(value).trim();
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return Math.round(
                value * density
        );
    }

    private boolean hasLocation() {

        return latitude != 0 &&
                longitude != 0;
    }

    // =============================================================
    // Phone
    // =============================================================

    private void callPhone() {

        if (phone.isEmpty()) {
            return;
        }

        Intent intent =
                new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse(
                                "tel:" +
                                        Uri.encode(phone)
                        )
                );

        try {

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "לא ניתן לפתוח את אפליקציית הטלפון",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =============================================================
    // Navigation
    // =============================================================

    private void openNavigation() {

        if (!hasLocation()) {
            return;
        }

        String coordinates =
                latitude +
                        "," +
                        longitude;

        String wazeUrl =
                "https://waze.com/ul?ll=" +
                        coordinates +
                        "&navigate=yes";

        Intent wazeIntent =
                new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(wazeUrl)
                );

        wazeIntent.setPackage("com.waze");

        try {

            startActivity(wazeIntent);
            return;

        } catch (Exception ignored) {
            // Waze is not installed.
        }

        String mapsUrl =
                "https://www.google.com/maps/dir/?api=1" +
                        "&destination=" +
                        coordinates;

        Intent mapsIntent =
                new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(mapsUrl)
                );

        try {

            startActivity(mapsIntent);

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "לא נמצאה אפליקציית ניווט",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =============================================================
    // Features
    // =============================================================

    private void buildFeatures(
            Map<String, Boolean> features) {

        featuresContainer.removeAllViews();

        if (features == null ||
                features.isEmpty()) {

            addEmptyState(
                    featuresContainer,
                    "לא הוגדרו מאפיינים"
            );

            return;
        }

        Map<String, String> names =
                new java.util.HashMap<>();

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

        boolean found = false;

        for (Map.Entry<String, Boolean> entry :
                features.entrySet()) {

            if (!Boolean.TRUE.equals(
                    entry.getValue())) {

                continue;
            }

            found = true;

            String key =
                    entry.getKey();

            String name =
                    names.get(key);

            if (TextUtils.isEmpty(name)) {
                name = key;
            }

            addFeatureChip(name);
        }

        if (!found) {

            addEmptyState(
                    featuresContainer,
                    "לא הוגדרו מאפיינים"
            );
        }
    }

    private void addFeatureChip(
            String name) {

        TextView chip =
                new TextView(
                        requireContext()
                );

        chip.setText(
                "✓  " + name
        );

        chip.setTextSize(14);

        chip.setTextColor(
                Color.rgb(
                        25,
                        103,
                        210
                )
        );

        chip.setGravity(
                Gravity.CENTER_VERTICAL
        );

        chip.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );

        chip.setBackgroundResource(
                R.drawable.bg_feature_chip
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
                dp(8)
        );

        chip.setLayoutParams(params);

        featuresContainer.addView(chip);
    }

    // =============================================================
    // Empty State
    // =============================================================

    private void addEmptyState(
            LinearLayout container,
            String message) {

        TextView empty =
                new TextView(
                        requireContext()
                );

        empty.setText(message);
        empty.setTextSize(15);

        empty.setTextColor(
                Color.rgb(
                        111,
                        123,
                        135
                )
        );

        empty.setGravity(
                Gravity.CENTER
        );

        empty.setPadding(
                dp(16),
                dp(20),
                dp(16),
                dp(20)
        );

        container.addView(empty);
    }

    // =============================================================
    // Google Maps
    // =============================================================

    @Override
    public void onMapReady(
            @NonNull GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings()
                .setZoomControlsEnabled(true);

        googleMap.getUiSettings()
                .setMapToolbarEnabled(true);

        if (!hasLocation()) {
            return;
        }

        LatLng location =
                new LatLng(
                        latitude,
                        longitude
                );

        googleMap.clear();

        googleMap.addMarker(
                new MarkerOptions()
                        .position(location)
                        .title("בית הכנסת")
        );

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        location,
                        16f
                )
        );
    }
}
