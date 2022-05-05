module group.six.weather4genz {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens group.six.weather4genz to javafx.fxml;
    exports group.six.weather4genz;
}