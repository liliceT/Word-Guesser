package application.wordle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage stage;

    public static int længdeAfOrd;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Home.fxml")));
        Main.stage = stage;
        stage.setResizable(false);
        stage.setTitle("Word Guesser");
        stage.setResizable(false);
        stage.setScene(new Scene(root));

        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}