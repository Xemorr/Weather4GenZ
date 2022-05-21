package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {

    public static WeatherDataHandler weatherDataHandler; //object that handles API calls
    private static Scene scene;
    private static final int FORECAST_COUNT = 12;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage_3.fxml"));
        scene = new Scene(fxmlLoader.load(), 335, 660);
        stage.setTitle("Weather4GenZ");
        stage.setScene(scene);
        stage.show();
        Timer timer = new Timer();
        //Executes main loop once every 5 minutes
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mainLoop();
            }
        }, 0, TimeUnit.MINUTES.toMillis(5));
        //Executes time loop once every second (basically ensures the time is always correct)
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLoop();
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }

    public static void timeLoop() {
        //Handle time & date
        Text time_text = (Text) scene.lookup("#time_text");
        Text date_text = (Text) scene.lookup("#date_text");
        LocalDateTime now = LocalDateTime.now();
        date_text.setText(String.format("%s %sth %s", now.getDayOfWeek(), now.getDayOfMonth(), now.getMonth().toString()));
        time_text.setText(String.format("%02d:%02d", now.toLocalTime().getHour(), now.toLocalTime().getMinute()));
    }

    public static void mainLoop() {
        TextField search_input = (TextField) scene.lookup("#search_input");
        String search = search_input.getText();

        
        //Handle current weather data
        weatherDataHandler.locationfromCity(search)
                .exceptionally((throwable) -> {
                    search_input.setText("City Not Found - defaulted to Cambridge");
                    return new WeatherDataHandler.Location(52.205276, 0.199167);
                })
                .thenAccept((location) -> {
                    weatherDataHandler.getCurrentWeatherData(location)
                        .thenAccept((data) -> {
                            try {
                                Text currentTemperatureText = (Text) scene.lookup("#current_temp_text");
                                currentTemperatureText.setText(String.format("%.2f°L", data.getTemperatureInLayers()));
                                Text currentRainText = (Text) scene.lookup("#current_rain_text");
                                currentRainText.setText(String.format("%.0f%% Humidity", data.humidity()));
                                ImageView currentWeatherIcon = (ImageView) scene.lookup("#current_weather_icon");
                                String weatherIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.icon() + ".png").toString();
                                currentWeatherIcon.setImage(new Image(weatherIconPath));

                                //Clothes - Hat
                                ImageView hat = (ImageView) scene.lookup("#clothes_head_icon");
                                String hatIconPath;
                                if (data.feelsLikeTemperature() >= 25) { //sunhat
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/sunhat.png").toString();
                                } else if (data.feelsLikeTemperature() <= 0) { //scarves, woolly hat
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/woollyhat.png").toString();
                                } else {
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/placeholder.png").toString();
                                }
                                hat.setImage(new Image(hatIconPath));

                                //Clothes - Body
                                ImageView body = (ImageView) scene.lookup("#clothes_body_icon");
                                String bodyIconPath;
                                try {
                                    bodyIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + Math.max(Math.min(Math.round(data.getTemperatureInLayers()), 4), 1) + "L.png").toString();
                                } catch (NullPointerException e) {
                                    bodyIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/placeholder.png").toString();
                                }
                                body.setImage(new Image(bodyIconPath));

                                //Clothes - Legs
                                ImageView legs = (ImageView) scene.lookup("#clothes_legs_icon");
                                String legsIconPath;
                                if (data.feelsLikeTemperature() >= 25) {
                                    legsIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/shorts.png").toString();
                                } else {
                                    legsIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/trousers.png").toString();
                                }
                                legs.setImage(new Image(legsIconPath));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    weatherDataHandler.get48HourWeatherData(location)
                            .thenAccept((data) -> {
                                for (int i = 0; i < FORECAST_COUNT; i++) {
                                    Text text = (Text) scene.lookup(String.format("#forecast_%d_text", i));
                                    ImageView icon = (ImageView) scene.lookup(String.format("#forecast_%d_icon", i));
                                    int hour = LocalTime.now().getHour();
                                    hour = (hour + i) % 24;
                                    text.setText(hour % 12 + ((hour >= 12) ? "PM" : "AM"));
                                    String str = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.get(i).icon() + ".png").toString();
                                    icon.setImage(new Image(str));
                                }
                            });
                    weatherDataHandler.get7DayWeatherData(location)
                            .thenAccept(dayData -> {
                               for (int i = 0; i < 7; i++) {

                               }
                            });
                });

    }

    public static void main(String[] args) {
        String token = args[0]; //The token for the openweatherapi
        weatherDataHandler = new WeatherDataHandler(token);
        launch();
    }
}