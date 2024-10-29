package com.example.knockknock.model;

public class ScheduleModel {
    private int fkmember;
    private String tasks;
    private String schedule_date;

    public ScheduleModel(int fkmember, String tasks, String schedule_date) {
        this.fkmember = fkmember;
        this.tasks = tasks;
        this.schedule_date = schedule_date;
    }

    public String getTasks() {
        return tasks;
    }

}
