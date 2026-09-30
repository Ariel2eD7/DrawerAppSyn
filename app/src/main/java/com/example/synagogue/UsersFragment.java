package com.example.synagogue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawerappsyn.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class UsersFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private RecyclerView recyclerView;
    private SynagogueAdapter adapter;
    private ArrayList<Synagogue> synagogueList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private GoogleMap googleMap;

    public UsersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.example.drawerappsyn.R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(com.example.drawerappsyn.R.id.buttonLogin).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(com.example.drawerappsyn.R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null).commit());

        view.findViewById(com.example.drawerappsyn.R.id.buttonAddSynagogue).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(com.example.drawerappsyn.R.id.fragment_container, new AddSynagogue01WelcomeFragment())
                        .addToBackStack(null).commit());

        recyclerView = view.findViewById(com.example.drawerappsyn.R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SynagogueAdapter(synagogueList, synagogue ->
                getParentFragmentManager().beginTransaction()
                        .replace(com.example.drawerappsyn.R.id.fragment_container,
                                SynagogueDetailsFragment.newInstance(
                                        synagogue.getName(),
                                        synagogue.getAddress(),
                                        synagogue.getPhone(),
                                        synagogue.getUsername(),
                                        synagogue.getLatitude(),
                                        synagogue.getLongitude(),
                                        synagogue.getOpeningHours()))
                        .addToBackStack(null).commit());

        recyclerView.setAdapter(adapter);
        firestore = FirebaseFirestore.getInstance();

        SupportMapFragment map = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        if (map != null) map.getMapAsync(this);

        loadSynagogues();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(31.7683, 35.2137), 12f));

        enableUserLocation();
        addSynagogueMarkers();
    }

    private void enableUserLocation() {
        if (googleMap == null) return;

        boolean fine = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean coarse = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fine || coarse) {
            try {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) return;

        for (int result : results) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                return;
            }
        }

        Toast.makeText(requireContext(),
                "לא ניתנה הרשאת מיקום", Toast.LENGTH_LONG).show();
    }

    private void loadSynagogues() {
        firestore.collection("synagogues").get()
                .addOnSuccessListener(snapshot -> {
                    synagogueList.clear();

                    for (var document : snapshot.getDocuments()) {
                        Synagogue synagogue = document.toObject(Synagogue.class);

                        if (synagogue != null) {
                            synagogue.setId(document.getId());
                            synagogueList.add(synagogue);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    addSynagogueMarkers();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "שגיאה בטעינת בתי הכנסת: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
    }

    private void addSynagogueMarkers() {
        if (googleMap == null) return;

        googleMap.clear();

        boolean first = true;

        for (Synagogue synagogue : synagogueList) {
            double lat = synagogue.getLatitude();
            double lng = synagogue.getLongitude();

            if (lat == 0 && lng == 0) continue;

            LatLng position = new LatLng(lat, lng);

            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(synagogue.getName())
                    .snippet(synagogue.getAddress()));

            if (first) {
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(position, 14f));
                first = false;
            }
        }
    }
}
