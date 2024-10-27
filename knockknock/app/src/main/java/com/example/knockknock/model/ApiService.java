package com.example.knockknock.model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/register")
    Call<Void> registerMember(@Body MemberModel member);

    @POST("/login")
    Call<MemberModel> login(@Body MemberModel member);

    @GET("getEmergencyContact/{pkid}")
    Call<EmergencyContactResponse> getEmergencyContact(@Path("pkid") int pkid);
}
