package group.six.weather4genz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {

    public static WeatherDataHandler weatherDataHandler; //object that handles API calls
    private Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage.fxml"));
        scene = new Scene(fxmlLoader.load(), 607, 800);
        stage.setTitle("Weather4GenZ");
        stage.setScene(scene);
        stage.show();
        Thread thread = new Thread(this::mainLoop);
        thread.start();
    }

    public void mainLoop() {
        weatherDataHandler.locationfromCity("London")
                .thenAccept((location) -> weatherDataHandler.getCurrentWeatherData(location)
                        .thenAccept((data) -> {
                            ImageView imageView = (ImageView) scene.lookup("#forecast_4_icon");
                            try {
                                String str = HelloApplication.class.getResource("/group/six/weather4genz/icons/" + data.icon() + ".png").toString();
                                imageView.setImage(new Image(str));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }));
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String token = args[0]; //The token for the openweatherapi
        weatherDataHandler = new WeatherDataHandler(token);
        launch();
    }
}