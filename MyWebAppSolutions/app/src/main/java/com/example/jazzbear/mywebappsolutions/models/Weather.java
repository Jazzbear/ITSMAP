package com.example.jazzbear.mywebappsolutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Class for weather description. Serves as lead information
public class Weather {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("main")
    @Expose
    public String main;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("icon")
    @Expose
    public String icon;
}
