package group.six.weather4genz;

import java.util.concurrent.CompletableFuture;

public class WeatherDataHandler {

    private final String token;

    public WeatherDataHandler(String token) {
        this.token = token;
    }

    /**
     * Use this API endpoint: https://api.openweathermap.org/data/2.5/weather?lat=latitude_here&lon=longitude_here&appid=token_here
     * @param location - the latitude and longitude of the location you want to check the weather of
     * @return data - All of the relevant data wrapped in a future to prevent freezing of the application
     */
    public CompletableFuture<Data> getWeatherData(Location location) {
        return CompletableFuture.completedFuture(new Data("Rain", "light rain", 10, 9, 1.5)); //Placeholder this function needs filling out
    }

    /**
     * Use this API endpoint:  http://api.openweathermap.org/geo/1.0/direct?q=city_name_here&limit=1&appid=token_here
     * API documentation: https://openweathermap.org/api/geocoding-api
     * @param cityName - the name of the city you want to get the location of
     * @return location - latitude and longitude of the city in question wrapped in a future to prevent freezing of the application
     */
    public CompletableFuture<Location> locationfromCity(String cityName) {
        return CompletableFuture.completedFuture(new Location(51.5, -0.118));
    }

    /**
     * The record that holds the latitude and longitude of a given location you want to find the weather information of
     */
    public static final record Location(double latitude, double longitude) {}

    /**
     * The record that holds the relevant data from the API
     */
    public static final record Data(String weatherType, String weatherDescription, double temperature, double feelsLikeTemperature, double windSpeed) {}
}
