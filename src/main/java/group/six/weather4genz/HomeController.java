package group.six.weather4genz;

import com.gluonhq.charm.glisten.control.TextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class HomeController {
    @FXML
	public ImageView cross_icon;
	@FXML
    private Label welcomeText;

    @FXML
    private ImageView hamburger_icon;

    @FXML
    private TextField search_input;

    @FXML
    public ScrollPane homeScroll;


    @FXML
    public AnchorPane homeAnchorPane;
    @FXML AnchorPane settingsAnchorPane;
    @FXML
    public Node forecastView;
    @FXML
    public Node hourlyView;
    @FXML
    public Node settingsView;

    public static final double FOOTER_INIT_HEIGHT = 120;
    public static final double SCROLL_TOP_OFFSET = 60;
    public AnchorPane root;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onSearch() {
        HelloApplication.mainLoop();
    }

    //static public Image hamburgerIconLight = new Image ( "/group/six/weather4genz/icons/hamburger_icon.png" );
    //static public Image crossImage = new Image ( "/group/six/weather4genz/icons/CrossIcon.png" );

    @FXML
    public void initialize() {

        System.out.println("initialised");

        homeAnchorPane.prefWidthProperty().bind(homeScroll.widthProperty());
        AnchorPane.setTopAnchor(homeScroll, SCROLL_TOP_OFFSET);

        // positioning to the bottom

        homeScroll.heightProperty().addListener((observableValue, number, t1) -> {
            AnchorPane.setTopAnchor(forecastView, root.getHeight() - SCROLL_TOP_OFFSET);
        });

        homeScroll.heightProperty().addListener((observableValue, number, t1) -> {
            AnchorPane.setTopAnchor(hourlyView, root.getHeight() - FOOTER_INIT_HEIGHT - SCROLL_TOP_OFFSET);
        });

        settingsAnchorPane.setVisible ( false );

        search_input.setOnKeyPressed ( ( KeyEvent event ) -> {
            if ( event.getCode () == KeyCode.ENTER )
                onSearch ();
        } );

        //settingsScroll.setMouseTransparent ( true );

        //AnchorPane.setTopAnchor(helloBtn, /*homeScroll.getHeight()*/700 - FOOTER_INIT_HEIGHT);
    }

    public void onHamburgerClick ()
    {
        settingsAnchorPane.setVisible ( !settingsAnchorPane.isVisible () );
        hamburger_icon.setVisible ( !hamburger_icon.isVisible () );
        cross_icon.setVisible ( !cross_icon.isVisible () );
    }

    public void onMouseEnteredFooter(){
        homeScroll.setMouseTransparent(false);
    }

    public void onMouseLeftFooter(){
        homeScroll.setMouseTransparent(true);
    }
}