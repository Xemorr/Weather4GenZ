module group.six.weather4genz {
    requires javafx.controls;
    requires javafx.fxml;
    requires okio;
    requires okhttp3;
    requires kotlin.stdlib;
    requires kotlin.stdlib.common;

    requires javafx.media;
    requires com.gluonhq.charm.glisten;
    requires com.gluonhq.attach.util;
    requires annotations;
    requires json.path;
    requires org.jfxtras.styles.jmetro;

    opens group.six.weather4genz to javafx.fxml;
    exports group.six.weather4genz;
}