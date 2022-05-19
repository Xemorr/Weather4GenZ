package group.six.weather4genz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;

public class SliderTestController {
    private boolean isDarkModeOn = false;
    @FXML
    private Rectangle toggleButtonSwitchBackground;
    @FXML
    private Button toggleButtonSwitch;
    @FXML
    protected void sliderSwitch(){
        isDarkModeOn = !isDarkModeOn;
        if (isDarkModeOn) {
            toggleButtonSwitch.setTranslateX(24);
            toggleButtonSwitchBackground.setFill(javafx.scene.paint.Color.PINK);
        }else{
            toggleButtonSwitch.setTranslateX(0);
            toggleButtonSwitchBackground.setFill(javafx.scene.paint.Color.WHITE);
        }
    }
}
