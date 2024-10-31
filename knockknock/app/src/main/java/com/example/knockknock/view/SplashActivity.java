package com.example.knockknock.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knockknock.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 로그인 정보가 있으면 MainActivity, 없으면 LoginActivity로 이동
                if (userIsLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 1500); // 1500ms = 1.5초
    }

    private boolean userIsLoggedIn() {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userId = sharedPref.getString("userId", null);

        int userPkid = sharedPref.getInt("userPkid", -1);
        String userName = sharedPref.getString("userName", "N/A");
        //Toast.makeText(this, "로그인 되었습니다: " + userPkid + ", " + userId + ", " + userName, Toast.LENGTH_LONG).show();
        Toast.makeText(this, "로그인 되었습니다: " +  userName, Toast.LENGTH_LONG).show();


        return userId != null;
    }

}