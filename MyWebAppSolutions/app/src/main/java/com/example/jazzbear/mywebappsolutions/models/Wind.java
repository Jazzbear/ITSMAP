package com.example.jazzbear.mywebappsolutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    @Expose
    public Double speed;
    @SerializedName("deg") // Degrees used to tell witch direction it blows in.
    @Expose
    public Double deg;
}
