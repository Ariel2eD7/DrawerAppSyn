package com.example.synagogue;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drawerappsyn.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UsersFragment extends Fragment
        implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private RecyclerView recyclerView;
    private EditText searchInput;
    private TextView resultCount;
    private TextView emptyState;
    private TextView loadingText;

    private FirebaseFirestore firestore;
    private GoogleMap googleMap;

    private final ArrayList<Synagogue> allSynagogues =
            new ArrayList<>();

    private final ArrayList<Synagogue> visibleSynagogues =
            new ArrayList<>();

    private SynagogueAdapter adapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_users,
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

        firestore =
                FirebaseFirestore.getInstance();

        searchInput =
                view.findViewById(
                        R.id.searchInput
                );

        resultCount =
                view.findViewById(
                        R.id.resultCount
                );

        emptyState =
                view.findViewById(
                        R.id.emptyState
                );

        loadingText =
                view.findViewById(
                        R.id.loadingText
                );

        recyclerView =
                view.findViewById(
                        R.id.recyclerViewUsers
                );

        setupRecyclerView();
        setupSearch();
        setupMap();
        loadSynagogues();
    }


    private void setupRecyclerView() {

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerView.setHasFixedSize(false);

        adapter =
                new SynagogueAdapter(
                        visibleSynagogues
                );

        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {

        searchInput.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterSynagogues(
                                s == null
                                        ? ""
                                        : s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void setupMap() {

        SupportMapFragment map =
                (SupportMapFragment)
                        getChildFragmentManager()
                                .findFragmentById(
                                        R.id.map
                                );

        if (map != null) {
            map.getMapAsync(this);
        }
    }

    private void loadSynagogues() {

        showLoading(true);

        firestore
                .collection("synagogues_v2")
                .whereEqualTo(
                        "status",
                        "active"
                )
                .get()
                .addOnSuccessListener(snapshot -> {

                    allSynagogues.clear();

                    for (var document :
                            snapshot.getDocuments()) {

                        Synagogue synagogue =
                                document.toObject(
                                        Synagogue.class
                                );

                        if (synagogue == null) {
                            continue;
                        }

                        synagogue.setId(
                                document.getId()
                        );

                        allSynagogues.add(
                                synagogue
                        );
                    }

                    sortSynagogues();

                    filterSynagogues(
                            searchInput
                                    .getText()
                                    .toString()
                    );

                    showLoading(false);

                    addSynagogueMarkers();
                })
                .addOnFailureListener(e -> {

                    showLoading(false);

                    Toast.makeText(
                            requireContext(),
                            "לא הצלחנו לטעון את בתי הכנסת",
                            Toast.LENGTH_LONG
                    ).show();

                    emptyState.setText(
                            "לא הצלחנו לטעון את הרשימה.\n"
                                    + "נסה שוב בעוד רגע."
                    );

                    emptyState.setVisibility(
                            View.VISIBLE
                    );
                });
    }

    private void sortSynagogues() {

        Collections.sort(
                allSynagogues,
                new Comparator<Synagogue>() {

                    @Override
                    public int compare(
                            Synagogue first,
                            Synagogue second) {

                        String firstName =
                                first.getName() == null
                                        ? ""
                                        : first.getName();

                        String secondName =
                                second.getName() == null
                                        ? ""
                                        : second.getName();

                        return firstName.compareToIgnoreCase(
                                secondName
                        );
                    }
                }
        );
    }

    private void filterSynagogues(
            String query) {

        String normalized =
                query == null
                        ? ""
                        : query.trim()
                        .toLowerCase();

        visibleSynagogues.clear();

        for (Synagogue synagogue :
                allSynagogues) {

            if (matchesSearch(
                    synagogue,
                    normalized
            )) {

                visibleSynagogues.add(
                        synagogue
                );
            }
        }

        adapter.notifyDataSetChanged();

        updateResultState();

        addSynagogueMarkers();
    }

    private boolean matchesSearch(
            Synagogue synagogue,
            String query) {

        if (query.isEmpty()) {
            return true;
        }

        return contains(
                synagogue.getName(),
                query
        )
                || contains(
                synagogue.getAddress(),
                query
        )
                || contains(
                synagogue.getDescription(),
                query
        );
    }

    private boolean contains(
            String value,
            String query) {

        if (value == null) {
            return false;
        }

        return value
                .toLowerCase()
                .contains(query);
    }

    private void updateResultState() {

        int count =
                visibleSynagogues.size();

        if (count == 0) {

            resultCount.setText(
                    "לא נמצאו בתי כנסת"
            );

            emptyState.setText(
                    "לא מצאנו בתי כנסת שמתאימים לחיפוש.\n"
                            + "נסה לחפש לפי שם או כתובת."
            );

            emptyState.setVisibility(
                    View.VISIBLE
            );

            recyclerView.setVisibility(
                    View.GONE
            );

        } else {

            resultCount.setText(
                    count == 1
                            ? "בית כנסת אחד"
                            : count
                            + " בתי כנסת"
            );

            emptyState.setVisibility(
                    View.GONE
            );

            recyclerView.setVisibility(
                    View.VISIBLE
            );
        }
    }

    private void showLoading(
            boolean loading) {

        if (loading) {

            loadingText.setVisibility(
                    View.VISIBLE
            );

            emptyState.setVisibility(
                    View.GONE
            );

        } else {

            loadingText.setVisibility(
                    View.GONE
            );
        }
    }

    @Override
    public void onMapReady(
            @NonNull GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings()
                .setZoomControlsEnabled(false);

        googleMap.getUiSettings()
                .setMapToolbarEnabled(true);

        googleMap.getUiSettings()
                .setCompassEnabled(true);

        LatLng israel =
                new LatLng(
                        31.7683,
                        35.2137
                );

        googleMap.moveCamera(
                CameraUpdateFactory
                        .newLatLngZoom(
                                israel,
                                10f
                        )
        );

        enableUserLocation();

        addSynagogueMarkers();
    }

    private void addSynagogueMarkers() {

        if (googleMap == null) {
            return;
        }

        googleMap.clear();

        boolean firstMarker = true;

        for (Synagogue synagogue :
                visibleSynagogues) {

            if (!synagogue.hasLocation()) {
                continue;
            }

            LatLng position =
                    new LatLng(
                            synagogue.getLatitude(),
                            synagogue.getLongitude()
                    );

            googleMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .title(
                                    safeText(
                                            synagogue.getName(),
                                            "בית כנסת"
                                    )
                            )
                            .snippet(
                                    safeText(
                                            synagogue.getAddress(),
                                            ""
                                    )
                            )
            );

            if (firstMarker) {

                googleMap.animateCamera(
                        CameraUpdateFactory
                                .newLatLngZoom(
                                        position,
                                        13f
                                )
                );

                firstMarker = false;
            }
        }
    }

    private void enableUserLocation() {

        if (googleMap == null) {
            return;
        }

        boolean fine =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission
                                .ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        boolean coarse =
                ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;

        if (fine || coarse) {

            try {

                googleMap.setMyLocationEnabled(
                        true
                );

                googleMap.getUiSettings()
                        .setMyLocationButtonEnabled(
                                true
                        );

            } catch (SecurityException ignored) {
            }

        } else {

            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION,
                            Manifest.permission
                                    .ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] results) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                results
        );

        if (requestCode !=
                LOCATION_PERMISSION_REQUEST_CODE) {

            return;
        }

        for (int result : results) {

            if (result ==
                    PackageManager.PERMISSION_GRANTED) {

                enableUserLocation();

                return;
            }
        }
    }

    private String safeText(
            String value,
            String fallback) {

        if (value == null ||
                value.trim().isEmpty()) {

            return fallback;
        }

        return value.trim();
    }

    /**
     * פתיחת מסך פרטי בית הכנסת.
     */
    private void openSynagogueDetails(
            Synagogue synagogue) {

        if (synagogue == null) {

            Toast.makeText(
                    requireContext(),
                    "לא נמצאו פרטי בית הכנסת",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        SynagogueDetailsFragment
                                .newInstance(
                                        synagogue
                                )
                )
                .addToBackStack(null)
                .commit();
    }

    private class SynagogueAdapter
            extends RecyclerView.Adapter<
            SynagogueAdapter.SynagogueViewHolder> {

        private final ArrayList<Synagogue> list;

        SynagogueAdapter(
                ArrayList<Synagogue> list) {

            this.list = list;
        }

        @NonNull
        @Override
        public SynagogueViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType) {

            View view =
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(
                                    R.layout.item_user,
                                    parent,
                                    false
                            );

            return new SynagogueViewHolder(
                    view
            );
        }

        @Override
        public void onBindViewHolder(
                @NonNull SynagogueViewHolder holder,
                int position) {

            Synagogue synagogue =
                    list.get(position);

            holder.name.setText(
                    safeText(
                            synagogue.getName(),
                            "בית כנסת"
                    )
            );

            String address =
                    safeText(
                            synagogue.getAddress(),
                            "כתובת לא הוגדרה"
                    );

            holder.address.setText(
                    "📍  " + address
            );

            String description =
                    safeText(
                            synagogue.getDescription(),
                            ""
                    );

            if (description.isEmpty()) {

                holder.description.setVisibility(
                        View.GONE
                );

            } else {

                holder.description.setText(
                        description
                );

                holder.description.setVisibility(
                        View.VISIBLE
                );
            }

            int featureCount =
                    synagogue.getFeatureCount();

            int prayerCount =
                    synagogue.getPrayerCount();

            if (featureCount > 0 ||
                    prayerCount > 0) {

                holder.meta.setVisibility(
                        View.VISIBLE
                );

                StringBuilder meta =
                        new StringBuilder();

                if (prayerCount > 0) {

                    meta.append("🕐 ")
                            .append(prayerCount)
                            .append(
                                    prayerCount == 1
                                            ? " שעה"
                                            : " שעות"
                            );
                }

                if (featureCount > 0) {

                    if (meta.length() > 0) {
                        meta.append("   •   ");
                    }

                    meta.append("✓ ")
                            .append(featureCount)
                            .append(" מאפיינים");
                }

                holder.meta.setText(
                        meta.toString()
                );

            } else {

                holder.meta.setVisibility(
                        View.GONE
                );
            }

            // כאן היה ה-Toast.
            // עכשיו באמת פותחים את מסך הפרטים.
            holder.itemView.setOnClickListener(
                    v -> openSynagogueDetails(
                            synagogue
                    )
            );
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class SynagogueViewHolder
                extends RecyclerView.ViewHolder {

            TextView name;
            TextView address;
            TextView description;
            TextView meta;

            SynagogueViewHolder(
                    @NonNull View itemView) {

                super(itemView);

                name =
                        itemView.findViewById(
                                R.id.textName
                        );

                address =
                        itemView.findViewById(
                                R.id.textAddress
                        );

                description =
                        itemView.findViewById(
                                R.id.textDescription
                        );

                meta =
                        itemView.findViewById(
                                R.id.textMeta
                        );
            }
        }
    }
}
