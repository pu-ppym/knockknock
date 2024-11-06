//BusStnFragment 여기까지는 됨

package com.example.test;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusStnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusStnFragment extends Fragment {
    // 리사이클러
    private RecyclerView recyclerView;
    private RecyclerStnAdapter adapter = new RecyclerStnAdapter();
    private ArrayList<StnItem> list = new ArrayList<>();


    // 위도 경도
    private String lat;
    private String lng;

    // 도시코드, 정류장id
    private String citycode = "";
    private String nodeid = "";

    //
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BusStnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusStnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusStnFragment newInstance(String param1, String param2) {
        BusStnFragment fragment = new BusStnFragment();
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
        View view = inflater.inflate(R.layout.fragment_bus_stn, container, false);

        // main에서부터 gps받아오기
        Bundle args = getArguments();
        if (args != null) {
            lat = args.getString("key_x");
            lng = args.getString("key_y");
            Log.i("getLocation", "x: "+lat + ", y: "+ lng);
        }

        stnData();

        // 제목 클릭 시 정보 업데이트
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView stnList = view.findViewById(R.id.stnText);
        stnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = new ArrayList<>();
                stnData();

            }
        });

        recyclerView = view.findViewById(R.id.busStnList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerStnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                StnItem item = list.get(pos);
                BusArrivalFragment busArrivalFragment = new BusArrivalFragment();
                Bundle bundle = new Bundle();
                citycode = item.getCitycode();
                nodeid = item.getNodeid();
                bundle.putString("key_citycode", citycode);  // 클릭한 아이템의 citycode
                bundle.putString("key_nodeid", nodeid);      // 클릭한 아이템의 nodeid
                busArrivalFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.busFrgContainerView, busArrivalFragment);
                transaction.addToBackStack(null); // 뒤로가기 스택에 추가
                transaction.commit();
            }
        });

        return view;
    }

    // 파싱해와서 리사이클러뷰에 올리기
    private void stnData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String bsSttnApiUrl = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList";
                    String serviceKey = "szed3dpHQow1gk6%2BHE%2F%2BuBo82pVn8Hv0uZ6hfaU7aD2bcLPxB5MLa8yDAeQxt%2BzxpnIplahGhvA5%2BeM7xqmoQw%3D%3D";

                    StringBuilder urlBuilder = new StringBuilder(bsSttnApiUrl); /*URL*/
                    urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
                    urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
                    urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*한 페이지 결과 수*/
                    urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터 타입(xml, json)*/
                    urlBuilder.append("&" + URLEncoder.encode("gpsLati","UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8")); /*WGS84 위도 좌표*/
                    urlBuilder.append("&" + URLEncoder.encode("gpsLong","UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8")); /*WGS84 경도 좌표*/

                    URL url = new URL(urlBuilder.toString());
                    System.out.println("url : " + url);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    Log.i("여긴가?","04");

                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        Log.i("여긴가?","01");
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        Log.i("여긴가?","02");
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }

                    //
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    //

                    rd.close();
                    conn.disconnect();
                    String result = sb.toString();
                    Log.i("API Response", result);
                    if (result == null || result.trim().isEmpty()) { // 응답이 비어 있으면 파싱 중단
                        Log.e("API Error", "Empty response received from server");
                        return;
                    }

                    JSONObject jsonObj_01 = new JSONObject(result);
                    String response = jsonObj_01.getString("response");

                    JSONObject jsonObj_02 = new JSONObject(response);
                    String body = jsonObj_02.getString("body");

                    JSONObject jsonObj_03 = new JSONObject(body);
                    String items = jsonObj_03.getString("items");

                    JSONObject jsonObj_04 = new JSONObject(items);
                    JSONArray jsonArray = jsonObj_04.getJSONArray("item");

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject temp = jsonArray.getJSONObject(i);
                                    list.add(new StnItem(temp.getString("citycode"),temp.getString("nodeid"),temp.getString("nodenm"),temp.getString("nodeno")));
                                }

                                // adapter에 적용
                                adapter.setstnList(list);
                                adapter.notifyDataSetChanged();
                                Log.i("리사이클러뷰 데이터 수", String.valueOf(list.size()));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}