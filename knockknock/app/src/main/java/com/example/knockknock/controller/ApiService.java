package com.example.knockknock.controller;

import com.example.knockknock.model.EmergencyContactResponse;
import com.example.knockknock.model.MedicineModel;
import com.example.knockknock.model.MemberModel;
import com.example.knockknock.model.ScheduleModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/register")
    Call<Void> registerMember(@Body MemberModel member);

    @POST("/login")
    Call<MemberModel> login(@Body MemberModel member);

    @GET("getEmergencyContact/{pkid}")
    Call<EmergencyContactResponse> getEmergencyContact(@Path("pkid") int pkid);

    @POST("/schedules")
    Call<Void> addSchedule(@Body ScheduleModel schedule);

    @GET("getTasks/{fkmember}/{schedule_date}")
    Call<List<ScheduleModel>> getTasks(
            @Path("fkmember") String fkmember,
            @Path("schedule_date") String scheduleDate
    );

    @POST("/update")
    Call<Void> updateMember(@Body MemberModel member);

    @POST("/medications")
    Call<Void> saveMedication(@Body MedicineModel medicineModel);

    @GET("medications/{fkmember}/{time_of_day}")
    Call<List<MedicineModel>> getMedicines(
            @Path("fkmember") int fkmember,
            @Path("time_of_day") String time_of_day
    );
}
