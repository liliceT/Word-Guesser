module application.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens application.wordle to javafx.fxml;
    exports application.wordle;
}