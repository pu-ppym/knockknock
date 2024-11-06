package com.example.knockknock.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knockknock.R;
import com.example.knockknock.controller.ApiService;
import com.example.knockknock.model.AccessModel;
import com.example.knockknock.model.EmergencyContactResponse;
import com.example.knockknock.controller.RetrofitClient;
import com.example.knockknock.model.MedicineModel;
import com.example.knockknock.model.ScheduleModel;
import com.razzaghimahdi78.dotsloading.circle.LoadingCircleFady;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private TextView ShowDate;
    private TextView ShowTime;
    String weatherInfo;
    TextView showWeather;
    ImageView showWeatherImg;
    LoadingCircleFady loadingImg;
    String[] weatherInfoArray;


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
    BluetoothDevice d;

    // 준비물 등록
    ImageButton ckListBtn;
    private List<String> checkedItems = new ArrayList<>();  // 체크할거 저장할 리스트
    private StringBuilder finalSelection = new StringBuilder(); // 결과를 저장할 StringBuilder

    // 긴급전화
    ImageButton callBtn;
    Retrofit retrofit = RetrofitClient.getClient();
    private String emergencyContact;

    // 할일
    ImageButton schedulesBtn;
    private List<ScheduleModel> tasks;
    StringBuilder stringBuilderTasks;

    // 설정
    ImageButton settingsBtn;

    // 약
    ImageButton medicationBtn;
    String time_of_day = "";
    List<MedicineModel> medicines;
    private ApiService apiService;

    // 추천 아이템
    boolean showCoatIcon = false;
    boolean showUmbrellaIcon = false;


    private boolean gpsLocationReceived = false;  //getGpsData()
    //int pkid;
    SharedPreferences sharedPreferences;


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
        ckListBtn = findViewById(R.id.imageButton4);
        callBtn = findViewById(R.id.imageButtonCall);
        schedulesBtn = findViewById(R.id.imageButtonScheds);
        settingsBtn = findViewById(R.id.imageButtonSettings);
        medicationBtn = findViewById(R.id.imageButtonMedi);

        loadingImg = findViewById(R.id.loadingImg);
        loadingImg.setSize(30);
        loadingImg.setDuration(400);

        // 파일에 저장된 유저정보 불러오기
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        int pkid = sharedPreferences.getInt("userPkid", -1); // -1은 기본값
        apiService = retrofit.create(ApiService.class);

        ShowTimeMethod();
        getGpsData();
//        initializeBluetooth();
        fetchTasks(pkid);
        fetchMedicine(pkid);
        fetchAccessRecords(pkid);

        // 스샷용
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert("오늘의 할일", stringBuilderTasks.toString());
                showAlert(medicines, time_of_day);
                showWeatherAlert(showCoatIcon, showUmbrellaIcon);
                showAlert("이것 챙기셨나요?", finalSelection.toString());
            }
        });



        ckListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChecklistDialog();
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchEmergencyContact(pkid);
            }
        });

        schedulesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScheduleDialog(pkid);

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        medicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMediInputDialog(pkid);
            }
        });


        BusBtnFragment busBtnFragment = new BusBtnFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.busFrgContainerView, busBtnFragment);
        transaction.commit();


    }

    public void ShowTimeMethod(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
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

        wd.lookUpWeatherAsync(getDate, getTime, x, y, new WeatherData.WeatherCallback() {
            @Override
            public void onSuccess(String weatherInfo) {
                // 이 부분은 메인 스레드에서 UI 작업을 수행해야 함
                handler.post(() -> {
                    loadingImg.setVisibility(View.GONE);
                    int hour = Integer.parseInt(simpleDateFormat2.format(new Date())); // 낮 밤 구분

                    if (weatherInfo != null) {
                        weatherInfoArray = weatherInfo.split(" ");   // 0하늘, 1기온, 2강수 확률, 3눈 or 비, 4습도
                        Log.d("날씨 정보: ", Arrays.toString(weatherInfoArray));
                        showWeather.setText(weatherInfoArray[1]);
                        // 날씨 이미지 세팅 함수
                        setWeatherImg(weatherInfoArray, hour);
                        checkWeatherConditions(weatherInfoArray);   // 추천 아이템
                    } else {
                        showWeather.setText("날씨 정보를 가져오지 못했습니다.");
                    }
                    showWeather.setVisibility(View.VISIBLE);
                    showWeatherImg.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(Exception e) {
                // 오류 처리
                e.printStackTrace();
            }
        });


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

                SharedPreferences sharedPreferences = getSharedPreferences("GPSData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("x", String.valueOf(x)); // x 값을 String으로 저장
                editor.putString("y", String.valueOf(y)); // y 값을 String으로 저장
                editor.apply(); // 변경사항 저장

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

    // 앱 권한 요청 설정 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // gps
        if (requestCode == 100) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }

        // 전화 권한 초기
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되면 전화 걸기 메서드를 호출
                makePhoneCall(emergencyContact);
            } else {
                Log.d("Error", "전화 권한이 거부되었습니다.");
            }
        }

        // 블루투스
        /*
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 Bluetooth 기능 실행
                if (d != null) { // d가 null이 아닐 때만 연결 시도
                    connectToDevice(d);
                } else {
                    Log.d("Bluetooth", "장치가 null입니다. MAC 주소를 확인하세요.");
                }
            } else {
                // 권한이 거부된 경우의 처리
                Log.d("Error", "Bluetooth 권한이 거부되었습니다.");
            }
        }
         */

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

        // 권한 체크
        /*
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    200);
            return;
        }

         */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    100); // 100은 요청 코드입니다.
        }




        if (mBluetoothAdapter == null) {
            Log.d("Bluetooth", "Bluetooth를 지원하지 않는 기기입니다.");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); // 활성화 요청을 위한 요청 코드
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();  // 페어링된 장치 목록
        for (BluetoothDevice device : pairedDevices) {
            Log.d("bluetooth", device.getName().toString());
            Log.d("bluetooth", "MAC ADDRESS : " + device.getAddress().toString());
        }

        d = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("C8:F0:9E:B1:AA:5A");
        connectToDevice(d);
    }

    private void connectToDevice(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Bluetooth", "Bluetooth 권한이 필요합니다.");
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
        } finally {
            if (bluetoothSocket != null) {

            }
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
                    closeSocket(); // 오류 발생 시 소켓 닫기
                    break; // 오류 발생 시 루프 종료
                }
            }
        }).start();
    }

    private boolean canReceiveData = true; // 데이터 수신 여부를 관리하는 변수

    private void handleReceivedData(String data) {
        // 수신한 데이터에 따라 처리
        runOnUiThread(() -> {
            if (data.equals("1") && canReceiveData) {
                btTest.setText("신호: ON");

                showNotifications(getApplicationContext());
                saveAccessRecord();

                canReceiveData = false;
                disableDataReceiving(5000); // 5초 후 데이터 수신 재개
            } else if (data.equals("0")) {
                btTest.setText("신호: OFF");
            }
        });
    }

    public void showNotifications(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("today_task_enabled", true) && stringBuilderTasks != null) {
            showAlert("오늘의 할일", stringBuilderTasks.toString());
        }

        if (preferences.getBoolean("medicine_enabled", true)) {
            showAlert(medicines, time_of_day);
        }


        if(preferences.getBoolean("recommended_enabled", true)) {
            showWeatherAlert(showCoatIcon, showUmbrellaIcon);
        }

        if (preferences.getBoolean("reminder_enabled", true)) {
            showAlert("이것 챙기셨나요?", finalSelection.toString());
        }


    }

    private void showAlert(String titleMessage, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleMessage)
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .setCancelable(true);

        if (message != null) {
            builder.setMessage(message);   // final 이거가 준비물 목록 스트링 담긴거

        }

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 데이터 수신을 재개
    private void disableDataReceiving(int milliseconds) {
        new Handler().postDelayed(() -> {
            canReceiveData = true; // 데이터 수신을 재개
        }, milliseconds);
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


    // 준비물 등록
    private void showChecklistDialog() {
        // 목록 배열
        String[] listItems = getResources().getStringArray(R.array.checklist);
        boolean[] checkedStatus = new boolean[listItems.length];

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("무엇을 가지고 나가야 할까요?");
        builder.setMultiChoiceItems(listItems, checkedStatus, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    checkedItems.add(listItems[which]);
                } else {
                    checkedItems.remove(listItems[which]);
                }
            }
        });
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finalSelection.setLength(0); // StringBuilder 초기화
                for (String item : checkedItems) {
                    finalSelection.append("\n").append(item);
                }
                Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "finalSelection: " + finalSelection);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // 취소 버튼 클릭 시 다이얼로그를 닫음
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // 긴급전화
    private void fetchEmergencyContact(int pkid) {
        //ApiService apiService = retrofit.create(ApiService.class);
        Call<EmergencyContactResponse> call = apiService.getEmergencyContact(pkid);

        call.enqueue(new Callback<EmergencyContactResponse>() {
            @Override
            public void onResponse(Call<EmergencyContactResponse> call, Response<EmergencyContactResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    emergencyContact = response.body().getEmergency_contact();
                    // 전화 걸기
                    makePhoneCall(emergencyContact);
                } else {
                    Log.d("Error", "응답 실패");
                }
            }

            @Override
            public void onFailure(Call<EmergencyContactResponse> call, Throwable t) {
                Log.d("Error", "API 호출 실패: " + t.getMessage());
            }
        });
    }


    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            // 전화 걸기
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }


    // 할일 추가
    private void showAddScheduleDialog(int pkid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 다이얼로그 설정이 다르면 걍 새로 만드는게 나음
        builder.setTitle("일정 등록");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        EditText editTextTask = dialogView.findViewById(R.id.editTextTask);
        EditText editTextDate = dialogView.findViewById(R.id.editTextDate);


        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 날짜를 가져옴.
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        (datePickerView, selectedYear, selectedMonth, selectedDay) -> {
                            // 선택된 날짜를 EditText에 설정
                            //editTextDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                            editTextDate.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay);
                        },
                        year, month, day);

                // 다이얼로그 표시
                datePickerDialog.show();
            }
        });

        builder.setPositiveButton("저장", (dialog, which) -> {
            String task = editTextTask.getText().toString();
            String date = editTextDate.getText().toString();

            Log.d("텍스트박스에 입력한 일정: ", task);
            Log.d("텍스트박스에 입력한 날짜: ", date);

            // 서버로 전송
            sendScheduleToServer(pkid, task, date);

        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void sendScheduleToServer(int fkmember, String tasks, String scheduleDate) {
        ScheduleModel schedule = new ScheduleModel(fkmember, tasks, scheduleDate);

        // Retrofit 으로 서버에 데이터 전송
        //ApiService apiService = retrofit.create(ApiService.class);
        Call<Void> call = apiService.addSchedule(schedule);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "일정이 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    fetchTasks(fkmember); // 일정 등록 성공 시 일정을 다시 가져옴
                } else {
                    Toast.makeText(getApplicationContext(), "일정 등록 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 할일 가져옴
    private void fetchTasks(int pkid) {
        // 회원 ID와 날짜로 데이터 요청
        String fkmember = String.valueOf(pkid);
        Calendar calendar = Calendar.getInstance();
        Date tdayDate = calendar.getTime(); // 현재 날짜로
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        String scheduleDate = simpleDateFormat1.format(tdayDate);


        //ApiService apiService = retrofit.create(ApiService.class);
        apiService.getTasks(fkmember, scheduleDate).enqueue(new Callback<List<ScheduleModel>>() {
            @Override
            public void onResponse(Call<List<ScheduleModel>> call, Response<List<ScheduleModel>> response) {
                if (response.isSuccessful()) {
                    tasks = response.body();
                    displayTasks();
                } else {
                    Toast.makeText(MainActivity.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void displayTasks() {
        if (tasks == null || tasks.isEmpty()) {
            Toast.makeText(this, "오늘의 할일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 여기서 tasks를 표시하는 로직 구현
        List<String> taskStrings = new ArrayList<>();
        for (ScheduleModel task : tasks) {
            taskStrings.add(task.getTasks());
            //Log.d("MainActivity", "할일: " + task.getTasks());
        }
        stringBuilderTasks = new StringBuilder();
        for (String task : taskStrings) {
            stringBuilderTasks.append(task).append("\n"); // 각 작업을 새로운 줄로 추가
        }

        Log.d("MainActivity", "할일 잘 저장됏나: " + stringBuilderTasks.toString());
    }


    // 약
    private void showMediInputDialog(int fkmember) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("약 복용 입력");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_medicine_input, null);
        builder.setView(dialogView);

        EditText morningMedicine = dialogView.findViewById(R.id.etMorningMedi);
        EditText lunchMedicine = dialogView.findViewById(R.id.etLunchMedi);
        EditText dinnerMedicine = dialogView.findViewById(R.id.etEveningMedi);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String morningMedi = morningMedicine.getText().toString();
            String lunchMedi = lunchMedicine.getText().toString();
            String dinnerMedi = dinnerMedicine.getText().toString();


            // 아침, 점심, 저녁 데이터 전송
            if (!morningMedi.isEmpty()) {
                saveMedicineDataToServer(fkmember, morningMedi, "아침");
            }
            if (!lunchMedi.isEmpty()) {
                saveMedicineDataToServer(fkmember, lunchMedi, "점심");
            }
            if (!dinnerMedi.isEmpty()) {
                saveMedicineDataToServer(fkmember, dinnerMedi, "저녁");
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    public void saveMedicineDataToServer(int fkmember, String med_name, String timeOfDay) {
        //ApiService apiService = retrofit.create(ApiService.class);

        MedicineModel medicineData = new MedicineModel(fkmember, med_name, timeOfDay);
        Call<Void> call = apiService.saveMedication(medicineData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Retrofit", "Data saved successfully");
                    Toast.makeText(getApplicationContext(), "약 정보가 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    fetchMedicine(fkmember);
                } else {
                    Log.d("Retrofit", "Failed to save data");
                    Toast.makeText(getApplicationContext(), "약 정보 등록 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
            }
        });
    }


    // 약 정보 가져옴
    private void fetchMedicine(int fkmember) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //String time_of_day = "";

        if (hour >= 6 && hour < 12) {
            time_of_day =  "아침";
        } else if (hour >= 12 && hour < 18) {
            time_of_day =  "점심";
        } else {
            time_of_day =  "저녁";
        }

        //ApiService apiService = retrofit.create(ApiService.class);
        apiService.getMedicines(fkmember, time_of_day).enqueue(new Callback<List<MedicineModel>>() {
            @Override
            public void onResponse(Call<List<MedicineModel>> call, Response<List<MedicineModel>> response) {
                if (response.isSuccessful()) {
                    medicines = response.body();  // showalert 테스트로 냅두고 나중엔 전역변수로 수정 // ㅇ
                    //showAlert(medicines, time_of_day);  // test
                    //showNotifications(getApplicationContext());  // test

                } else {
                    Toast.makeText(MainActivity.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MedicineModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(List<MedicineModel> medicines, String time) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_medicine_list, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewMedi);
        MedicineAdapter adapter = new MedicineAdapter(medicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        builder.setTitle(time + "약 드셨나요?");
        builder.setNegativeButton("닫기", null); // 닫기 버튼

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // 출입 기록 저장
    private void saveAccessRecord() {
        int pkid = sharedPreferences.getInt("userPkid", -1);

        //ApiService apiService = retrofit.create(ApiService.class);
        Call<AccessModel> call = apiService.recordAccess(pkid);
        call.enqueue(new Callback<AccessModel>() {
            @Override
            public void onResponse(@NonNull Call<AccessModel> call, @NonNull Response<AccessModel> response) {
                if (response.isSuccessful()) {
                    Log.d("DB", "출입 기록 저장 성공");
                } else {
                    Log.e("DB", "출입 기록 저장 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessModel> call, Throwable t) {
                Log.e("DB", "출입 기록 저장 실패: " + t.getMessage());
            }
        });
    }


    // 출입 기록 가져오기
    private void fetchAccessRecords(int fkmember) {
        Call<List<AccessModel>> call = apiService.getAccessRecords(fkmember);
        call.enqueue(new Callback<List<AccessModel>>() {
            @Override
            public void onResponse(Call<List<AccessModel>> call, Response<List<AccessModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AccessModel> accessRecords = response.body();
                    for (AccessModel record : accessRecords) {
                        Log.d("DB", "출입 기록: " + record.getAccess_timestamp());
                        checkAccessTimeAndCall(record.getAccess_timestamp());
                    }
                } else {
                    Log.e("DB", "출입 기록 가져오기 실패: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<AccessModel>> call, Throwable t) {
                Log.e("DB", "출입 기록 가져오기 실패: " + t.getMessage());
            }
        });
    }

    // 출입 기록 확인 + 일단 전화 걸기
    private void checkAccessTimeAndCall(String accessTime) {
        // UTC 시간 문자열 포맷
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        try {
            // 시간 문자열을 Date 객체로 변환
            Date date = sdf.parse(accessTime);
            if (date != null) {
                // 현재 시간 구하기
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());
                int hour = Integer.parseInt(hourFormat.format(date));

                Log.d("출입 감지된 시간", String.valueOf(hour));
                // 새벽 시간(00:00 ~ 06:00) 체크
                if (hour >= 0 && hour < 6) {
                    //makePhoneCall(emergencyContact);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace(); // 오류 발생 시 로그 출력
        }
    }

    // 날씨에 따른 추천 아이템
    private void checkWeatherConditions(String[] weatherInfoArray) {
        // 기온과 강수확률 파싱
        //int temperature = Integer.parseInt(weatherInfoArray[1].replace("℃", "").trim());
        //int precipitationProbability = Integer.parseInt(weatherInfoArray[2].replace("%", "").trim());

        int temperature = 0;
        int precipitationProbability = 0;
        if (weatherInfoArray[1].matches("-?\\d+℃")) {
            temperature = Integer.parseInt(weatherInfoArray[1].replace("℃", "").trim());
        } else {
            System.out.println("온도 정보가 유효하지 않습니다."); // 로그 출력 (디버깅 용도)
            return;
        }
        if (weatherInfoArray[2].matches("\\d+%")) {
            precipitationProbability = Integer.parseInt(weatherInfoArray[2].replace("%", "").trim());
        } else {
            System.out.println("강수 확률 정보가 유효하지 않습니다.");
            return;
        }

        StringBuilder alertMessage = new StringBuilder();
        //showCoatIcon = false;
        //showUmbrellaIcon = false;

        // 기온 체크
        if (temperature <= 30) {   // 테스트 30도 이하
            //alertMessage.append("기온이 낮습니다.\n");
            showCoatIcon = true; // 외투 아이콘 표시
        }

        // 강수확률 체크
        if (precipitationProbability >= 0) {    // 테스트 0% 이상
            //alertMessage.append("비가 올 수 있습니다\n");
            showUmbrellaIcon = true; // 우산 아이콘 표시
        }

        // 알림이 있을 경우 AlertDialog 생성
        if (showCoatIcon || showUmbrellaIcon) {
            //showWeatherAlert(alertMessage.toString(), showCoatIcon, showUmbrellaIcon);
            //showWeatherAlert(showCoatIcon, showUmbrellaIcon);
        }
    }

// String message 매개변수 뺏음
    private void showWeatherAlert(boolean showCoatIcon, boolean showUmbrellaIcon) {
        // 커스텀 레이아웃 인플레이트
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_weather_alert, null);

        ImageView imageCoatIcon = dialogView.findViewById(R.id.imageJacketIcon);
        ImageView imageUmbrellaIcon = dialogView.findViewById(R.id.imageUmbrellaIcon);
        TextView textCoatMessage = dialogView.findViewById(R.id.textCoatMessage);
        TextView textUmbrellaMessage = dialogView.findViewById(R.id.textUmbrellaMessage);

        // 아이콘 표시 여부 설정
        if (showCoatIcon) {
            imageCoatIcon.setImageResource(R.drawable.jacket);
            imageCoatIcon.setVisibility(View.VISIBLE); // 외투 아이콘 보이기
            textCoatMessage.setText("기온이 낮습니다.");
            textCoatMessage.setVisibility(View.VISIBLE);
        }

        if (showUmbrellaIcon) {
            imageUmbrellaIcon.setImageResource(R.drawable.umbrella);
            imageUmbrellaIcon.setVisibility(View.VISIBLE); // 우산 아이콘 보이기
            textUmbrellaMessage.setText("비가 올 수 있습니다");
            textUmbrellaMessage.setVisibility(View.VISIBLE);
        }

        // 메시지 설정
        //textWeatherMessage.setText(message);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("이런 물건은 어떠신가요?")
                .setPositiveButton("확인", null)
                .show();
    }


}

