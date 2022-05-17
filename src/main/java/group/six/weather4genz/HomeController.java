package group.six.weather4genz;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class HomeController {

    @FXML
    public ScrollPane homeScroll;

    @FXML
    public AnchorPane homeAnchorPane;
    public Node forecastView;

    public static final double FOOTER_INIT_HEIGHT = 100;
    public static final double SCROLL_TOP_OFFSET = 60;
    public AnchorPane root;

    @FXML
    public void initialize() {

        System.out.println("initislized");

        homeAnchorPane.prefWidthProperty().bind(homeScroll.widthProperty());
        AnchorPane.setTopAnchor(homeScroll, SCROLL_TOP_OFFSET);

        // positioning to the bottom

        homeScroll.heightProperty().addListener((observableValue, number, t1) -> {
            AnchorPane.setTopAnchor(forecastView, root.getHeight() - FOOTER_INIT_HEIGHT - SCROLL_TOP_OFFSET);
        });
        //AnchorPane.setTopAnchor(helloBtn, /*homeScroll.getHeight()*/700 - FOOTER_INIT_HEIGHT);


    }
}
