package com.example.knockknock.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.knockknock.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlertSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertSettingsFragment newInstance(String param1, String param2) {
        AlertSettingsFragment fragment = new AlertSettingsFragment();
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

    private Switch switchTodayTask, switchReminder, switchMedicine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_settings, container, false);

        switchTodayTask = view.findViewById(R.id.switch_today_task);
        switchReminder = view.findViewById(R.id.switch_reminder);
        switchMedicine = view.findViewById(R.id.switch_medicine);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        switchTodayTask.setChecked(preferences.getBoolean("today_task_enabled", true));
        switchReminder.setChecked(preferences.getBoolean("reminder_enabled", true));
        switchMedicine.setChecked(preferences.getBoolean("medicine_enabled", true));

        switchTodayTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("today_task_enabled", isChecked);
            editor.apply();
        });

        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("reminder_enabled", isChecked);
            editor.apply();
        });

        switchMedicine.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("medicine_enabled", isChecked);
            editor.apply();
        });

        return view;
    }
}