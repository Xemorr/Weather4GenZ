package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {

    public static WeatherDataHandler weatherDataHandler; //object that handles API calls
    public static Scene scene;
    private static final int FORECAST_COUNT = 12;
    public static boolean useLayer = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage_3.fxml"));
        scene = new Scene(fxmlLoader.load(), 335, 660);
        stage.setTitle("Weather4GenZ");
        stage.setScene(scene);
        lightMode(scene.getRoot());
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
        Platform.runLater(() -> date_text.setText(String.format("%s %sth %s", now.getDayOfWeek(), now.getDayOfMonth(), now.getMonth().toString())));
        Platform.runLater(() -> time_text.setText(String.format("%02d:%02d", now.toLocalTime().getHour(), now.toLocalTime().getMinute())));
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
                                currentTemperatureText.setText(formatTemperature(data));
                                Text currentRainText = (Text) scene.lookup("#current_rain_text");
                                currentRainText.setText(String.format("%.0f%% Humidity", data.humidity()));
                                ImageView currentWeatherIcon = (ImageView) scene.lookup("#current_weather_icon");
                                String weatherIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.icon() + ".png").toString();
                                Platform.runLater(() -> currentWeatherIcon.setImage(new Image(weatherIconPath)));

                                //Clothes - Hat
                                ImageView hat = (ImageView) scene.lookup("#clothes_head_icon");
                                String hatIconPath;
                                if (data.feelsLikeTemperature() >= 25) { //sunhat
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/sunhat.png").toString();
                                } else if (data.feelsLikeTemperature() <= 0) { //scarves, woolly hat
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/woollyhat.png").toString();
                                } else {
                                    hatIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/blank.png").toString();
                                }
                                Platform.runLater(() -> hat.setImage(new Image(hatIconPath)));

                                //Clothes - Body
                                ImageView body = (ImageView) scene.lookup("#clothes_body_icon");
                                String bodyIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + Math.max(Math.min(Math.round(data.getTemperatureInLayers()), 4), 1) + "L.png").toString();
                                Platform.runLater(() -> body.setImage(new Image(bodyIconPath)));
                                //Clothes - Legs
                                ImageView legs = (ImageView) scene.lookup("#clothes_legs_icon");
                                String legsIconPath;
                                if (data.feelsLikeTemperature() >= 25) {
                                    legsIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/shorts.png").toString();
                                } else {
                                    legsIconPath = HelloApplication.class.getResource("/group/six/weather4genz/icons/trousers.png").toString();
                                }
                                Platform.runLater(() -> legs.setImage(new Image(legsIconPath)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    weatherDataHandler.get48HourWeatherData(location)
                            .thenAccept((data) -> {
                                try {
                                    for (int i = 0; i < FORECAST_COUNT; i++) {
                                        Label label = (Label) scene.lookup(String.format("#forecast_%d_text", i));
                                        ImageView icon = (ImageView) scene.lookup(String.format("#forecast_%d_icon", i));
                                        int hour = (LocalTime.now().getHour() + i) % 24;
                                        Platform.runLater(() -> label.setText(hour % 12 + ((hour >= 12) ? "PM" : "AM")));
                                        String str = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.get(i).icon() + ".png").toString();
                                        Platform.runLater(() -> icon.setImage(new Image(str)));
                                    }
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            });
                    weatherDataHandler.get7DayWeatherData(location)
                            .thenAccept(dayDatas -> {
                               for (int i = 0; i < 7; i++) {
                                   try {
                                       Label dayName = (Label) scene.lookup(String.format("#day_%s", i));
                                       String dayNameStr = LocalDateTime.now().plusDays(i).getDayOfWeek().toString().substring(0, 3);
                                       Platform.runLater(() -> dayName.setText(dayNameStr));

                                       ImageView morning = (ImageView) scene.lookup(String.format("#image_%s%s", i, 0));
                                       ImageView day = (ImageView) scene.lookup(String.format("#image_%s%s", i, 1));
                                       ImageView evening = (ImageView) scene.lookup(String.format("#image_%s%s", i, 2));
                                       ImageView night = (ImageView) scene.lookup(String.format("#image_%s%s", i, 3));
                                       WeatherDataHandler.DayData dayData = dayDatas.get(i);
                                       String weatherTypePath = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + dayData.icon() + ".png").toString();
                                       morning.setImage(new Image(weatherTypePath));
                                       day.setImage(new Image(weatherTypePath));
                                       evening.setImage(new Image(weatherTypePath));
                                       night.setImage(new Image(weatherTypePath));
                                       Label morningLabel = (Label) scene.lookup(String.format("#label_%s%s", i, 0));
                                       Label dayLabel = (Label) scene.lookup(String.format("#label_%s%s", i, 1));
                                       Label eveningLabel = (Label) scene.lookup(String.format("#label_%s%s", i, 2));
                                       Label nightLabel = (Label) scene.lookup(String.format("#label_%s%s", i, 3));
                                       Platform.runLater(() -> morningLabel.setText(formatTemperature(dayData, WeatherDataHandler.TimeOfDay.MORNING)));
                                       Platform.runLater(() -> dayLabel.setText(formatTemperature(dayData, WeatherDataHandler.TimeOfDay.DAY)));
                                       Platform.runLater(() -> eveningLabel.setText(formatTemperature(dayData, WeatherDataHandler.TimeOfDay.EVENING)));
                                       Platform.runLater(() -> nightLabel.setText(formatTemperature(dayData, WeatherDataHandler.TimeOfDay.NIGHT)));
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                            });
                });

    }

    public static void darkMode(Node node) {
        if (node instanceof Rectangle) {
            node.setStyle("-fx-fill: #404040; -fx-stroke: white; -fx-stroke-width: 1;");
        }
        else if (node instanceof Label ) {
            node.setStyle("-fx-text-fill: white;");
        }
        else if (node instanceof Text) {
            node.setStyle("-fx-fill: white;");
        }
        else if (node instanceof View) {
            node.setStyle("-fx-background-color: #404040;");
        }
        else if (node instanceof ImageView) {
            String id = node.getId();
            URL resource = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + id + "_dark.png");
            if (resource != null) {
                ImageView imageView = (ImageView) node;
                imageView.setImage(new Image(resource.toString()));
            }
        }
        if (node instanceof Parent) {
            for (Node children : ((Parent) node).getChildrenUnmodifiable()) {
                darkMode(children);
            }
        }
    }

    public static void lightMode(Node node) {
        if (node instanceof Rectangle) {
            node.setStyle("-fx-fill: #FFFFFF; -fx-stroke: black; -fx-stroke-width: 1;");
        }
        else if (node instanceof Label ) {
            node.setStyle("-fx-text-fill: black;");
        }
        else if (node instanceof Text) {
            node.setStyle("-fx-fill: black;");
        }
        else if (node instanceof View) {
            node.setStyle("-fx-background-color: #FFFFFF;");
        }
        else if (node instanceof ImageView) {
            String id = node.getId();
            URL resource = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + id + ".png");
            if (resource != null) {
                ImageView imageView = (ImageView) node;
                imageView.setImage(new Image(resource.toString()));
            }
        }
        if (node instanceof Parent) {
            for (Node children : ((Parent) node).getChildrenUnmodifiable()) {
                lightMode(children);
            }
        }
    }

    public static String formatTemperature(WeatherDataHandler.Data data) {
        double temperature = useLayer ? data.getTemperatureInLayers() : data.temperature();
        return useLayer ? String.format("%.2f°L", temperature) : String.format("%.2f°C", temperature);
    }

    public static String formatTemperature(WeatherDataHandler.DayData data, WeatherDataHandler.TimeOfDay timeOfDay) {
        double temperature = useLayer ? data.feelsLikeTemperatures().getTemperatureInLayers(timeOfDay) : data.temperatures().getTemperature(timeOfDay);
        return useLayer ? String.format("%.2f°L", temperature) : String.format("%.2f°C", temperature);
    }

    public static void main(String[] args) {
        String token = args[0]; //The token for the openweatherapi
        weatherDataHandler = new WeatherDataHandler(token);
        launch();
    }
}