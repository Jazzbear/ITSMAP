package com.example.assi90.aydabtu_lecture5_exercise2.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;



@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    //no annotation: default to column name = var name
    private String place;

    @ColumnInfo(name = "Creation_date")
    private Date date;
    @ColumnInfo(name = "Task_color")
    private String colorHex;

    public Task(String name, String place){
        this.name = name;
        this.place = place;
        this.colorHex = "0F0F0F";
        this.date = new Date(System.currentTimeMillis());
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
}
