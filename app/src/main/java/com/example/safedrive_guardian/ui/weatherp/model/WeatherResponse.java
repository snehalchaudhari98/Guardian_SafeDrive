package com.example.safedrive_guardian.ui.weatherp.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private Weather[] weather;

    // Getter and Setter for main
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    // Getter and Setter for weather
    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        @SerializedName("humidity")
        private int humidity;

        // Getter and Setter for temp
        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        // Getter and Setter for humidity
        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }

    public static class Weather {
        @SerializedName("main")
        private String main;

        @SerializedName("description")
        private String description;

        // Getter and Setter for main
        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        // Getter and Setter for description
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
