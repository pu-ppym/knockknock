package com.example.knockknock.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.knockknock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView ShowDate;
    private TextView ShowTime;
    String test;
    TextView showWeater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ShowDate = (TextView) findViewById(R.id.textView4);
        ShowTime = (TextView) findViewById(R.id.textView5);
        //showWeater = findViewById(R.id.textViewWd);


        ShowTimeMethod();

        WeatherData wd = new WeatherData();
        new Thread(() -> {
            try {
                test = wd.lookUpWeather("20240919","0500","56","126");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            System.out.println("날씨 테스트: "+test);
        }).start();



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



    public class WeatherData {

        private String sky, temperature, wind, rain, snow, humidity;

        public String lookUpWeather(String date, String time, String nx, String ny) throws IOException, JSONException {
            String baseDate = date; // 2022xxxx 형식을 사용해야 함
            String baseTime = timeChange(time); // 0500 형식을 사용해야 함
            String type = "json";

            // end point 주소값
            String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
            // 일반 인증키(encoding)
            String serviceKey = "DrAzMMJkHFq%2BmVkLJyCir2O6kJ81fMnRX57xZEgcutB%2Be%2BU8mkOvLudKfZQ4poGy9FgqAhTFdp9OWDv0rHU6SA%3D%3D";


            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey); // 서비스 키
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); // x좌표
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); // y좌표
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("14", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */

            /*
             * GET방식으로 전송해서 파라미터 받아오기
             */
            URL url = new URL(urlBuilder.toString());
            // json데이터들을 웹페이지를통해 확인할 수 있게  로그캣에 링크 출력
            System.out.println("url : " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            String result = sb.toString();



            // response 키를 가지고 데이터를 파싱
            JSONObject jsonObj_1 = new JSONObject(result);
            String response = jsonObj_1.getString("response");

            // response 로 부터 body 찾기
            JSONObject jsonObj_2 = new JSONObject(response);
            String body = jsonObj_2.getString("body");

            // body 로 부터 items 찾기
            JSONObject jsonObj_3 = new JSONObject(body);
            String items = jsonObj_3.getString("items");
            Log.i("ITEMS", items);

            // items로 부터 itemlist 를 받기
            JSONObject jsonObj_4 = new JSONObject(items);
            JSONArray jsonArray = jsonObj_4.getJSONArray("item");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj_4 = jsonArray.getJSONObject(i);
                String fcstValue = jsonObj_4.getString("fcstValue");
                String category = jsonObj_4.getString("category");

                if (category.equals("SKY")) {
                    if (fcstValue.equals("1")) {
                        sky = "맑음 ";
                        //current_weather_code = "1";
                    } else if (fcstValue.equals("2")) {
                        sky = "비 ";
                        //current_weather_code = "2";
                    } else if (fcstValue.equals("3")) {
                        sky = "구름많음 ";
                        //current_weather_code = "3";
                    } else if (fcstValue.equals("4")) {
                        sky = "흐림 ";
                        //current_weather_code = "4";
                    }
                }

                if (category.equals("TMP")) {
                    temperature = fcstValue + "℃ ";
                }

                if(category.equals("WSD")) {
                    wind = fcstValue + "m/s ";
                }

                if(category.equals("POP")) {
                    rain = fcstValue + "% ";
                }
                if(category.equals("SNO")) {
                    snow = fcstValue + " ";
                }
                if(category.equals("REH")) {
                    humidity = fcstValue + "%";
                }

            }


            return sky + rain + temperature + wind + snow + humidity;
        }

        public String timeChange(String time)
        {
            // 현재 시간에 따라 데이터 시간 설정(3시간 마다 업데이트) //
            switch(time) {
                case "0200":
                case "0300":
                case "0400":
                    time = "0200";
                    break;
                case "0500":
                case "0600":
                case "0700":
                    time = "0500";
                    break;
                case "0800":
                case "0900":
                case "1000":
                    time = "0800";
                    break;
                case "1100":
                case "1200":
                case "1300":
                    time = "1100";
                    break;
                case "1400":
                case "1500":
                case "1600":
                    time = "1400";
                    break;
                case "1700":
                case "1800":
                case "1900":
                    time = "1700";
                    break;
                case "2000":
                case "2100":
                case "2200":
                    time = "2000";
                    break;
                case "2300":
                case "0000":
                case "0100":
                    time = "2300";

            }
            return time;
        }

    }

}

