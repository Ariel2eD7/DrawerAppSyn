        package com.example.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView profileName;
    private TextView profileDescription;
    private TextView profileAddress;
    private TextView profilePhone;
    private TextView profilePrayers;
    private TextView profileFeatures;
    private TextView profileStatus;

    private ProgressBar progressBar;

    private MaterialButton editProfileButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // ==========================================
        // חיבור Views
        // ==========================================

        profileName =
                view.findViewById(R.id.profileName);

        profileDescription =
                view.findViewById(R.id.profileDescription);

        profileAddress =
                view.findViewById(R.id.profileAddress);

        profilePhone =
                view.findViewById(R.id.profilePhone);

        profilePrayers =
                view.findViewById(R.id.profilePrayers);

        profileFeatures =
                view.findViewById(R.id.profileFeatures);

        profileStatus =
                view.findViewById(R.id.profileStatus);

        progressBar =
                view.findViewById(R.id.profileProgressBar);

        editProfileButton =
                view.findViewById(R.id.editProfileButton);

        // ==========================================
        // Firebase
        // ==========================================

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        // ==========================================
        // כפתור עריכה
        // ==========================================

        editProfileButton.setOnClickListener(v -> {

            Toast.makeText(
                    requireContext(),
                    "מסך העריכה יתווסף בשלב הבא",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // ==========================================
        // טעינת הפרופיל
        // ==========================================

        loadProfile();
    }

    // ==========================================
    // הסתרת Toolbar
    // ==========================================

    @Override
    public void onResume() {

        super.onResume();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(false);
    }

    // ==========================================
    // החזרת Toolbar
    // ==========================================

    @Override
    public void onPause() {

        super.onPause();

        MainActivity activity =
                (MainActivity) requireActivity();

        activity.setToolbarVisible(true);
    }

    // ==========================================
    // טעינת נתוני המשתמש
    // ==========================================

    private void loadProfile() {

        FirebaseUser currentUser =
                auth.getCurrentUser();

        // ------------------------------------------
        // אין משתמש מחובר
        // ------------------------------------------

        if (currentUser == null) {

            showError(
                    "לא נמצא משתמש מחובר."
            );

            return;
        }

        String uid =
                currentUser.getUid();

        showLoading(true);

        // ==========================================
        // קריאת בית הכנסת
        // ==========================================

        db.collection("synagogues_v2")
                .document(uid)
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {

                            showLoading(false);

                            if (!documentSnapshot.exists()) {

                                showError(
                                        "לא נמצא בית כנסת המשויך לחשבון."
                                );

                                return;
                            }

                            displayProfile(
                                    documentSnapshot
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            showLoading(false);

                            showError(
                                    "טעינת הפרופיל נכשלה:\n"
                                            + e.getMessage()
                            );
                        }
                );
    }

    // ==========================================
    // הצגת פרטי בית הכנסת
    // ==========================================

    private void displayProfile(
            DocumentSnapshot document) {

        // ==========================================
        // פרטים בסיסיים
        // ==========================================

        String name =
                document.getString("name");

        String description =
                document.getString("description");

        String address =
                document.getString("address");

        String phone =
                document.getString("phone");

        String status =
                document.getString("status");

        // ==========================================
        // שם
        // ==========================================

        profileName.setText(
                valueOrDefault(
                        name,
                        "בית הכנסת"
                )
        );

        // ==========================================
        // תיאור
        // ==========================================

        profileDescription.setText(
                valueOrDefault(
                        description,
                        "לא נוסף תיאור."
                )
        );

        // ==========================================
        // כתובת
        // ==========================================

        profileAddress.setText(
                valueOrDefault(
                        address,
                        "לא הוגדרה כתובת."
                )
        );

        // ==========================================
        // טלפון
        // ==========================================

        profilePhone.setText(
                valueOrDefault(
                        phone,
                        "לא הוגדר טלפון."
                )
        );

        // ==========================================
        // סטטוס
        // ==========================================

        if ("active".equals(status)) {

            profileStatus.setText(
                    "פעיל"
            );

        } else {

            profileStatus.setText(
                    valueOrDefault(
                            status,
                            "לא ידוע"
                    )
            );
        }

        // ==========================================
        // תפילות
        // ==========================================

        Object prayersObject =
                document.get("prayers");

        profilePrayers.setText(
                buildPrayerText(
                        prayersObject
                )
        );

        // ==========================================
        // מאפיינים
        // ==========================================

        Object featuresObject =
                document.get("features");

        profileFeatures.setText(
                buildFeaturesText(
                        featuresObject
                )
        );
    }

    // ==========================================
    // בניית טקסט תפילות
    // ==========================================

    private String buildPrayerText(
            Object prayersObject) {

        if (!(prayersObject instanceof Map)) {

            return "לא הוגדרו שעות תפילה.";
        }

        Map<?, ?> days =
                (Map<?, ?>) prayersObject;

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

        StringBuilder result =
                new StringBuilder();

        boolean found =
                false;

        for (int i = 0;
             i < dayKeys.length;
             i++) {

            Object dayObject =
                    days.get(dayKeys[i]);

            if (!(dayObject instanceof Map)) {
                continue;
            }

            Map<?, ?> prayers =
                    (Map<?, ?>) dayObject;

            StringBuilder dayResult =
                    new StringBuilder();

            for (int j = 0;
                 j < prayerKeys.length;
                 j++) {

                Object timesObject =
                        prayers.get(
                                prayerKeys[j]
                        );

                if (!(timesObject instanceof List)) {
                    continue;
                }

                List<?> times =
                        (List<?>) timesObject;

                if (times.isEmpty()) {
                    continue;
                }

                if (dayResult.length() > 0) {
                    dayResult.append("\n");
                }

                dayResult.append(
                        prayerNames[j]
                );

                dayResult.append(": ");

                for (int k = 0;
                     k < times.size();
                     k++) {

                    if (k > 0) {
                        dayResult.append(", ");
                    }

                    dayResult.append(
                            String.valueOf(
                                    times.get(k)
                            )
                    );
                }
            }

            if (dayResult.length() > 0) {

                found = true;

                result.append(
                        "יום "
                );

                result.append(
                        dayNames[i]
                );

                result.append("\n");

                result.append(
                        dayResult
                );

                result.append("\n\n");
            }
        }

        if (!found) {

            return "לא הוגדרו שעות תפילה.";
        }

        return result.toString().trim();
    }

    // ==========================================
    // בניית טקסט מאפיינים
    // ==========================================

    private String buildFeaturesText(
            Object featuresObject) {

        if (!(featuresObject instanceof Map)) {

            return "לא נבחרו מאפיינים.";
        }

        Map<?, ?> features =
                (Map<?, ?>) featuresObject;

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

        String[] names = {
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

        StringBuilder result =
                new StringBuilder();

        boolean found =
                false;

        for (int i = 0;
             i < keys.length;
             i++) {

            Object value =
                    features.get(keys[i]);

            if (value instanceof Boolean &&
                    (Boolean) value) {

                found = true;

                result.append("• ");
                result.append(names[i]);
                result.append("\n");
            }
        }

        if (!found) {

            return "לא נבחרו מאפיינים.";
        }

        return result.toString().trim();
    }

    // ==========================================
    // ערך ברירת מחדל
    // ==========================================

    private String valueOrDefault(
            String value,
            String defaultValue) {

        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }

        return value;
    }

    // ==========================================
    // Loading
    // ==========================================

    private void showLoading(boolean loading) {

        if (progressBar != null) {

            progressBar.setVisibility(
                    loading
                            ? View.VISIBLE
                            : View.GONE
            );
        }

        if (editProfileButton != null) {

            editProfileButton.setEnabled(
                    !loading
            );
        }
    }

    // ==========================================
    // שגיאה
    // ==========================================

    private void showError(String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
