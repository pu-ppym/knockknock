package com.example.knockknock.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knockknock.R;
import com.example.knockknock.controller.ApiService;
import com.example.knockknock.model.MemberModel;
import com.example.knockknock.controller.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextName, editTextId, editTextPw, editTextCall;
    private Button buttonRegister, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextId = findViewById(R.id.editRegisterId);
        editTextPw = findViewById(R.id.editRegisterPw);
        editTextName = findViewById(R.id.editRegisterName);
        editTextCall = findViewById(R.id.editRegisterCall);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editTextId.getText().toString();
                String password = editTextPw.getText().toString();
                String name = editTextName.getText().toString();
                String eCall = editTextCall.getText().toString();

                if (id.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eCall.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "전화번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                MemberModel member = new MemberModel(id, password, name, eCall);
                registerMember(member);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerMember(MemberModel member) {
        Retrofit retrofit = RetrofitClient.getClient();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Void> call = apiService.registerMember(member);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();

                    // 로그인 액티비티로 이동
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                } else {
                    Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RegisterActivity", "에러 발생: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

}