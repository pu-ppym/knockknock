package com.example.test;

import android.app.Activity;
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
import java.util.Timer;


public class BusArrivalData{
    private static String citycode;
    private static String nodeid;
    private static String nodeno;
    private static String nodenm;
    private String arrprevstationcnt;
    private String arrtime;
    private String routeno;
    private String vehicletp;

    // 실시간 업데이트 어쩌구
//    private Timer timerCall;
//    private int nCnt;


    // 버스 정류소 정보 파싱
    public static void busStn() throws IOException, JSONException {
        String bsSttnApiUrl = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList";
        String serviceKey = "szed3dpHQow1gk6%2BHE%2F%2BuBo82pVn8Hv0uZ6hfaU7aD2bcLPxB5MLa8yDAeQxt%2BzxpnIplahGhvA5%2BeM7xqmoQw%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(bsSttnApiUrl); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("20", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터 타입(xml, json)*/
        urlBuilder.append("&" + URLEncoder.encode("gpsLati","UTF-8") + "=" + URLEncoder.encode("37.54", "UTF-8")); /*WGS84 위도 좌표*/
        urlBuilder.append("&" + URLEncoder.encode("gpsLong","UTF-8") + "=" + URLEncoder.encode("126.72", "UTF-8")); /*WGS84 경도 좌표*/

        URL url = new URL(urlBuilder.toString());
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

        try {
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
                citycode = jsonObj_04.getString("citycode");
                nodeid = jsonObj_04.getString("nodeid");
                nodenm = jsonObj_04.getString("nodenm");
                nodeno = jsonObj_04.getString("nodeno");
            }
        } catch (JSONException e){
            Log.i("busArrival", e.getMessage());

        }
        Log.d("정류소정보", citycode+nodeid+nodenm+nodeno);
    }
    // 끝


    // 버스 도착 정보 파싱
    public String busArrival(String bNodeId, String bCityCode) throws IOException, JSONException {
        String bsApiUrl = "http://apis.data.go.kr/1613000/ArvlInfoInqireService";

        String serviceKey = "szed3dpHQow1gk6%2BHE%2F%2BuBo82pVn8Hv0uZ6hfaU7aD2bcLPxB5MLa8yDAeQxt%2BzxpnIplahGhvA5%2BeM7xqmoQw%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(bsApiUrl); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("50", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*데이터 타입(xml, json)*/
        urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode(bCityCode, "UTF-8")); /*도시코드 [상세기능3 도시코드 목록 조회]에서 조회 가능*/
        urlBuilder.append("&" + URLEncoder.encode("nodeId", "UTF-8") + "=" + URLEncoder.encode(bNodeId, "UTF-8")); /*정류소ID [국토교통부(TAGO)_버스정류소정보]에서 조회가능*/


        URL url = new URL(urlBuilder.toString());
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

        try {
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
                arrprevstationcnt = jsonObj_04.getString("arrprevstationcnt");
                arrtime = jsonObj_04.getString("arrtime");
                routeno = jsonObj_04.getString("routeno");
                vehicletp = jsonObj_04.getString("vehicletp");
            }
        } catch (JSONException e){
            Log.i("busArrival", e.getMessage());

        }
        return arrprevstationcnt + arrtime + routeno + vehicletp;
    }
    // 끝

}
