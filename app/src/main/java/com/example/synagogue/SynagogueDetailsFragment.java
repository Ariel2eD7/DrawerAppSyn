package com.example.synagogue;

import android.content.Intent;
import android.graphics.Color;
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

import com.example.drawerappsyn.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.drawerappsyn.MainActivity;

public class SynagogueDetailsFragment extends Fragment
        implements OnMapReadyCallback {

    private static final String ARG_SYNAGOGUE = "synagogue";

    private GoogleMap googleMap;

    private String phone = "";
    private double latitude = 0;
    private double longitude = 0;

    private LinearLayout prayersContainer;
    private LinearLayout featuresContainer;

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

        buttonBack.setOnClickListener(
                v -> goBack()
        );

        Synagogue synagogue = getSynagogue();

        if (synagogue == null) {

            Toast.makeText(
                    requireContext(),
                    "לא נמצאו פרטי בית הכנסת",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // ---------------------------------------------------------
        // Basic information
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // Description
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // Phone
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // Location
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // Prayers
        // ---------------------------------------------------------

        buildPrayers(
                synagogue.getPrayers()
        );

        // ---------------------------------------------------------
        // Features
        // ---------------------------------------------------------

        buildFeatures(
                synagogue.getFeatures()
        );

        // ---------------------------------------------------------
        // Map
        // ---------------------------------------------------------

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

        Bundle args = getArguments();

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
    // Navigation / Waze / Google Maps
    // =============================================================

    private void openNavigation() {

        if (!hasLocation()) {
            return;
        }

        String coordinates =
                latitude +
                        "," +
                        longitude;

        // Try Waze first
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

        // Fallback to Google Maps
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
    // Prayers
    // =============================================================

    private void buildPrayers(
            Map<String, Map<String, ArrayList<String>>> prayers) {

        prayersContainer.removeAllViews();

        if (prayers == null ||
                prayers.isEmpty()) {

            addEmptyState(
                    prayersContainer,
                    "לא הוגדרו זמני תפילות"
            );

            return;
        }

        String[] dayKeys = {
                "sunday",
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "shabbat"
        };

        String[] dayNames = {
                "ראשון",
                "שני",
                "שלישי",
                "רביעי",
                "חמישי",
                "שישי",
                "שבת"
        };

        String[] prayerKeys = {
                "shacharit",
                "mincha",
                "maariv",
                "kabbalatShabbat",
                "musaf",
                "havdalah"
        };

        String[] prayerNames = {
                "שחרית",
                "מנחה",
                "ערבית",
                "קבלת שבת",
                "מוסף",
                "הבדלה"
        };

        boolean found = false;

        for (int i = 0;
             i < dayKeys.length;
             i++) {

            Map<String, ArrayList<String>> dayPrayers =
                    prayers.get(dayKeys[i]);

            if (dayPrayers == null ||
                    dayPrayers.isEmpty()) {
                continue;
            }

            ArrayList<String> dayTimes =
                    new ArrayList<>();

            for (int j = 0;
                 j < prayerKeys.length;
                 j++) {

                ArrayList<String> times =
                        dayPrayers.get(
                                prayerKeys[j]
                        );

                if (times == null ||
                        times.isEmpty()) {
                    continue;
                }

                ArrayList<String> validTimes =
                        new ArrayList<>();

                for (String time : times) {

                    if (time == null) {
                        continue;
                    }

                    String cleanTime =
                            time.trim();

                    if (!cleanTime.isEmpty()) {
                        validTimes.add(cleanTime);
                    }
                }

                if (validTimes.isEmpty()) {
                    continue;
                }

                found = true;

                StringBuilder timeLine =
                        new StringBuilder();

                timeLine.append(
                        prayerNames[j]
                );

                timeLine.append(": ");

                for (int k = 0;
                     k < validTimes.size();
                     k++) {

                    if (k > 0) {
                        timeLine.append(", ");
                    }

                    timeLine.append(
                            validTimes.get(k)
                    );
                }

                dayTimes.add(
                        timeLine.toString()
                );
            }

            if (!dayTimes.isEmpty()) {

                addDayPrayerCard(
                        dayNames[i],
                        dayTimes
                );
            }
        }

        if (!found) {

            addEmptyState(
                    prayersContainer,
                    "לא הוגדרו זמני תפילות"
            );
        }
    }

    private void addDayPrayerCard(
            String dayName,
            ArrayList<String> times) {

        MaterialCardView card =
                new MaterialCardView(
                        requireContext()
                );

        card.setRadius(20);
        card.setCardElevation(0);
        card.setStrokeWidth(1);

        card.setStrokeColor(
                Color.rgb(
                        228,
                        234,
                        241
                )
        );

        LinearLayout content =
                new LinearLayout(
                        requireContext()
                );

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                18,
                16,
                18,
                16
        );

        TextView day =
                new TextView(
                        requireContext()
                );

        day.setText(
                "יום " + dayName
        );

        day.setTextSize(18);
        day.setTextColor(
                Color.rgb(
                        24,
                        34,
                        48
                )
        );

        day.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        day.setGravity(
                Gravity.START
        );

        content.addView(day);

        for (String time : times) {

            TextView prayer =
                    new TextView(
                            requireContext()
                    );

            prayer.setText(
                    "🕐  " + time
            );

            prayer.setTextSize(15);
            prayer.setTextColor(
                    Color.rgb(
                            75,
                            88,
                            102
                    )
            );

            prayer.setGravity(
                    Gravity.START
            );

            prayer.setPadding(
                    0,
                    9,
                    0,
                    0
            );

            content.addView(prayer);
        }

        card.addView(content);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                0,
                0,
                12
        );

        card.setLayoutParams(params);

        prayersContainer.addView(card);
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
                14,
                10,
                14,
                10
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
                8
        );

        chip.setLayoutParams(params);

        featuresContainer.addView(chip);
    }

    // =============================================================
    // Empty state
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
                16,
                20,
                16,
                20
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
