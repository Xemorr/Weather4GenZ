package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class HomeController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField search_input;

    @FXML
    public ScrollPane homeScroll;

    @FXML
    public AnchorPane homeAnchorPane;

    public Node forecastView;

    public static final double FOOTER_INIT_HEIGHT = 120;
    public static final double SCROLL_TOP_OFFSET = 120;
    public AnchorPane root;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSearch() {
        HelloApplication.mainLoop();
    }

    @FXML
    public void initialize() {

        System.out.println("initialised");

        homeAnchorPane.prefWidthProperty().bind(homeScroll.widthProperty());
        AnchorPane.setTopAnchor(homeScroll, SCROLL_TOP_OFFSET);

        // positioning to the bottom

        homeScroll.heightProperty().addListener((observableValue, number, t1) -> {
            AnchorPane.setTopAnchor(forecastView, root.getHeight() - FOOTER_INIT_HEIGHT - SCROLL_TOP_OFFSET);
        });


        //AnchorPane.setTopAnchor(helloBtn, /*homeScroll.getHeight()*/700 - FOOTER_INIT_HEIGHT);
    }

    public void onMouseEnteredFooter(){
        homeScroll.setMouseTransparent(false);
    }

    public void onMouseLeftFooter(){
        homeScroll.setMouseTransparent(true);
    }
}