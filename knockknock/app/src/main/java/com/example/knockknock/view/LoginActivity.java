package com.example.knockknock.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knockknock.R;
import com.example.knockknock.model.ApiService;
import com.example.knockknock.model.MemberModel;
import com.example.knockknock.model.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginId, editTextLoginPw;
    private Button buttonLogin, buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextLoginId = findViewById(R.id.editLoginId);
        editTextLoginPw = findViewById(R.id.editLoginPw);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextLoginId.getText().toString();
                String password = editTextLoginPw.getText().toString();

                MemberModel member = new MemberModel(id, password);
                login(member);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private void login(MemberModel member) {
        Retrofit retrofit = RetrofitClient.getClient();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<MemberModel> call = apiService.login(member);

        call.enqueue(new Callback<MemberModel>() {
            @Override
            public void onResponse(Call<MemberModel> call, Response<MemberModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 로그인 성공 시, 사용자 정보 저장
                    MemberModel member = response.body();
                    Log.d("뭐가 넘어오나 보자:", member.getUser_id());
                    Log.d("pkid 어케넘어옴:", String.valueOf(member.getPkid()));
                    saveUserInfo(member);

                    // 메인 액티비티로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MemberModel> call, Throwable t) {
                Log.e("LoginActivity", "에러 발생: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 파일에 사용자 정보 저장
    private void saveUserInfo(MemberModel member) {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("userPkid", member.getPkid());
        editor.putString("userId", member.getUser_id());
        editor.putString("userName", member.getName());
        editor.apply();

    }


}