package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField search_input;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSearch() {
        HelloApplication.mainLoop();
    }

    @FXML
    protected void onHamburgerClick() {
        
    }
}