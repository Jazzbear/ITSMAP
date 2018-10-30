package com.example.jazzbear.mywebappsolutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Class for system info
public class Sys {
    @SerializedName("message")
    @Expose
    public Double message;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("sunrise")
    @Expose
    public Integer sunrise;
    @SerializedName("sunset")
    @Expose
    public Integer sunset;
}
