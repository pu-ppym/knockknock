package com.example.knockknock.model;

public class MemberModel {
    private int pkid;
    private String user_id;
    private String user_pw;
    private String name;
    private String emergency_contact;


    public MemberModel(String user_id, String user_pw, String name, String emergency_contact) {
        this.user_id = user_id;
        this.user_pw = user_pw;
        this.name = name;
        this.emergency_contact = emergency_contact;
    }

    public MemberModel(String user_id, String user_pw) {
        this.user_id = user_id;
        this.user_pw = user_pw;
    }

    public int getPkid() {
        return pkid;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_pw() {
        return user_pw;
    }

    public void setUser_pw(String user_pw) {
        this.user_pw = user_pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmergency_contact() {
        return emergency_contact;
    }

    public void setEmergency_contact(String emergency_contact) {
        this.emergency_contact = emergency_contact;
    }
}
