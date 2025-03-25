package application.wordle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Objects;

public class SkiftScene {

    public SkiftScene(String FXMLfile) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(FXMLfile)));
            Scene scene = new Scene(root);
            Main.stage.setScene(scene);
            Main.stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
