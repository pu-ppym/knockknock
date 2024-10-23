package com.example.knockknock.view;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class BusArrivalInfo {
    public static void main(String[] args) throws IOException, JSONException {
        String bsApiUrl = "http://apis.data.go.kr/1613000/ArvlInfoInqireService";

        String serviceKey = "szed3dpHQow1gk6%2BHE%2F%2BuBo82pVn8Hv0uZ6hfaU7aD2bcLPxB5MLa8yDAeQxt%2BzxpnIplahGhvA5%2BeM7xqmoQw%3D%3D";

        ArrayList<ArrayList> list = new ArrayList<>();

        StringBuilder urlBuilder = new StringBuilder(bsApiUrl); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*데이터 타입(xml, json)*/
        urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode("25", "UTF-8")); /*도시코드 [상세기능3 도시코드 목록 조회]에서 조회 가능*/
        urlBuilder.append("&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + URLEncoder.encode("DJB8001793", "UTF-8")); /*정류소ID [국토교통부(TAGO)_버스정류소정보]에서 조회가능*/

        try {
            URL url = new URL(urlBuilder.toString());
            System.out.println("url : " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
//        System.out.println("Response code: " + conn.getResponseCode());

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
            System.out.println(sb.toString());
            String result = sb.toString();

            JSONObject jsonObj_01 = new JSONObject(result);
            String response = jsonObj_01.getString("response");

            JSONObject jsonObj_02 = new JSONObject(response);
            String body = jsonObj_02.getString("body");

            JSONObject jsonObj_03 = new JSONObject(body);
            String items = jsonObj_03.getString("items");

            JSONObject jsonObj_04 = new JSONObject(items);
            JSONArray jsonArray = jsonObj_04.getJSONArray("item");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj_04 = jsonArray.getJSONObject(i);
                int arrprevstationcnt = jsonObj_04.getInt("arrprevstationcnt");
                int arrtime = jsonObj_04.getInt("arrtime");
                int routeno = jsonObj_04.getInt("routeno");
                String vehicletp = jsonObj_04.getString("vehicletp");


                Log.i("arrprevstationcnt", String.valueOf(arrprevstationcnt));
                Log.i("arrtime", String.valueOf(arrtime));
                Log.i("routeno", String.valueOf(routeno));
                Log.i("vehicletp", vehicletp);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
