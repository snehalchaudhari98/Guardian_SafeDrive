package com.example.safedrive_guardian.ui.weatherp;

import com.example.safedrive_guardian.ui.weatherp.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("weather")
    Call<WeatherResponse> getWeatherByCity(@Query("q") String city, @Query("appid") String apiKey);
}

