package com.example.knockknock.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.knockknock.R;
import com.example.knockknock.controller.ApiService;
import com.example.knockknock.controller.RetrofitClient;
import com.example.knockknock.model.MemberModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditInfoFragment newInstance(String param1, String param2) {
        EditInfoFragment fragment = new EditInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private EditText etName;
    private EditText etCall;
    String userName;
    String userCall;
    int pkid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);

        etName = view.findViewById(R.id.editRegisterName);
        etCall = view.findViewById(R.id.editRegisterCall);

        // SharedPreferences에서 저장된 정보 가져오기
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", ""); // 기본값은 빈 문자열
        userCall = sharedPreferences.getString("userCall", "");
        pkid = sharedPreferences.getInt("userPkid", -1); // -1은 기본값

        Button buttonU = view.findViewById(R.id.buttonUpdate);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMember();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        etName.setText(userName);
        etCall.setText(userCall);

    }


    private void updateMember() {
        String name = etName.getText().toString();
        String eCall = etCall.getText().toString();
        if (name.isEmpty() || eCall.isEmpty()) {
            Toast.makeText(getActivity(), "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        MemberModel member = new MemberModel(pkid, name, eCall);

        Retrofit retrofit = RetrofitClient.getClient();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<Void> call = apiService.updateMember(member);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "회원 정보가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                    saveUserInfo(member);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "정보 수정 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EditInfo 프래그먼트", "에러 발생: " + t.getMessage());
                Toast.makeText(getActivity(), "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(MemberModel member) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userName", member.getName());
        editor.putString("userCall", member.getEmergency_contact());
        editor.apply();
    }

}