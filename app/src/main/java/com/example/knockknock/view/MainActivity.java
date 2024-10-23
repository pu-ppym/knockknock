package com.example.knockknock.view;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.knockknock.R;
import com.razzaghimahdi78.dotsloading.circle.LoadingCircleFady;
import com.razzaghimahdi78.dotsloading.linear.LoadingWavy;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {
    private TextView ShowDate;
    private TextView ShowTime;
    String weatherInfo;
    TextView showWeather;
    ImageView showWeatherImg;
    LoadingCircleFady loadingImg;

    String[] weatherInfoArray;  // 임시

    // gps
    LocationManager locationManager;
    LocationListener locationListener;
    double lat; // 위도
    double lng; // 경도
    String x = "";
    String y = "";

    // bluetooth
    private InputStream inputStream;
    private BluetoothSocket bluetoothSocket;
    private TextView btTest;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean gpsLocationReceived = false;  //getGpsData()


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ShowDate = (TextView) findViewById(R.id.textView4);
        ShowTime = (TextView) findViewById(R.id.textView5);
        showWeather = findViewById(R.id.textViewWd);
        showWeatherImg = findViewById(R.id.imgWeather);
        btTest = findViewById(R.id.btTest);

        loadingImg = findViewById(R.id.loadingImg);
        loadingImg.setSize(30);
        loadingImg.setDuration(400);


        ShowTimeMethod();
        getGpsData();

        initializeBluetooth();

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
            System.out.println("날씨 테스트: "+ weatherInfo);   // 하늘, 기온, 강수 확률, 눈 or 비, 습도

            Runnable updateUiRunnable = () -> {
                loadingImg.setVisibility(View.GONE);
                int hour = Integer.parseInt(simpleDateFormat2.format(new Date())); // 낮 밤 구분

                if (weatherInfo != null) {
                    weatherInfoArray = weatherInfo.split(" ");   // 0하늘, 1기온, 2강수 확률, 3눈 or 비, 4습도
                    showWeather.setText(weatherInfoArray[1]);
                    // 날씨 이미지 세팅 함수
                    setWeatherImg(weatherInfoArray, hour);
                } else {
                    showWeather.setText("날씨 정보를 가져오지 못했습니다.");
                }
                showWeather.setVisibility(View.VISIBLE);
                showWeatherImg.setVisibility(View.VISIBLE);

            };

            // 백그라운드 작업 후 UI 스레드로 전환하여 업데이트
            handler.post(updateUiRunnable);
        }).start();


    }

    public void setWeatherImg(String[] array, int hour) {
        // 날씨 이미지 세팅
        System.out.println("매개변수 테스트: "+ Arrays.toString(array));
        if (array[0].equals("맑음")) {
            if(hour >= 6 && hour < 18) {   // 낮
                showWeatherImg.setImageResource(R.drawable.sun);
            } else {
                showWeatherImg.setImageResource(R.drawable.moon);
            }
        } else if (array[0].equals("비")) {
            showWeatherImg.setImageResource(R.drawable.rainy_day);
        } else if (array[0].equals("구름많음")) {
            showWeatherImg.setImageResource(R.drawable.sun_cloud);
        } else if (array[0].equals("흐림")) {
            showWeatherImg.setImageResource(R.drawable.cloud);
        } else if (array[3].equals("눈")) {
            showWeatherImg.setImageResource(R.drawable.snowfall);
        }
    }


    public void getGpsData(){
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
                Log.i("MyAddress","주소: " + address);

                // 엑셀파일에서 기상청만의 특별한^^ x,y 좌표(격자 XY) 가져옴
                readExcel(String.valueOf((int) lat),  String.valueOf((int) lng));

                showWeatherData();

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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
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
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        //showWeatherData();
    }


    // 블루투스

    private void initializeBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    100); // 100은 요청 코드입니다.
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();  // 페어링된 장치 목록
        for (BluetoothDevice device : pairedDevices) {
            Log.d("bluetooth", device.getName().toString());
            Log.d("bluetooth", "MAC ADDRESS : " + device.getAddress().toString());
        }

        BluetoothDevice d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("C8:F0:9E:B1:AA:5A");
        connectToDevice(d);
    }

    private void connectToDevice(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청 로직 추가
                return;
            }

            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();  // 소켓연결
            inputStream = bluetoothSocket.getInputStream(); // 입력 스트림 초기화

            // 연결 성공 후 데이터 읽기 등의 작업 수행
            Log.d("Bluetooth", "연결 성공: " + device.getName());

            readData(); // 데이터 수신 시작

        } catch (IOException e) {
            Log.e("Bluetooth", "Error connecting to device", e);
            closeSocket(); // 소켓 닫기 처리
        }
    }

    private void readData() {
        byte[] buffer = new byte[1024];

        new Thread(() -> {
            while (true) {
                try {
                    // 입력 스트림에서 데이터 읽기
                    int bytes = inputStream.read(buffer); // 데이터 읽기

                    if (bytes > 0) {
                        String receivedData = new String(buffer, 0, bytes).trim();
                        Log.d("Bluetooth", "Received Data: " + receivedData); // 수신 데이터 로그 출력
                        // 수신한 데이터 처리
                        handleReceivedData(receivedData);
                    }
                } catch (IOException e) {
                    Log.e("Bluetooth", "Error reading data", e);
                    break; // 오류 발생 시 루프 종료
                }
            }
        }).start();
    }

    private void handleReceivedData(String data) {
        // 수신한 데이터에 따라 처리
        runOnUiThread(() -> {
            if (data.equals("1")) {
                btTest.setText("신호: ON");
            } else if (data.equals("0")) {
                btTest.setText("신호: OFF");
            }
        });
    }

    private void closeSocket() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close(); // 소켓 닫기
            }
        } catch (IOException e) {
            Log.e("Bluetooth", "Error closing socket", e);
        }
    }



}

