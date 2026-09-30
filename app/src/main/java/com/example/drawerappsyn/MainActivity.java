package com.example.drawerappsyn;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.example.synagogue.UsersFragment;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // חיבור בין Java לבין ה-XML
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = findViewById(R.id.toolbar);

        // הגדרת ה-Toolbar
        setSupportActionBar(toolbar);

        // יצירת כפתור התפריט ☰
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // לחיצה על פריטים בתפריט
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new UsersFragment())
                        .commit();


                // כאן יהיה מסך הבית

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
}