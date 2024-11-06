package com.example.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusBtnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusBtnFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String lat = "";
    private String lng = "";

    public BusBtnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusBtnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusBtnFragment newInstance(String param1, String param2) {
        BusBtnFragment fragment = new BusBtnFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus_btn, container, false);

        Button button = view.findViewById(R.id.busBtn);

        Bundle args = getArguments();
        if (args != null) {
            lat = args.getString("key_x");
            lng = args.getString("key_y");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusStnFragment busStnFrg = new BusStnFragment();
                Bundle bundle = new Bundle();
                bundle.putString("key_x", lat);
                bundle.putString("key_y", lng);
                busStnFrg.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.busFrgContainerView, busStnFrg);
                transaction.addToBackStack(null); // 뒤로가기 스택에 추가
                transaction.commit();
            }
        });

        return view;
    }
}