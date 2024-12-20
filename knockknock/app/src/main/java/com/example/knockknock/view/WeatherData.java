package com.example.knockknock.view;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherData {
    private String sky, temperature, rain, snow, humidity;

    public void lookUpWeatherAsync(String date, String time, String nx, String ny, WeatherCallback callback) {
        new Thread(() -> {
            try {
                String weatherData = lookUpWeather(date, time, nx, ny);
                // 메인 스레드에서 콜백 호출
                callback.onSuccess(weatherData);
            } catch (IOException | JSONException e) {
                callback.onError(e);
            }
        }).start();
    }

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
        JSONObject jsonObj_2;

        // response에서 body를 찾기
        try {
            jsonObj_2 = jsonObj_1.getJSONObject("response");
        } catch (JSONException e) {
            Log.e("JSON", "No 'response' found in JSON", e);
            return "데이터를 찾을 수 없습니다.";
        }

        // body로부터 items 찾기
        JSONObject jsonObj_3;
        try {
            jsonObj_3 = jsonObj_2.getJSONObject("body");
        } catch (JSONException e) {
            Log.e("JSON", "No 'body' found in response", e);
            return "데이터를 찾을 수 없습니다.";
        }

        JSONArray jsonArray;
        try {
            jsonArray = jsonObj_3.getJSONObject("items").getJSONArray("item");
        } catch (JSONException e) {
            Log.e("JSON", "No 'items' found in body", e);
            return "데이터를 찾을 수 없습니다.";
        }




/*

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
*/


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj_4 = jsonArray.getJSONObject(i);
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


            if(category.equals("POP")) {    // 강수확률
                rain = fcstValue + "% ";
            }

            if(category.equals("PTY")) {
                if (fcstValue.equals("1")) {
                    snow = "비 ";
                } else if (fcstValue.equals("2")) {
                    snow = "비/눈 ";
                } else if (fcstValue.equals("3")) {
                    snow = "눈 ";
                } else if (fcstValue.equals("4")) {
                    snow = "소나기 ";
                } else {
                    snow = "없음 ";
                }
            }

            if(category.equals("REH")) {
                humidity = fcstValue + "%";
            }

        }


        return sky + temperature + rain + snow + humidity;
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

    // WeatherCallback 인터페이스
    public interface WeatherCallback {
        void onSuccess(String weatherData);
        void onError(Exception e);
    }
}
