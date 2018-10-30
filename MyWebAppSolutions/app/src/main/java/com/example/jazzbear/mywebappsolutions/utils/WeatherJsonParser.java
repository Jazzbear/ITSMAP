package com.example.jazzbear.mywebappsolutions.utils;

import com.example.jazzbear.mywebappsolutions.models.CityWeather;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

//utility class for parsing json data returned from the OpenWeatherMap API
public class WeatherJsonParser {
    //for conversion of temperature to celcius
    private static final double TO_CELCIOUS_FROM_KELVIN = -273.15;

    //example of simple JSON parsing
    public static String parseCityWeatherJson(String jsonString) {
        // in this example the properties are mapped one by one by matching the name tags,
        // exactly similar to how it would be done in a C# project or Angular project, mapping json to DTO's
        String weatherString = "could not parse json";
        try {
            JSONObject cityWeatherJson = new JSONObject(jsonString);
            String name = cityWeatherJson.getString("name");
            JSONObject measurements = cityWeatherJson.getJSONObject("main");
            weatherString = name + " " + measurements.toString();
            // measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main") + " : " +
            // measurements.getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return weatherString;
    }

    //example of parsing with Gson - note that the Gson parser uses the model object CityWeather,
    // Clouds, Coord, Main, Sys, Weather and Wind extracted with http://www.jsonschema2pojo.org/
    public static String parseCityWeatherJsonWithGson(String jsonString) {
        Gson gson = new GsonBuilder().create();
        CityWeather weatherInfo = gson.fromJson(jsonString, CityWeather.class); // Map json input string as a CityWeather object.
        if (weatherInfo != null) {
            // Return relevant info, Name of city, temperature in celcious, and country name.
            return weatherInfo.name + "\n" + "Temp: " + (weatherInfo.main.temp.doubleValue() + TO_CELCIOUS_FROM_KELVIN) + "\u2103" + //unicode for celcius
                    "\nCountry: " + weatherInfo.sys.country;
        } else {
            return "could not parse with gson";
        }
    }
}
