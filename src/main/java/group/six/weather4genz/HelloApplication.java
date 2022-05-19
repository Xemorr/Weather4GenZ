package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import com.jayway.jsonpath.PathNotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {

    public static WeatherDataHandler weatherDataHandler; //object that handles API calls
    private static Scene scene;
    private static final int forecastCount = 12;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage_3.fxml"));
        scene = new Scene(fxmlLoader.load(), 335, 600);
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    weatherDataHandler.get48HourWeatherData(location)
                            .thenAccept((data) -> {
                                for (int i = 0; i < forecastCount; i++) {
                                    Text text = (Text) scene.lookup(String.format("#forecast_%d_text", i));
                                    ImageView icon = (ImageView) scene.lookup(String.format("#forecast_%d_icon", i));
                                    int hour = LocalTime.now().getHour();
                                    hour += i;
                                    text.setText(hour % 12 + ((hour >= 12) ? "PM" : "AM"));
                                    String str = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.get(i).icon() + ".png").toString();
                                    icon.setImage(new Image(str));
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