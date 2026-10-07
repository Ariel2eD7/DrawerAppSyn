package com.example.drawerappsyn;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import com.example.synagogue.UsersFragment;
import com.example.synagogue.LoginFragment;
import com.example.register.AddSynagogue01WelcomeFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.icu.util.HebrewCalendar;
import com.example.profile.ProfileFragment;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private FirebaseAuth auth;

    // Listener שמאזין לשינוי במצב ההתחברות
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ==========================================
        // Firebase
        // ==========================================

        auth = FirebaseAuth.getInstance();


        // ==========================================
        // חיבור בין Java לבין ה-XML
        // ==========================================

        drawerLayout =
                findViewById(R.id.drawer_layout);

        navigationView =
                findViewById(R.id.navigation_view);

        toolbar =
                findViewById(R.id.toolbar);


        // ==========================================
        // מסך ראשי
        // ==========================================

        if (savedInstanceState == null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            new UsersFragment()
                    )
                    .commit();
        }


        // ==========================================
        // הגדרת Toolbar
        // ==========================================

        setSupportActionBar(toolbar);


        // ==========================================
        // יצירת כפתור התפריט
        // ==========================================

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.open_drawer,
                        R.string.close_drawer
                );

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        // ==========================================
        // תאריך עברי
        // ==========================================

        updateHebrewDate();


        // ==========================================
        // כפתורי Header
        // ==========================================

        setupDrawerHeaderButtons();


        // ==========================================
        // Listener של Firebase
        //
        // בכל פעם שמשתמש מתחבר / מתנתק
        // ה-Header מתעדכן אוטומטית
        // ==========================================

        authStateListener =
                firebaseAuth -> {

                    updateDrawerHeader();
                };


        // ==========================================
        // לחיצה על פריטים בתפריט
        // ==========================================

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();


            // ======================================
            // בית
            // ======================================

            if (id == R.id.nav_home) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new UsersFragment()
                        )
                        .commit();
            }


            // ======================================
            // פרופיל
            // ======================================

            else if (id == R.id.nav_profile) {

                // כאן יהיה בהמשך מסך הפרופיל
            }


            // ======================================
            // הגדרות
            // ======================================

            else if (id == R.id.nav_settings) {

                // כאן יהיו ההגדרות
            }


            // סגירת Drawer

            drawerLayout.closeDrawers();

            return true;
        });
    }


    // ==========================================
    // התחלת האזנה ל-Firebase
    // ==========================================

    @Override
    protected void onStart() {

        super.onStart();

        if (auth != null &&
                authStateListener != null) {

            auth.addAuthStateListener(
                    authStateListener
            );
        }
    }


    // ==========================================
    // הפסקת האזנה ל-Firebase
    // ==========================================

    @Override
    protected void onStop() {

        super.onStop();

        if (auth != null &&
                authStateListener != null) {

            auth.removeAuthStateListener(
                    authStateListener
            );
        }
    }


    // ==========================================
    // הצגה / הסתרה של Toolbar
    // ==========================================

    public void setToolbarVisible(boolean visible) {

        if (toolbar != null) {

            toolbar.setVisibility(
                    visible
                            ? View.VISIBLE
                            : View.GONE
            );
        }
    }


    // ==========================================
    // עדכון Drawer Header
    // לפי מצב Firebase
    // ==========================================

    private void updateDrawerHeader() {

        if (navigationView == null) {
            return;
        }

        if (navigationView.getHeaderCount() == 0) {
            return;
        }


        View headerView =
                navigationView.getHeaderView(0);


        // ==========================================
        // אזור אורח
        // ==========================================

        View guestSection =
                headerView.findViewById(
                        R.id.drawerGuestSection
                );


        // ==========================================
        // אזור משתמש מחובר
        // ==========================================

        View loggedInSection =
                headerView.findViewById(
                        R.id.drawerLoggedInSection
                );


        // ==========================================
        // טקסט שלום
        // ==========================================

        TextView welcomeText =
                headerView.findViewById(
                        R.id.drawerWelcomeText
                );


        // ==========================================
        // אימייל
        // ==========================================

        TextView emailText =
                headerView.findViewById(
                        R.id.drawerLoggedInEmail
                );


        // ==========================================
        // בדיקת משתמש Firebase
        // ==========================================

        FirebaseUser user =
                auth.getCurrentUser();


        // ==========================================
        // משתמש מחובר
        // ==========================================

        if (user != null) {

            // --------------------------------------
            // הסתרת אזור אורח
            // --------------------------------------

            if (guestSection != null) {

                guestSection.setVisibility(
                        View.GONE
                );
            }


            // --------------------------------------
            // הצגת אזור משתמש מחובר
            // --------------------------------------

            if (loggedInSection != null) {

                loggedInSection.setVisibility(
                        View.VISIBLE
                );
            }


            // --------------------------------------
            // שם המשתמש
            // --------------------------------------

            if (welcomeText != null) {

                String displayName =
                        user.getDisplayName();

                if (displayName != null &&
                        !displayName.trim().isEmpty()) {

                    welcomeText.setText(
                            "שלום, " + displayName
                    );

                } else {

                    welcomeText.setText(
                            "שלום!"
                    );
                }
            }


            // --------------------------------------
            // אימייל
            // --------------------------------------

            if (emailText != null) {

                String email =
                        user.getEmail();

                if (email != null &&
                        !email.trim().isEmpty()) {

                    emailText.setText(
                            email
                    );

                } else {

                    emailText.setText(
                            "מחובר"
                    );
                }
            }
        }


        // ==========================================
        // משתמש לא מחובר
        // ==========================================

        else {

            // --------------------------------------
            // הצגת אזור אורח
            // --------------------------------------

            if (guestSection != null) {

                guestSection.setVisibility(
                        View.VISIBLE
                );
            }


            // --------------------------------------
            // הסתרת אזור משתמש מחובר
            // --------------------------------------

            if (loggedInSection != null) {

                loggedInSection.setVisibility(
                        View.GONE
                );
            }


            // --------------------------------------
            // איפוס הטקסטים
            // --------------------------------------

            if (welcomeText != null) {

                welcomeText.setText(
                        "שלום!"
                );
            }

            if (emailText != null) {

                emailText.setText(
                        "מחובר"
                );
            }
        }
    }


    // ==========================================
    // כפתורי Header
    // ==========================================

    private void setupDrawerHeaderButtons() {

        if (navigationView == null) {
            return;
        }

        if (navigationView.getHeaderCount() == 0) {
            return;
        }


        View headerView =
                navigationView.getHeaderView(0);


        // ==========================================
        // כפתור הרשמה
        // ==========================================

        View registerButton =
                headerView.findViewById(
                        R.id.drawerRegisterButton
                );

        if (registerButton != null) {

            registerButton.setOnClickListener(v -> {

                drawerLayout.closeDrawers();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new AddSynagogue01WelcomeFragment()
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }


        // ==========================================
        // כפתור כניסה
        // ==========================================

        View loginButton =
                headerView.findViewById(
                        R.id.drawerLoginButton
                );

        if (loginButton != null) {

            loginButton.setOnClickListener(v -> {

                drawerLayout.closeDrawers();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new LoginFragment()
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }


        // ==========================================
        // כפתור פרופיל
        // ==========================================



        View profileButton =
                headerView.findViewById(
                        R.id.drawerProfileButton
                );

        if (profileButton != null) {

            profileButton.setOnClickListener(v -> {

                // סגירת ה-Drawer
                drawerLayout.closeDrawers();

                // מעבר למסך הפרופיל
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new com.example.profile.ProfileFragment()
                        )
                        .addToBackStack(null)
                        .commit();
            });
        }



        // ==========================================
        // כפתור התנתקות
        // ==========================================

        View logoutButton =
                headerView.findViewById(
                        R.id.drawerLogoutButton
                );

        if (logoutButton != null) {

            logoutButton.setOnClickListener(v -> {

                // ==================================
                // התנתקות
                // ==================================

                auth.signOut();

                // ==================================
                // אין צורך לקרוא כאן
                // ל-updateDrawerHeader()
                //
                // ה-AuthStateListener יעשה זאת
                // אוטומטית
                // ==================================

                drawerLayout.closeDrawers();


                // ==================================
                // חזרה למסך הראשי
                // ==================================

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new UsersFragment()
                        )
                        .commit();
            });
        }
    }


    // ==========================================
    // עדכון התאריך העברי
    // ==========================================

    private void updateHebrewDate() {

        if (navigationView == null) {
            return;
        }

        if (navigationView.getHeaderCount() == 0) {
            return;
        }


        View headerView =
                navigationView.getHeaderView(0);


        TextView hebrewDateText =
                headerView.findViewById(
                        R.id.hebrewDateText
                );


        if (hebrewDateText == null) {
            return;
        }


        HebrewCalendar hebrewCalendar =
                new HebrewCalendar();


        int day =
                hebrewCalendar.get(
                        Calendar.DAY_OF_MONTH
                );


        int month =
                hebrewCalendar.get(
                        Calendar.MONTH
                );


        int year =
                hebrewCalendar.get(
                        Calendar.YEAR
                );


        Calendar gregorianCalendar =
                Calendar.getInstance();


        int dayOfWeek =
                gregorianCalendar.get(
                        Calendar.DAY_OF_WEEK
                );


        String monthName =
                getHebrewMonthName(month);


        String dayName =
                getHebrewDayName(dayOfWeek);


        String hebrewDay =
                numberToHebrew(day);


        String hebrewYear =
                hebrewYearToHebrewLetters(year);


        String fullDate =
                dayName
                        + ", "
                        + hebrewDay
                        + " ב"
                        + monthName
                        + " "
                        + hebrewYear;


        hebrewDateText.setText(
                fullDate
        );
    }


    // ==========================================
    // שמות חודשי השנה העברית
    // ==========================================

    private String getHebrewMonthName(int month) {

        switch (month) {

            case HebrewCalendar.TISHRI:
                return "תשרי";

            case HebrewCalendar.HESHVAN:
                return "חשוון";

            case HebrewCalendar.KISLEV:
                return "כסלו";

            case HebrewCalendar.TEVET:
                return "טבת";

            case HebrewCalendar.SHEVAT:
                return "שבט";

            case HebrewCalendar.ADAR_1:
                return "אדר א׳";

            case HebrewCalendar.ADAR:
                return "אדר";

            case HebrewCalendar.NISAN:
                return "ניסן";

            case HebrewCalendar.IYAR:
                return "אייר";

            case HebrewCalendar.SIVAN:
                return "סיוון";

            case HebrewCalendar.TAMUZ:
                return "תמוז";

            case HebrewCalendar.AV:
                return "אב";

            case HebrewCalendar.ELUL:
                return "אלול";

            default:
                return "";
        }
    }


    // ==========================================
    // שמות ימי השבוע
    // ==========================================

    private String getHebrewDayName(int dayOfWeek) {

        switch (dayOfWeek) {

            case Calendar.SUNDAY:
                return "יום ראשון";

            case Calendar.MONDAY:
                return "יום שני";

            case Calendar.TUESDAY:
                return "יום שלישי";

            case Calendar.WEDNESDAY:
                return "יום רביעי";

            case Calendar.THURSDAY:
                return "יום חמישי";

            case Calendar.FRIDAY:
                return "יום שישי";

            case Calendar.SATURDAY:
                return "שבת";

            default:
                return "";
        }
    }


    // ==========================================
    // המרת מספר לאותיות עבריות
    // ==========================================

    private String numberToHebrew(int number) {

        if (number <= 0) {
            return "";
        }


        StringBuilder result =
                new StringBuilder();


        // מאות

        while (number >= 400) {

            result.append("ת");

            number -= 400;
        }


        if (number >= 300) {

            result.append("ש");

            number -= 300;
        }


        if (number >= 200) {

            result.append("ר");

            number -= 200;
        }


        if (number >= 100) {

            result.append("ק");

            number -= 100;
        }


        // עשרות

        String[] tens = {
                "",
                "י",
                "כ",
                "ל",
                "מ",
                "נ",
                "ס",
                "ע",
                "פ",
                "צ"
        };


        if (number >= 10) {

            result.append(
                    tens[number / 10]
            );

            number %= 10;
        }


        // יחידות

        String[] ones = {
                "",
                "א",
                "ב",
                "ג",
                "ד",
                "ה",
                "ו",
                "ז",
                "ח",
                "ט"
        };


        if (number > 0) {

            result.append(
                    ones[number]
            );
        }


        String hebrew =
                result.toString();


        // גרש / גרשיים

        if (hebrew.length() == 1) {

            return hebrew + "׳";

        } else {

            return hebrew.substring(
                    0,
                    hebrew.length() - 1
            )
                    + "״"
                    + hebrew.substring(
                    hebrew.length() - 1
            );
        }
    }


    // ==========================================
    // המרת השנה העברית
    // ==========================================

    private String hebrewYearToHebrewLetters(
            int year) {

        int thousands =
                year / 1000;

        int remainder =
                year % 1000;


        StringBuilder result =
                new StringBuilder();


        // האלפים

        if (thousands > 0) {

            String thousandsText =
                    numberToHebrew(thousands);


            if (thousandsText.endsWith("׳")) {

                thousandsText =
                        thousandsText.substring(
                                0,
                                thousandsText.length() - 1
                        );
            }


            result.append(
                    thousandsText
            );


            result.append("׳");
        }


        // שאר השנה

        if (remainder > 0) {

            result.append(
                    numberToHebrew(
                            remainder
                    )
            );
        }


        return result.toString();
    }
}
