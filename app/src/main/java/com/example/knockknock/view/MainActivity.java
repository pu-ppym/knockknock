package com.example.knockknock.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.knockknock.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private TextView ShowDate;
    private TextView ShowTime;
    String weatherInfo;
    TextView showWeater;

    // gps
    LocationManager locationManager;
    LocationListener locationListener;
    double lat; // 위도
    double lng; // 경도
    String x = "";
    String y = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ShowDate = (TextView) findViewById(R.id.textView4);
        ShowTime = (TextView) findViewById(R.id.textView5);
        showWeater = findViewById(R.id.textViewWd);


        ShowTimeMethod();
        getGpsData();


    }

    public void ShowTimeMethod(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
//                ShowTime.setText(DateFormat.getDateTimeInstance().format(new Date()));
                Calendar cal = Calendar.getInstance();

                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd(E)");
                String strDate = date.format(cal.getTime());
                SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                String strTime = time.format(cal.getTime());
                ShowDate.setText(strDate);
                ShowTime.setText(strTime);
            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    Handler handler = new Handler(Looper.getMainLooper());

    public void showWeatherData() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        // 넘겨줄 날짜 가져오기
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
        String getDate = simpleDateFormat1.format(mDate);

        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH");
        String getTime = simpleDateFormat2.format(mDate) + "00";

        WeatherData wd = new WeatherData();

        new Thread(() -> {
            try {
                weatherInfo = wd.lookUpWeather(getDate,getTime,x,y);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            System.out.println("날씨 테스트: "+ weatherInfo);

            Runnable updateUiRunnable = () -> {
                if (weatherInfo != null) {
                    String[] weatherInfoArray = weatherInfo.split(" ");
                    showWeater.setText(weatherInfoArray[1]);
                } else {
                    showWeater.setText("날씨 정보를 가져오지 못했습니다.");
                }
            };

            // 백그라운드 작업 후 UI 스레드로 전환하여 업데이트
            handler.post(updateUiRunnable);
        }).start();


    }



    public void getGpsData(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            // GPS의 정보를 얻어 올 수 있는 메소드
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                Log.i("MyLocation", "위도 : " + lat);
                Log.i("MyLocation", "경도 : " + lng);


                String address = getCurrentAddress(lat, lng);
                Log.i("MyAddress","주소: " + address);

                // 엑셀파일에서 기상청만의 특별한^^ x,y 좌표(격자 XY) 가져옴
                readExcel(String.valueOf((int) lat),  String.valueOf((int) lng));

            }
        };

        // requestLocationUpdates(String provider, long minTimeMs, float minDistanceM, LocationListener listener, Looper looper)
        // 10분마다(minTime 60000) 위치 정보 업데이트
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            //return;
        } else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 10, locationListener);
        }
    }

    // gps - 앱 권한 요청 설정 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // 권한 요청
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }

// gps로 주소 가져오기
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


    public void readExcel(String lat, String lng) {
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("xy_data_update.xlt");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if (sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 2;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal - 1).length;

                    for (int row = rowIndexStart; row < rowTotal; row++) {
                        String lngCellContents = sheet.getCell(4, row).getContents();  // 엑셀 4열=경도
                        String latCellContents = sheet.getCell(5, row).getContents();

                        if (latCellContents.contains(lat) && lngCellContents.contains(lng)) {
                            x = sheet.getCell(2, row).getContents();  // 엑셀 2열 격자 X
                            y = sheet.getCell(3, row).getContents();
                            row = rowTotal;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.i("READ_EXCEL1", e.getMessage());
            e.printStackTrace();
        } catch (BiffException e) {
            Log.i("READ_EXCEL1", e.getMessage());
            e.printStackTrace();
        }
        Log.i("격자값", "x = " + x + "  y = " + y);

        showWeatherData();
    }



}

