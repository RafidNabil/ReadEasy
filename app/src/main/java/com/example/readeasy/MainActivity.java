package com.example.readeasy;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    SearchFragment searchFragment = new SearchFragment();
    LibraryFragment libraryFragment = new LibraryFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(MainActivity.this, UploadActivity.class));

        bottomNavigationView = findViewById(R.id.navbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.homebtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, homeFragment).commit();
                        return true;
                    case R.id.discoverbtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, searchFragment).commit();
                        return true;
                    case R.id.librarybtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, libraryFragment).commit();
                        return true;
                    case R.id.profilebtn:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, profileFragment).commit();
                        return true;
                }

                return false;
            }
        });

    }

}