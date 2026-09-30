package com.example.synagogue;

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

     private TextView summaryText;

    private MaterialButton buttonBack;
    private MaterialButton buttonSave;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public AddSynagogue06SummaryFragment() {
        // Required empty public constructor
    }

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

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        summaryText =
                view.findViewById(R.id.summaryText);

        buttonBack =
                view.findViewById(R.id.buttonBack);

        buttonSave =
                view.findViewById(R.id.buttonSave);

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        showSummary();

        buttonBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        buttonSave.setOnClickListener(v ->
                saveSynagogue()
        );
    }




    private void showSummary() {

        Bundle data = getArguments();

        if (data == null) {

            summaryText.setText(
                    "לא נמצאו נתונים להרשמה."
            );

            buttonSave.setEnabled(false);

            return;
        }

        StringBuilder summary =
                new StringBuilder();

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

        /*
         * שם בית הכנסת
         */

        summary.append("שם בית הכנסת\n");
        summary.append(
                emptyOrValue(name)
        );

        summary.append("\n\n");

        /*
         * תיאור
         */

        summary.append("תיאור\n");
        summary.append(
                emptyOrValue(description)
        );

        summary.append("\n\n");

        /*
         * כתובת
         */

        summary.append("כתובת\n");
        summary.append(
                emptyOrValue(address)
        );

        summary.append("\n\n");

        /*
         * טלפון
         */

        summary.append("טלפון\n");
        summary.append(
                emptyOrValue(phone)
        );

        summary.append("\n\n");

        /*
         * מיקום
         */

        summary.append("מיקום\n");

        if (latitude != 0 || longitude != 0) {

            summary.append(latitude);
            summary.append(", ");
            summary.append(longitude);

        } else {

            summary.append("לא הוגדר");
        }

        summary.append("\n\n");

        /*
         * תפילות
         */

        summary.append("תפילות\n");

        appendPrayerSummary(
                summary,
                data
        );

        summary.append("\n");

        /*
         * מאפיינים
         */

        summary.append("מאפיינים\n");

        appendFeaturesSummary(
                summary,
                data
        );

        summaryText.setText(
                summary.toString()
        );
    }





    private String emptyOrValue(String value) {

        if (TextUtils.isEmpty(value)) {
            return "-";
        }

        return value;
    }

    private void appendPrayerSummary(
            StringBuilder summary,
            Bundle data) {

        Object prayerObject =
                data.getSerializable(
                        "prayerData"
                );

        if (!(prayerObject instanceof Map)) {

            summary.append(
                    "לא הוגדרו תפילות\n"
            );

            return;
        }

        Map<?, ?> days =
                (Map<?, ?>) prayerObject;

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

        String[] prayerNames = {
                "shacharit",
                "mincha",
                "maariv",
                "kabbalatShabbat",
                "musaf",
                "havdalah"
        };

        String[] prayerHebrewNames = {
                "שחרית",
                "מנחה",
                "ערבית",
                "קבלת שבת",
                "מוסף",
                "הבדלה"
        };

        boolean foundAny =
                false;

        for (int i = 0; i < dayKeys.length; i++) {

            Object dayObject =
                    days.get(dayKeys[i]);

            if (!(dayObject instanceof Map)) {
                continue;
            }

            Map<?, ?> prayers =
                    (Map<?, ?>) dayObject;

            StringBuilder daySummary =
                    new StringBuilder();

            for (int j = 0;
                 j < prayerNames.length;
                 j++) {

                Object timesObject =
                        prayers.get(
                                prayerNames[j]
                        );

                if (!(timesObject instanceof ArrayList)) {
                    continue;
                }

                ArrayList<?> times =
                        (ArrayList<?>) timesObject;

                if (times.isEmpty()) {
                    continue;
                }

                if (daySummary.length() > 0) {
                    daySummary.append("\n");
                }

                daySummary.append(
                        prayerHebrewNames[j]
                );

                daySummary.append(": ");

                for (int k = 0;
                     k < times.size();
                     k++) {

                    if (k > 0) {
                        daySummary.append(", ");
                    }

                    daySummary.append(
                            String.valueOf(
                                    times.get(k)
                            )
                    );
                }
            }

            if (daySummary.length() > 0) {

                foundAny = true;

                summary.append(
                        dayNames[i]
                );

                summary.append("\n");

                summary.append(
                        daySummary
                );

                summary.append("\n\n");
            }
        }

        if (!foundAny) {

            summary.append(
                    "לא הוגדרו תפילות\n"
            );
        }
    }

    private void appendFeaturesSummary(
            StringBuilder summary,
            Bundle data) {

        ArrayList<String> selected =
                data.getStringArrayList(
                        "synagogueFeatures"
                );

        if (selected == null ||
                selected.isEmpty()) {

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

        for (String key : selected) {

            String name =
                    names.get(key);

            if (name == null) {
                name = key;
            }

            summary.append("• ");
            summary.append(name);
            summary.append("\n");
        }
    }

    private void saveSynagogue() {

        Bundle data = getArguments();

        if (data == null) {

            showError(
                    "לא נמצאו נתוני הרשמה"
            );

            return;
        }

        String name =
                data.getString(
                        "synagogueName",
                        ""
                ).trim();

        String address =
                data.getString(
                        "synagogueAddress",
                        ""
                ).trim();

        String username =
                data.getString(
                        "username",
                        ""
                ).trim();

        String password =
                data.getString(
                        "password",
                        ""
                );

        if (TextUtils.isEmpty(name)) {

            showError(
                    "חסר שם בית הכנסת"
            );

            return;
        }

        if (TextUtils.isEmpty(address)) {

            showError(
                    "חסרה כתובת"
            );

            return;
        }

        if (TextUtils.isEmpty(username)) {

            showError(
                    "חסר שם משתמש"
            );

            return;
        }

        if (TextUtils.isEmpty(password)) {

            showError(
                    "חסרה סיסמה"
            );

            return;
        }

        buttonSave.setEnabled(false);
        buttonBack.setEnabled(false);

        FirebaseUser currentUser =
                auth.getCurrentUser();

        if (currentUser != null) {

            saveToFirestore(
                    currentUser.getUid(),
                    data
            );

            return;
        }

        String email =
                username.toLowerCase()
                        + "@myapp.local";

        auth.createUserWithEmailAndPassword(
                email,
                password
        ).addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {

                buttonSave.setEnabled(true);
                buttonBack.setEnabled(true);

                String message =
                        task.getException() != null
                                ? task.getException()
                                .getMessage()
                                : "שגיאה לא ידועה";

                showError(
                        "יצירת המשתמש נכשלה: "
                                + message
                );

                return;
            }

            FirebaseUser user =
                    auth.getCurrentUser();

            if (user == null) {

                buttonSave.setEnabled(true);
                buttonBack.setEnabled(true);

                showError(
                        "המשתמש נוצר אך לא ניתן לקבל את פרטיו"
                );

                return;
            }

            saveToFirestore(
                    user.getUid(),
                    data
            );
        });
    }

    private void saveToFirestore(
            String uid,
            Bundle data) {

        Map<String, Object> synagogue =
                new HashMap<>();

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
                "username",
                data.getString(
                        "username",
                        ""
                )
        );

        synagogue.put(
                "description",
                data.getString("synagogueDescription", "")
        );


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

        /*
         * תפילות
         */

        synagogue.put(
                "prayers",
                buildPrayerData(data)
        );

        /*
         * מאפיינים
         */

        synagogue.put(
                "features",
                buildFeatures(data)
        );

        /*
         * מידע מערכת
         */

        synagogue.put(
                "status",
                "active"
        );

        synagogue.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        synagogue.put(
                "updatedAt",
                FieldValue.serverTimestamp()
        );

        db.collection("synagogues_v2")
                .document(uid)
                .set(synagogue)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            requireContext(),
                            "בית הכנסת נשמר בהצלחה!",
                            Toast.LENGTH_LONG
                    ).show();

                    finishRegistration();
                })
                .addOnFailureListener(e -> {

                    buttonSave.setEnabled(true);
                    buttonBack.setEnabled(true);

                    showError(
                            "שמירת בית הכנסת נכשלה:\n"
                                    + e.getMessage()
                    );
                });
    }

    private Map<String, Object> buildPrayerData(
            Bundle data) {

        Map<String, Object> result =
                new HashMap<>();

        Object prayerObject =
                data.getSerializable(
                        "prayerData"
                );

        if (!(prayerObject instanceof Map)) {
            return result;
        }

        Map<?, ?> original =
                (Map<?, ?>) prayerObject;

        for (Map.Entry<?, ?> dayEntry :
                original.entrySet()) {

            if (dayEntry.getKey() == null) {
                continue;
            }

            String dayKey =
                    String.valueOf(
                            dayEntry.getKey()
                    );

            Object prayerObjectForDay =
                    dayEntry.getValue();

            if (!(prayerObjectForDay instanceof Map)) {
                continue;
            }

            Map<?, ?> originalPrayers =
                    (Map<?, ?>) prayerObjectForDay;

            Map<String, Object> prayers =
                    new HashMap<>();

            for (Map.Entry<?, ?> prayerEntry :
                    originalPrayers.entrySet()) {

                if (prayerEntry.getKey() == null) {
                    continue;
                }

                prayers.put(
                        String.valueOf(
                                prayerEntry.getKey()
                        ),
                        prayerEntry.getValue()
                );
            }

            result.put(
                    dayKey,
                    prayers
            );
        }

        return result;
    }

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

        for (String key : keys) {

            result.put(
                    key,
                    selected != null &&
                            selected.contains(key)
            );
        }

        return result;
    }

    private void finishRegistration() {

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager
                                .POP_BACK_STACK_INCLUSIVE
                );
    }

    private void showError(String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }

}
