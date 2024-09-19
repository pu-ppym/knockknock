package com.example.knockknock.view;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MyGps {
    private Context mContext;

    public MyGps(Context context) {
        this.mContext = context;

        // 위치 관리자 객체 참조
        final LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        else{
            // 가장최근 위치정보 가져오기
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null) {
                String provider = location.getProvider();  // 위치정보
                double longitude = location.getLongitude();   //위도
                double latitude = location.getLatitude();  //경도
                double altitude = location.getAltitude();  //고도

            }

            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000,
                    1,
                    gpsLocationListener);
        }
    }





}
