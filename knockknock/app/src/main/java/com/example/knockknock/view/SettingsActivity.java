package com.example.knockknock.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.knockknock.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private AlertSettingsFragment aFragment;
    private EditInfoFragment eFragment;
    private LogoutFragment lFragment;

    BottomNavigationView bNavigation;

    public static final int ALERT_SETTINGS_FRAGMENT = 0;
    public static final int EDIT_INFO_FRAGMENT = 1;
    public static final int LOGOUT_FRAGMENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        aFragment = new AlertSettingsFragment();
        eFragment = new EditInfoFragment();
        lFragment = new LogoutFragment();

        bNavigation = findViewById(R.id.btmNavigation);
        bNavigation.setOnNavigationItemSelectedListener(new BtmNavItemSelectedLister());

        changeFragment(ALERT_SETTINGS_FRAGMENT);

    }

    private class BtmNavItemSelectedLister implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int selectedItemId = item.getItemId();

            if(selectedItemId == R.id.navAlertSettings) {
                changeFragment(ALERT_SETTINGS_FRAGMENT);
            } else if(selectedItemId == R.id.navEditInfo) {
                changeFragment(EDIT_INFO_FRAGMENT);
            } else if(selectedItemId == R.id.navLogout) {
                changeFragment(LOGOUT_FRAGMENT);
            }

            return true;
        }
    }

    public void changeFragment(int fragmentNum) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch(fragmentNum) {
            case ALERT_SETTINGS_FRAGMENT:
                ft = ft.replace(R.id.frmMain, aFragment);
                ft.commitNow();
                break;
            case EDIT_INFO_FRAGMENT:
                ft = ft.replace(R.id.frmMain, eFragment);
                ft.commitNow();
                break;
            case LOGOUT_FRAGMENT:
                ft = ft.replace(R.id.frmMain, lFragment);
                ft.commitNow();
                break;
        }

    }
}