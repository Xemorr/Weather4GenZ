package group.six.weather4genz;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.scene.shape.Rectangle;
public class SettingsController {

    //switch states
    private boolean isDarkModeOn = false;
    private boolean isLayerModeOn = false;

    //switches
    @FXML
    private ImageView darkModeSwitch;
    @FXML
    private ImageView layerModeSwitch;

    //all other GUI features to switch themes
    @FXML
    private View settingsBackground;
    @FXML
    private ImageView crossIcon;
    @FXML
    private Rectangle Rectangle1;
    @FXML
    private Rectangle Rectangle2;
    @FXML
    private Rectangle Rectangle3;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;



    @FXML
    protected void darkModeSwitchClick(){
        isDarkModeOn = !isDarkModeOn;
        if (isDarkModeOn) {
            String onSwitchImagePath = HelloApplication.class.getResource("/group/six/weather4genz/icons/ToggleSwitchOn.png").toString();
            darkModeSwitch.setImage(new Image(onSwitchImagePath));
            setDarkMode();
        }else{
            String onSwitchImagePath = HelloApplication.class.getResource("/group/six/weather4genz/icons/ToggleSwitchOff.png").toString();
            darkModeSwitch.setImage(new Image(onSwitchImagePath));
            setLightMode();
        }
    }
    @FXML
    protected void layerModeSwitchClick(){
        isLayerModeOn = !isLayerModeOn;
        if (isLayerModeOn) {
            String onSwitchImagePath = HelloApplication.class.getResource("/group/six/weather4genz/icons/ToggleSwitchOn.png").toString();
            layerModeSwitch.setImage(new Image(onSwitchImagePath));

        }else{
            String onSwitchImagePath = HelloApplication.class.getResource("/group/six/weather4genz/icons/ToggleSwitchOff.png").toString();
            layerModeSwitch.setImage(new Image(onSwitchImagePath));

        }
    }

    @FXML
    protected void crossIconClick(){
        //TODO change back to main screen
    }
    private void setDarkMode(){
        settingsBackground.setStyle("-fx-background-color: #404040;");
        String crossIconDarkModePath= HelloApplication.class.getResource("/group/six/weather4genz/icons/CrossIconDarkMode.png").toString();
        crossIcon.setImage(new Image(crossIconDarkModePath));
        Rectangle1.setStyle("-fx-fill: #404040; -fx-stroke: white; -fx-stroke-width: 1;");
        Rectangle2.setStyle("-fx-fill: #404040; -fx-stroke: white; -fx-stroke-width: 1;");
        Rectangle3.setStyle("-fx-fill: #404040; -fx-stroke: white; -fx-stroke-width: 1;");
        label1.setStyle("-fx-text-fill: white;");
        label2.setStyle("-fx-text-fill: white;");
        label3.setStyle("-fx-text-fill: white;");
    }
    private void setLightMode(){
        settingsBackground.setStyle("-fx-background-color: #FFFFFF;");
        String crossIconLightModePath= HelloApplication.class.getResource("/group/six/weather4genz/icons/CrossIconLightMode.png").toString();
        crossIcon.setImage(new Image(crossIconLightModePath));
        Rectangle1.setStyle("-fx-fill: #FFFFFF; -fx-stroke: black; -fx-stroke-width: 1;");
        Rectangle2.setStyle("-fx-fill: #FFFFFF; -fx-stroke: black; -fx-stroke-width: 1;");
        Rectangle3.setStyle("-fx-fill: #FFFFFF; -fx-stroke: black; -fx-stroke-width: 1;");
        label1.setStyle("-fx-text-fill: black;");
        label2.setStyle("-fx-text-fill: black;");
        label3.setStyle("-fx-text-fill: black;");
    }

}
