package com.example.jazzbear.mywebappsolutions.utils;

public class GlobalConstants {
    public static final String CONNECT_LOG = "CONNECTIVITY";

    public static final String WEATHER_API_KEY = "1d6e60935af8d2a787cf04a580e5983f";
    public static final long CITY_ID_AARHUS = 2624652;

    // The &APPID= header parameter is specified by the OpenWeatherMap api documentations
    // HTTP CALL STRING, used by volley
    public static final String WEATHER_API_CALL = "http://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID_AARHUS + "&APPID=" + WEATHER_API_KEY;
    // HTTPS CALL STRING, used by HttpsURLConnect
    public static final String WEATHER_API_HTTPS_CALL = "https://api.openweathermap.org/data/2.5/weather?id=" + CITY_ID_AARHUS + "&APPID=" + WEATHER_API_KEY;

}
