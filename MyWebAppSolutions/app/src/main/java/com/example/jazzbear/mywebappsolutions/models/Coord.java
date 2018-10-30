package com.example.jazzbear.mywebappsolutions.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Class for coordinates, remember that to use the Expose and Serialize methods,
// we need to implement Gson to the project in the app's build.gradle
public class Coord {
    @SerializedName("lon")
    @Expose
    public Double lon;
    @SerializedName("lat")
    @Expose
    public Double lat;
}
