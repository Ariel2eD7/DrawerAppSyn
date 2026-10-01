package com.example.register;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class AddSynagogue03LocationFragment extends Fragment
        implements OnMapReadyCallback {

    private EditText editTextCity;
    private EditText editTextStreet;
    private EditText editTextHouseNumber;

    private GoogleMap googleMap;
    private Marker marker;

    private double latitude = 0;
    private double longitude = 0;

    public AddSynagogue03LocationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_add_synagogue_03_location,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        setupProgress(view, 3);

        editTextCity =
                view.findViewById(R.id.editTextCity);

        editTextStreet =
                view.findViewById(R.id.editTextStreet);

        editTextHouseNumber =
                view.findViewById(R.id.editTextHouseNumber);

        View buttonBack =
                view.findViewById(R.id.buttonBack);

        View buttonFindAddress =
                view.findViewById(R.id.buttonFindAddress);

        View buttonNext =
                view.findViewById(R.id.buttonNext);

        buttonBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        buttonFindAddress.setOnClickListener(v ->
                findAddress()
        );

        buttonNext.setOnClickListener(v ->
                continueToNextStep()
        );

        SupportMapFragment mapFragment =
                (SupportMapFragment)
                        getChildFragmentManager()
                                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {

        googleMap = map;

        LatLng defaultLocation =
                new LatLng(31.7683, 35.2137);

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        defaultLocation,
                        11f
                )
        );

        googleMap.getUiSettings()
                .setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(latLng -> {

            setMarkerPosition(latLng);

            Toast.makeText(
                    requireContext(),
                    "המיקום עודכן",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void findAddress() {

        String city =
                editTextCity.getText()
                        .toString()
                        .trim();

        String street =
                editTextStreet.getText()
                        .toString()
                        .trim();

        String houseNumber =
                editTextHouseNumber.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(city)) {

            editTextCity.setError(
                    "יש להזין עיר"
            );

            editTextCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(street)) {

            editTextStreet.setError(
                    "יש להזין רחוב"
            );

            editTextStreet.requestFocus();
            return;
        }

        if (googleMap == null) {

            Toast.makeText(
                    requireContext(),
                    "המפה עדיין נטענת",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String addressText =
                street
                        + " "
                        + houseNumber
                        + ", "
                        + city
                        + ", Israel";

        Geocoder geocoder =
                new Geocoder(
                        requireContext(),
                        Locale.getDefault()
                );

        try {

            List<Address> addresses =
                    geocoder.getFromLocationName(
                            addressText,
                            1
                    );

            if (addresses == null ||
                    addresses.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "לא הצלחנו למצוא את הכתובת",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            Address address =
                    addresses.get(0);

            latitude =
                    address.getLatitude();

            longitude =
                    address.getLongitude();

            LatLng position =
                    new LatLng(
                            latitude,
                            longitude
                    );

            setMarkerPosition(position);

            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            position,
                            17f
                    )
            );

            Toast.makeText(
                    requireContext(),
                    "הכתובת נמצאה בהצלחה",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (IOException e) {

            Toast.makeText(
                    requireContext(),
                    "אירעה שגיאה בחיפוש הכתובת",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void setMarkerPosition(
            LatLng position) {

        latitude =
                position.latitude;

        longitude =
                position.longitude;

        if (marker != null) {
            marker.remove();
        }

        marker =
                googleMap.addMarker(
                        new MarkerOptions()
                                .position(position)
                                .title("בית הכנסת")
                                .draggable(true)
                );

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLng(
                        position
                )
        );
    }

    private void continueToNextStep() {

        String city =
                editTextCity.getText()
                        .toString()
                        .trim();

        String street =
                editTextStreet.getText()
                        .toString()
                        .trim();

        String houseNumber =
                editTextHouseNumber.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(city)) {

            editTextCity.setError(
                    "יש להזין עיר"
            );

            editTextCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(street)) {

            editTextStreet.setError(
                    "יש להזין רחוב"
            );

            editTextStreet.requestFocus();
            return;
        }

        if (latitude == 0 && longitude == 0) {

            Toast.makeText(
                    requireContext(),
                    "יש למצוא את הכתובת או לבחור מיקום במפה",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        /*
         * ממשיכים עם אותו Bundle שקיבלנו
         * מהמסך הקודם.
         */
        Bundle oldData = getArguments();

        final Bundle data;

        if (oldData == null) {
            data = new Bundle();
        } else {
            data = new Bundle(oldData);
        }

        /*
         * שומרים את חלקי הכתובת בנפרד.
         */
        data.putString(
                "city",
                city
        );

        data.putString(
                "street",
                street
        );

        data.putString(
                "houseNumber",
                houseNumber
        );

        /*
         * בנוסף שומרים כתובת מלאה
         * עבור מסך הסיכום ו-Firebase.
         */
        String fullAddress;

        if (TextUtils.isEmpty(houseNumber)) {

            fullAddress =
                    street + ", " + city;

        } else {

            fullAddress =
                    street + " " + houseNumber + ", " + city;
        }

        data.putString(
                "synagogueAddress",
                fullAddress
        );

        /*
         * מיקום במפה.
         */
        data.putDouble(
                "latitude",
                latitude
        );

        data.putDouble(
                "longitude",
                longitude
        );

        /*
         * מעבר למסך התפילות.
         */
        AddSynagogue04PrayerFragment nextFragment =
                new AddSynagogue04PrayerFragment();

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