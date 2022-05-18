package group.six.weather4genz;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WeatherDataHandler {

    private final String token;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final static String[] times = {"morn", "day", "eve", "night"};

    public WeatherDataHandler(String token) {
        this.token = token;
    }

    /**
     * Use this API endpoint: https://api.openweathermap.org/data/2.5/onecall?lat=latitude_here&lon=longitude_here&exclude=daily,minutely&units=metric&appid=API_key_here
     * @param location - the latitude and longitude of the location you want to check the weather of
     * @return data - All of the relevant data wrapped in a future to preven2t freezing of the application
     */
    public CompletableFuture<List<Data>> get48HourWeatherData(Location location) {
        CompletableFuture<List<Data>> completableFuture = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=daily&units=metric&appid=%s", location.latitude, location.longitude, token))
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
                List<Data> hours48 = new ArrayList<>();
                for (int i = 0; i < 48; i++) { //load the next 48 hours worth of data
                    String weatherType = parse.read(String.format("$.hourly[%s].weather[0].main", i), String.class);
                    String weatherDescription = parse.read(String.format("$.hourly[%s].weather[0].description", i), String.class);
                    String weatherIconName = parse.read(String.format("$.hourly[%s].weather[0].icon", i), String.class);
                    double temperature = parse.read(String.format("$.hourly[%s].temp", i), Double.class);
                    double feelsLikeTemperature = parse.read(String.format("$.hourly[%s].feels_like", i), Double.class);
                    double windSpeed = parse.read(String.format("$.hourly[%s].wind_speed", i), Double.class);
                    hours48.add(new Data(weatherType, weatherDescription, weatherIconName, temperature, feelsLikeTemperature, windSpeed));
                }
                completableFuture.complete(hours48);
            }
        });
        return completableFuture;
    }

    /**
     * Use this API endpoint: https://api.openweathermap.org/data/2.5/onecall?lat=latitude_here&lon=longitude_here&exclude=daily,minutely&units=metric&appid=API_key_here
     * @param location - the latitude and longitude of the location you want to check the weather of
     * @return data - All of the relevant data wrapped in a future to preven2t freezing of the application
     */
    public CompletableFuture<List<DayData>> get7DayWeatherData(Location location) {
        CompletableFuture<List<DayData>> completableFuture = new CompletableFuture<>();
        Request request = new Request.Builder()
                .url(String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&exclude=minutely,hourly&units=metric&appid=%s", location.latitude, location.longitude, token))
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
                List<DayData> days7 = new ArrayList<>();
                for (int i = 0; i < 7; i++) { //load the next 7 days worth of data
                    String weatherType = parse.read(String.format("$.daily[%s].weather[0].main", i), String.class);
                    String weatherDescription = parse.read(String.format("$.daily[%s].weather[0].description", i), String.class);
                    String weatherIconName = parse.read(String.format("$.daily[%s].weather[0].icon", i), String.class);

                    double[] temperaturesAtTimes = new double[4]; //morn, day, eve, night temperature corresponds to each index
                    double[] feelsLikeAtTimes = new double[4];
                    for (int j = 0; i < times.length; i++) { //morn, day, eve, night
                        temperaturesAtTimes[j] = parse.read(String.format("$.daily[%s].temp.%s", i, times[j]), Double.class);
                        feelsLikeAtTimes[j] = parse.read(String.format("$.daily[%s].feels_like.%s", i, times[j]), Double.class);
                    }
                    DayTemperatures dayTemperatures = new DayTemperatures(temperaturesAtTimes[0], temperaturesAtTimes[1], temperaturesAtTimes[2], temperaturesAtTimes[3]);
                    DayTemperatures feelsLikeTemperature = new DayTemperatures(feelsLikeAtTimes[0], feelsLikeAtTimes[1], feelsLikeAtTimes[2], feelsLikeAtTimes[3]);
                    double windSpeed = parse.read(String.format("$.daily[%s].wind_speed", i), Double.class);
                    days7.add(new DayData(weatherType, weatherDescription, weatherIconName, windSpeed, dayTemperatures, feelsLikeTemperature));
                }
                completableFuture.complete(days7);
            }
        });
        return completableFuture;
    }

    /**
     * Use this API endpoint: https://api.openweathermap.org/data/2.5/weather?lat=latitude_here&lon=longitude_here&appid=token_here
     * @param location - the latitude and longitude of the location you want to check the weather of
     * @return data - All of the relevant data wrapped in a future to preven2t freezing of the application
     */
    public CompletableFuture<Data> getCurrentWeatherData(Location location) {
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
                double humidity = parse.read("$.main.humidity", Double.class);

                completableFuture.complete(new Data(weatherType, weatherDescription, weatherIconName, temperature, feelsLikeTemperature, humidity));
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
                try {
                    String json = response.body().string();
                    DocumentContext parse = JsonPath.parse(json);
                    double latitude = parse.read("$[0].lat", Double.class);
                    double longitude = parse.read("$[0].lon", Double.class);
                    completableFuture.complete(new Location(latitude, longitude));
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
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
    public static final record Data(String weatherType, String weatherDescription, String icon, double temperature, double feelsLikeTemperature, double humidity) {
        public double getTemperatureInLayers() {
            return Math.max((41 - feelsLikeTemperature) / 13.5, 1);
        }
    }

    /**
     * The record that holds the various temperatures corresponding to general sections of the day
     */
    public static final record DayTemperatures(double morningTemperature, double dayTemperature, double eveningTemperature, double nightTemperature) {}

    /**
     * Holds the data given for daily data (this is in a weirder format than Data allows, multiple temperatures per day but not multiple weather readings)
     */
    public static final record DayData(String weatherType, String weatherDescription, String icon, double windSpeed, DayTemperatures temperatures, DayTemperatures feelsLikeTemperatures) {}
}
