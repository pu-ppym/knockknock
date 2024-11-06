package com.example.test;

public class BusArrivalItem {

    private String arrprevstationcnt;
    private String arrtime;
    private String routeno;
    private String vehicletp;

    public String getArrprevstationcnt() {
        return arrprevstationcnt;
    }

    public void setArrprevstationcnt(String arrprevstationcnt) {
        this.arrprevstationcnt = arrprevstationcnt;
    }

    public String getArrtime() {
        int intArrtime = Integer.parseInt(arrtime)/60;
        String strArrtime = "";
        if(intArrtime != 0){
            strArrtime = String.valueOf(intArrtime)+"분 전";
        }else {
            strArrtime = "곧 도착";
        }

        return strArrtime;
    }

    public void setArrtime(String arrtime) {
        this.arrtime = arrtime;
    }

    public String getRouteno() {
        return routeno;
    }

    public void setRouteno(String routeno) {
        this.routeno = routeno;
    }

    public String getVehicletp() {
        return vehicletp;
    }

    public void setVehicletp(String vehicletp) {
        this.vehicletp = vehicletp;
    }

    public BusArrivalItem(String arrprevstationcnt, String arrtime, String routeno, String vehicletp) {
        this.arrprevstationcnt = arrprevstationcnt;
        this.arrtime = arrtime;
        this.routeno = routeno;
        this.vehicletp = vehicletp;
    }
}
