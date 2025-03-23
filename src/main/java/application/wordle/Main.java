package application.wordle;

import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage stage;
    public static Scene scene;

    public static int længdeAfOrd;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Home.fxml")));
        scene = new Scene(root);
        Main.stage = stage;
        stage.setResizable(false);
        stage.setTitle("Word Guesser");
        stage.setResizable(false);
        stage.setScene(scene);

        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}