package com.example.synagogue;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class SynagogueDetailsFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_NAME = "name", ARG_ADDRESS = "address",
            ARG_PHONE = "phone", ARG_OPENING_HOURS = "openingHours",
            ARG_LATITUDE = "latitude", ARG_LONGITUDE = "longitude";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap googleMap;
    private String phoneNumber = "";
    private double latitude, longitude;

    public SynagogueDetailsFragment() {}

    public static SynagogueDetailsFragment newInstance(
            String name, String address, String phone, String username,
            double latitude, double longitude,
            ArrayList<HashMap<String, String>> openingHours) {

        SynagogueDetailsFragment f = new SynagogueDetailsFragment();
        Bundle b = new Bundle();

        b.putString(ARG_NAME, name);
        b.putString(ARG_ADDRESS, address);
        b.putString(ARG_PHONE, phone);
        b.putSerializable(ARG_OPENING_HOURS, openingHours);
        b.putDouble(ARG_LATITUDE, latitude);
        b.putDouble(ARG_LONGITUDE, longitude);

        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_synagogue_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameView = view.findViewById(R.id.textDetailName);
        TextView addressView = view.findViewById(R.id.textDetailAddress);
        LinearLayout hoursContainer = view.findViewById(R.id.openingHoursContainer);
        Button callButton = view.findViewById(R.id.buttonCall);
        LinearLayout navigateButton = view.findViewById(R.id.buttonNavigate);

        Bundle b = getArguments();
        if (b == null) return;

        String name = b.getString(ARG_NAME, "");
        String address = b.getString(ARG_ADDRESS, "");
        phoneNumber = b.getString(ARG_PHONE, "").trim();
        latitude = b.getDouble(ARG_LATITUDE, 0);
        longitude = b.getDouble(ARG_LONGITUDE, 0);

        nameView.setText(name.isEmpty() ? "בית כנסת" : name);
        addressView.setText(address.isEmpty() ? "לא הוזנה כתובת" : address);
        addressView.setTextColor(address.isEmpty() ? Color.GRAY : Color.rgb(32, 33, 36));

        if (phoneNumber.isEmpty()) {
            callButton.setText("📞  לא הוזן טלפון");
            callButton.setEnabled(false);
            callButton.setAlpha(.5f);
        } else {
            callButton.setText("📞  " + phoneNumber);
            callButton.setOnClickListener(v -> callPhone());
        }

        if (latitude == 0 && longitude == 0) {
            navigateButton.setEnabled(false);
            navigateButton.setAlpha(.5f);
        } else {
            navigateButton.setOnClickListener(v -> openNavigation());
        }

        ArrayList<HashMap<String, String>> hours =
                (ArrayList<HashMap<String, String>>) b.getSerializable(ARG_OPENING_HOURS);

        if (hours == null || hours.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("שעות הפתיחה לא הוזנו");
            empty.setTextSize(16);
            empty.setTextColor(Color.GRAY);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(16, 20, 16, 20);
            hoursContainer.addView(empty);
        } else {
            for (HashMap<String, String> hour : hours) {
                if (hour == null) continue;

                if ("header".equals(hour.get("type"))) {
                    addHeaderView(hoursContainer, hour.get("text"));
                } else if ("normal".equals(hour.get("type"))) {
                    addOpeningHourView(
                            hoursContainer,
                            hour.get("title"),
                            hour.get("content")
                    );
                }
            }
        }

        SupportMapFragment map =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.detailMap);

        if (map != null) map.getMapAsync(this);
    }

    private void callPhone() {
        if (phoneNumber.isEmpty()) return;

        startActivity(new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + Uri.encode(phoneNumber))
        ));
    }

    private void openNavigation() {
        if (latitude == 0 && longitude == 0) return;

        String url = "https://waze.com/ul?ll=" +
                latitude + "," + longitude + "&navigate=yes";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.waze");

        try {
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void addOpeningHourView(
            LinearLayout container, String title, String content) {

        LinearLayout row = createRow();

        TextView titleView = new TextView(requireContext());
        titleView.setText(title == null ? "" : title);
        titleView.setTextSize(16);
        titleView.setTextColor(Color.rgb(45, 45, 45));
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        TextView contentView = new TextView(requireContext());
        contentView.setText(content == null ? "" : content);
        contentView.setTextSize(16);
        contentView.setTextColor(Color.rgb(80, 80, 80));
        contentView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(0, 0, 8, 0);
        titleView.setLayoutParams(p);

        p = new LinearLayout.LayoutParams(0, -2, 1);
        p.setMargins(8, 0, 0, 0);
        contentView.setLayoutParams(p);

        row.addView(titleView);
        row.addView(contentView);
        container.addView(row);
    }

    private LinearLayout createRow() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(16, 14, 16, 14);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(18);
        bg.setStroke(1, Color.rgb(232, 234, 237));
        row.setBackground(bg);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 0, 0, 8);
        row.setLayoutParams(p);

        return row;
    }

    private void addHeaderView(
            LinearLayout container, String text) {

        if (text == null || text.trim().isEmpty()) return;

        View spacer = new View(requireContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(-1, 10));
        container.addView(spacer);

        LinearLayout row = createHeaderRow();

        View line = new View(requireContext());
        line.setBackgroundColor(Color.rgb(25, 118, 210));

        LinearLayout.LayoutParams lineParams =
                new LinearLayout.LayoutParams(5, 36);
        lineParams.setMargins(0, 0, 12, 0);
        row.addView(line, lineParams);

        TextView title = new TextView(requireContext());
        title.setText(text.trim());
        title.setTextSize(18);
        title.setTextColor(Color.rgb(25, 75, 120));
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(0, -2, 1);
        titleParams.setMargins(0, 0, 12, 0);
        row.addView(title, titleParams);

        container.addView(row);
    }

    private LinearLayout createHeaderRow() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(16, 14, 16, 14);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(227, 242, 253));
        bg.setCornerRadius(16);
        bg.setStroke(1, Color.rgb(187, 222, 251));
        row.setBackground(bg);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 0, 0, 8);
        row.setLayoutParams(p);

        return row;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        Bundle b = getArguments();
        if (b == null) return;

        double lat = b.getDouble(ARG_LATITUDE, 0);
        double lng = b.getDouble(ARG_LONGITUDE, 0);

        if (lat != 0 || lng != 0) {
            LatLng location = new LatLng(lat, lng);

            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(b.getString(ARG_NAME, "בית כנסת")));

            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(location, 16f)
            );
        }

        enableUserLocation();
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException ignored) {}
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) return;

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                break;
            }
        }
    }
}
