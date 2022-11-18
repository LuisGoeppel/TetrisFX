module com.example.tetrisfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens Controller to javafx.fxml;
    exports Controller;
}