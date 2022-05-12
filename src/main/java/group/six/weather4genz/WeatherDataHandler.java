package group.six.weather4genz;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WeatherDataHandler {

    private final String token;
    private final OkHttpClient okHttpClient = new OkHttpClient();


    public WeatherDataHandler(String token) {
        this.token = token;
    }

    /**
     * Use this API endpoint: https://api.openweathermap.org/data/2.5/weather?lat=latitude_here&lon=longitude_here&appid=token_here
     * @param location - the latitude and longitude of the location you want to check the weather of
     * @return data - All of the relevant data wrapped in a future to preven2t freezing of the application
     */
    public CompletableFuture<Data> getWeatherData(Location location) {
        CompletableFuture<Data> completableFuture = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric", location.latitude, location.longitude, token))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                DocumentContext parse = JsonPath.parse(json);
                String weatherType = parse.read("$.weather[0].main", String.class);
                String weatherDescription = parse.read("$.weather[0].description", String.class);
                String weatherIconName = parse.read("$.weather[0].icon", String.class);
                double temperature = parse.read("$.main.temp", Double.class);
                double feelsLikeTemperature = parse.read("$.main.feels_like", Double.class);
                double windSpeed = parse.read("$.wind.speed", Double.class);

                completableFuture.complete(new Data(weatherType, weatherDescription, weatherIconName, temperature, feelsLikeTemperature, windSpeed));
            }
        });
        return completableFuture;
    }

    /**
     * Use this API endpoint:  http://api.openweathermap.org/geo/1.0/direct?q=city_name_here&limit=1&appid=token_here
     * API documentation: https://openweathermap.org/api/geocoding-api
     * @param cityName - the name of the city you want to get the location of
     * @return location - latitude and longitude of the city in question wrapped in a future to prevent freezing of the application
     */
    public CompletableFuture<Location> locationfromCity(String cityName) {
        CompletableFuture<Location> completableFuture = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s", cityName, token))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                DocumentContext parse = JsonPath.parse(json);
                double latitude = parse.read("$[0].lat", Double.class);
                double longitude = parse.read("$[0].lon", Double.class);

                completableFuture.complete(new Location(latitude, longitude));
            }
        });
        return completableFuture;
        //return CompletableFuture.completedFuture(new Location(51.5, -0.118));
    }

    /**
     * The record that holds the latitude and longitude of a given location you want to find the weather information of
     */
    public static final record Location(double latitude, double longitude) {}

    /**
     * The record that holds the relevant data from the API
     * Temperature and FeelsLike are measured in Celsius, Wind Speed in m/s
     */
    public static final record Data(String weatherType, String weatherDescription, String icon, double temperature, double feelsLikeTemperature, double windSpeed) { }
}
