package com.example.knockknock.model;

public class MedicineModel {
    private int fkmember;
    private String med_name;
    private String time_of_day;

    public MedicineModel(int fkmember, String med_name, String time_of_day) {
        this.fkmember = fkmember;
        this.med_name = med_name;
        this.time_of_day = time_of_day;
    }

    public String getMed_name() {
        return med_name;
    }
}
