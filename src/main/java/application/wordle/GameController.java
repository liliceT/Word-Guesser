package application.wordle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class GameController {

    public static String RigtigOrd = "";
    public JsonReader JsonReader;
    public String JsonFilePath = "";
    public List<String> OrdListe;
    public StringBuilder OrdGæt;
    public Scene GameScene;
    public SplitPane MainPane;
    public SplitPane[] WordPane;
    public Label[][] BogstavBox;
    public Pane alfabetet;
    public Text errorText;

    public int BogstavSize = 75;
    public int længdeAfOrd = Main.længdeAfOrd;
    public int MainPaneWidth = BogstavSize * længdeAfOrd;

    public int AktivLinje = 0;
    public int AktivBogstavBox = 0;

    @FXML
    public void initialize() {
        loadOrd(længdeAfOrd);

        MainPane.getItems().clear();
        BogstavBox = new Label[6][længdeAfOrd];
        WordPane = new SplitPane[6];

        MainPane.setPrefSize(MainPaneWidth, BogstavSize * 6);
        MainPane.setLayoutY(25);
        MainPane.setLayoutX((550 - MainPane.getPrefWidth()) / 2);

        for (int i = 0; i < 6; i++) {

            WordPane[i] = new SplitPane();
            WordPane[i].setPrefSize(MainPaneWidth, BogstavSize);
            WordPane[i].setMaxSize(MainPaneWidth, BogstavSize);
            WordPane[i].setMinSize(MainPaneWidth, BogstavSize);
            WordPane[i].setLayoutY(BogstavSize * i);
            WordPane[i].setLayoutX(0);

            for (int j = 0; j < længdeAfOrd; j++) {
                BogstavBox[i][j] = new Label();
                BogstavBox[i][j].setPrefSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setMaxSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setMinSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setLayoutX(BogstavSize * j);
                BogstavBox[i][j].setLayoutY(0);
                BogstavBox[i][j].setAlignment(Pos.CENTER);
                BogstavBox[i][j].setStyle("-fx-text-fill: #222222; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                WordPane[i].getItems().add(BogstavBox[i][j]);
            }
            MainPane.getItems().add(WordPane[i]);
        }

        //Fordi KeyListener ikke virker i initialize, bruges platform.runLater
        //Så den sætter keyListener på scenen, efter den er blevet oprettet
        Platform.runLater(this::setupKeyListener);
    }

    public void setupKeyListener() {
        GameScene = MainPane.getScene();
        if (GameScene != null) {
            GameScene.setOnKeyPressed(this::FysiskKeyboard);
        }
    }

    public void loadOrd(int LængdeAfOrd) {

        switch (LængdeAfOrd) {
            case 3 -> JsonFilePath = "three-letter-words.json";
            case 4 -> JsonFilePath = "four-letter-words.json";
            case 5 -> JsonFilePath = "five-letter-words.json";
            case 6 -> JsonFilePath = "six-letter-words.json";
        }
        JsonReader = new JsonReader(JsonFilePath);
        RigtigOrd = JsonReader.getRandomWord();
    }

    //Tilsluttet alle taster på det virtuelle keyboard
    //Håndtere tasterne og indsætter deres værdier i deres respektive felt
    @FXML
    public void VirtueltKeyboard(MouseEvent event) {
        String LabelText = ((Label) event.getSource()).getText().toUpperCase();

        HåntereInput(LabelText);
    }

    private void FysiskKeyboard(KeyEvent event) {
        String keyCode = event.getCode().toString().toUpperCase();

        if (keyCode.equals("BACK_SPACE")) {
            keyCode = "DELETE";
        }

        HåntereInput(keyCode);
    }

    public void HåntereInput(String keyCode) {

        if (Objects.equals(keyCode, "ENTER")) {
            if (CheckOrdErEtOrd()) {
                SammenlignOrd();
                AktivLinje++;
                AktivBogstavBox = 0;

            } else {
                visError();
                new SoundPlayer("NotAWord");
            }

        } else if (Objects.equals(keyCode, "DELETE")) {
            if (AktivBogstavBox > 0) {
                AktivBogstavBox--;
            }
            BogstavBox[AktivLinje][AktivBogstavBox].setText("");

        } else if (keyCode.length() == 1 && keyCode.matches("[A-Z]")) {
            if (AktivBogstavBox <= længdeAfOrd - 1) {
                BogstavBox[AktivLinje][AktivBogstavBox].setText(keyCode);
                AktivBogstavBox++;
            }
        }
    }

    public void visError() {

        errorText.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(_ -> errorText.setVisible(false));
        pause.play();

    }

    public boolean CheckOrdErEtOrd() {

        OrdGæt = new StringBuilder();

        for (int i = 0; i < længdeAfOrd; i++) {
            OrdGæt.append(BogstavBox[AktivLinje][i].getText());
        }

        JsonReader = new JsonReader(JsonFilePath);
        OrdListe = JsonReader.getJsonFile();

        return OrdListe.contains(OrdGæt.toString());
    }

    public void SammenlignOrd() {
        if (OrdGæt.toString().equals(RigtigOrd)) {
            new SkiftScene("Win.fxml");
            new SoundPlayer("Win");
            Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
        } else if (AktivLinje == 5) {
            new SkiftScene("Lose.fxml");
            new SoundPlayer("GameOver");
            Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
        }

        boolean[] letterUsed = new boolean[længdeAfOrd];
        String[] resultat = new String[længdeAfOrd];

        // Første loop: Find grønne (korrekte placeringer)
        for (int i = 0; i < længdeAfOrd; i++) {
            if (RigtigOrd.charAt(i) == OrdGæt.charAt(i)) {
                resultat[i] = "Grøn";
                letterUsed[i] = true; // Markér som brugt
            }
        }

        // Andet loop: Find gule (forkert placering, men findes i ordet)
        for (int i = 0; i < længdeAfOrd; i++) {
            if (resultat[i] == null) { // Hvis det ikke allerede er grøn

                for (int j = 0; j < længdeAfOrd; j++) {
                    if (OrdGæt.charAt(i) == RigtigOrd.charAt(j) && !letterUsed[j]) {
                        resultat[i] = "Gul";
                        letterUsed[j] = true; // Markér som brugt
                        break;
                    }
                }
                if (resultat[i] == null) { // Hvis bogstavet ikke blev fundet
                    resultat[i] = "Grå";
                }
            }
        }

        // Opdater UI med farver
        for (int i = 0; i < længdeAfOrd; i++) {
            switch (resultat[i]) {
                case "Grøn" -> {
                    BogstavBox[AktivLinje][i].setBackground(Background.fill(Paint.valueOf("#77dd76")));
                    BogstavBox[AktivLinje][i].setStyle("-fx-background-color: #77dd76; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                }
                case "Gul" -> {
                    BogstavBox[AktivLinje][i].setBackground(Background.fill(Paint.valueOf("#ebed74")));
                    BogstavBox[AktivLinje][i].setStyle("-fx-background-color: #ebed74; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                }
                case "Grå" -> {
                    BogstavBox[AktivLinje][i].setBackground(Background.fill(Paint.valueOf("#787c7e")));
                    BogstavBox[AktivLinje][i].setStyle("-fx-background-color: #787c7e; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                }
            }
        }

        FarveTaster();

    }

    public void FarveTaster() {
        for (int i = 0; i < længdeAfOrd; i++) {

            for (int j = 0; j < 28; j++) {
                Label label = (Label) alfabetet.getChildren().get(j);

                if (Objects.equals(label.getText(), String.valueOf(OrdGæt.charAt(i))) && Objects.equals(BogstavBox[AktivLinje][i].getBackground(), Background.fill(Paint.valueOf("#77dd76")))) {
                    label.setStyle("-fx-background-color: #77dd76; -fx-border-color: #000000;");

                } else if (Objects.equals(label.getText(), String.valueOf(OrdGæt.charAt(i))) && Objects.equals(BogstavBox[AktivLinje][i].getBackground(), Background.fill(Paint.valueOf("#ebed74")))) {
                    if (!Objects.equals(BogstavBox[AktivLinje][i].getBackground(), Background.fill(Paint.valueOf("#77dd76")))) {
                        label.setStyle("-fx-background-color: #ebed74; -fx-border-color: #000000;");
                    }

                } else if (Objects.equals(label.getText(), String.valueOf(OrdGæt.charAt(i))) && Objects.equals(BogstavBox[AktivLinje][i].getBackground(), Background.fill(Paint.valueOf("#787c7e")))) {
                    label.setStyle("-fx-background-color: #787c7e; -fx-border-color: #000000;");
                }
            }
        }
    }

    public void giveUp() {

        new SkiftScene("Lose.fxml");
        new SoundPlayer("Button");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);

    }
}