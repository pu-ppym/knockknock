package com.example.test;

import static com.example.test.BusArrivalData.busStn;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    double lat = 0; // 위도
    double lng = 0; // 경도
    String x = "";
    String y = "";
    private boolean gpsLocationReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getGpsData();


        x ="37.54";
        y = "126.72";


        // 위도 경도값 보내기
        BusBtnFragment busBtnFragment = new BusBtnFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key_x", x);
        bundle.putString("key_y", y);
        busBtnFragment.setArguments(bundle);

        // 버튼 프래그먼트 표시
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.busFrgContainerView, busBtnFragment);
        transaction.addToBackStack(null); // 뒤로 가기 스택에 추가
        transaction.commit();
    }


    public void getGpsData() {
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {


            // GPS의 정보를 얻어 올 수 있는 메소드
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("LocationChanged", "New location: " + location.toString());
                gpsLocationReceived = true; // GPS 위치 수신됨
                lat = location.getLatitude();
                lng = location.getLongitude();

                Log.i("MyLocation", "위도 : " + lat);
                Log.i("MyLocation", "경도 : " + lng);


                String address = getCurrentAddress(lat, lng);
                Log.i("MyAddress", "주소: " + address);

                x = Double.toString(lat);
                y = Double.toString(lng);

            }

        };
    }
    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더-> GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }
}