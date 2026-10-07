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

import android.icu.util.HebrewCalendar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
        // חיבור בין Java לבין ה-XML
        // ==========================================

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        // ==========================================
        // הגדרת ה-Toolbar
        // ==========================================

        setSupportActionBar(toolbar);

        // ==========================================
        // יצירת כפתור התפריט ☰
        // ==========================================

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ==========================================
        // הצגת התאריך העברי ב-Drawer Header
        // ==========================================

        updateHebrewDate();

        // ==========================================
        // חיבור כפתורי הרשמה / כניסה ב-Drawer
        // ==========================================

        setupDrawerHeaderButtons();

        // ==========================================
        // לחיצה על פריטים בתפריט
        // ==========================================

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragment_container,
                                new UsersFragment()
                        )
                        .commit();

            } else if (id == R.id.nav_profile) {

                // כאן יהיה מסך הפרופיל

            } else if (id == R.id.nav_settings) {

                // כאן יהיו ההגדרות
            }

            // סגירת התפריט
            drawerLayout.closeDrawers();

            return true;
        });
    }

    public void setToolbarVisible(boolean visible) {

        if (toolbar != null) {
            toolbar.setVisibility(
                    visible ? View.VISIBLE : View.GONE
            );
        }
    }


    // ==========================================
    // כפתורי הרשמה / כניסה ב-Drawer Header
    // ==========================================

    private void setupDrawerHeaderButtons() {

        if (navigationView.getHeaderCount() == 0) {
            return;
        }

        // קבלת ה-Header
        View headerView =
                navigationView.getHeaderView(0);

        // כפתור הרשמה
        View registerButton =
                headerView.findViewById(
                        R.id.drawerRegisterButton
                );

        // כפתור כניסה
        View loginButton =
                headerView.findViewById(
                        R.id.drawerLoginButton
                );

        // ==========================================
        // הרשמת בית כנסת
        // ==========================================

        if (registerButton != null) {

            registerButton.setOnClickListener(v -> {

                // קודם סוגרים את ה-Drawer
                drawerLayout.closeDrawers();

                // מעבר למסך תחילת ההרשמה
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
        // כניסה לחשבון
        // ==========================================

        if (loginButton != null) {

            loginButton.setOnClickListener(v -> {

                // קודם סוגרים את ה-Drawer
                drawerLayout.closeDrawers();

                // מעבר למסך ההתחברות
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
    }

    // ==========================================
    // עדכון התאריך העברי
    // ==========================================

    private void updateHebrewDate() {

        if (navigationView.getHeaderCount() == 0) {
            return;
        }

        // קבלת ה-Header של ה-Drawer
        View headerView =
                navigationView.getHeaderView(0);

        // מציאת TextView של התאריך
        TextView hebrewDateText =
                headerView.findViewById(
                        R.id.hebrewDateText
                );

        if (hebrewDateText == null) {
            return;
        }

        // יצירת לוח עברי לפי התאריך הנוכחי
        HebrewCalendar hebrewCalendar =
                new HebrewCalendar();

        // היום בחודש העברי
        int day =
                hebrewCalendar.get(
                        Calendar.DAY_OF_MONTH
                );

        // החודש העברי
        int month =
                hebrewCalendar.get(
                        Calendar.MONTH
                );

        // השנה העברית
        int year =
                hebrewCalendar.get(
                        Calendar.YEAR
                );

        // יום בשבוע
        Calendar gregorianCalendar =
                Calendar.getInstance();

        int dayOfWeek =
                gregorianCalendar.get(
                        Calendar.DAY_OF_WEEK
                );

        // שמות החודשים
        String monthName =
                getHebrewMonthName(month);

        // שמות ימי השבוע
        String dayName =
                getHebrewDayName(dayOfWeek);

        // המרת היום העברי לאותיות
        String hebrewDay =
                numberToHebrew(day);

        // המרת השנה העברית לאותיות
        String hebrewYear =
                hebrewYearToHebrewLetters(year);

        // בניית הטקסט הסופי
        String fullDate =
                dayName
                        + ", "
                        + hebrewDay
                        + " ב"
                        + monthName
                        + " "
                        + hebrewYear;

        hebrewDateText.setText(fullDate);
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
    //
    // לדוגמה:
    // 5787 -> ה׳תשפ״ז
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
                    numberToHebrew(
                            thousands
                    );

            // מסירים את הגרש
            // שהפונקציה numberToHebrew מוסיפה
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

            // גרש אחד בלבד אחרי ה-ה'
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
