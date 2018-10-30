package com.example.jazzbear.mywebappsolutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// Weather class with all the sub model properties.
// remember that to use the Expose and Serialize methods,
// we need to implement Gson to the project in the app's build.gradle
public class CityWeather {

    @SerializedName("coord")
    @Expose
    public Coord coord;

    @SerializedName("weather")
    @Expose
    public List<Weather> weather = null; // list of weather descriptions

    @SerializedName("base")
    @Expose
    public String base;

    @SerializedName("main")
    @Expose
    public Main main;

    @SerializedName("wind")
    @Expose
    public Wind wind;

    @SerializedName("clouds")
    @Expose
    public Clouds clouds;

    @SerializedName("dt") // date
    @Expose
    public Integer dt;

    @SerializedName("sys")
    @Expose
    public Sys sys;

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("cod")
    @Expose
    public Integer cod;
}
