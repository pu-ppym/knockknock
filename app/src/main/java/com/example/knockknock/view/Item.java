package com.example.knockknock.view;

public class Item {
    private int arrtime;
    private int arrprevstationcnt;
    private int routeno;
    private String vehicletp;

    public Item(int routeno) {
        this.routeno = routeno;
    }

    public Item(int arrtime, int arrprevstationcnt, String vehicletp) {
        this.arrtime = arrtime;
        this.arrprevstationcnt = arrprevstationcnt;
        this.vehicletp = vehicletp;
    }

    public int getRouteno() {
        return routeno;
    }


}
