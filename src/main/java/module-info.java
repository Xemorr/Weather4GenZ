module group.six.weather4genz {
    requires javafx.controls;
    requires javafx.fxml;
    requires okio;
    requires okhttp3;
    requires kotlin.stdlib;
    requires kotlin.stdlib.common;

    requires com.gluonhq.charm.glisten;
    requires com.gluonhq.attach.util;
    requires annotations;
    requires json.path;

    opens group.six.weather4genz to javafx.fxml;
    exports group.six.weather4genz;
}