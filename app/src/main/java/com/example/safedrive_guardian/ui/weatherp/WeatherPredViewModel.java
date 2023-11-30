package com.example.safedrive_guardian.ui.weatherp;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.safedrive_guardian.ui.weatherp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherPredViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;

    public WeatherPredViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is weather fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    private WeatherService createWeatherService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WeatherService.class);
    }

    public void fetchWeatherPrediction(String city, String apiKey) {
        WeatherService service = createWeatherService();
        service.getWeatherByCity(city, apiKey).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    double temperature = weatherResponse.getMain().getTemp() - 273.15; // Convert Kelvin to Celsius
                    String generalCondition = weatherResponse.getWeather()[0].getMain();

                    String weatherStatus;
                    if (temperature > 30) {
                        weatherStatus = "It's hot outside.";
                    } else if (temperature < 10) {
                        weatherStatus = "It's cold outside.";
                    } else {
                        weatherStatus = "The temperature is moderate.";
                    }

                    if (generalCondition.equalsIgnoreCase("Rain") || generalCondition.equalsIgnoreCase("Snow")) {
                        weatherStatus += " Also, it's " + generalCondition.toLowerCase() + "ing.";
                    }

                    mText.postValue(weatherStatus);
                } else {
                    mText.postValue("Error fetching weather data");
                }
            }


            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAPI", "Error fetching weather data", t);
                mText.postValue("Network error: " + t.getMessage());
            }
        });
    }


}