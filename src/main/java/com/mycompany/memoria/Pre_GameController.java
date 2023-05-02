package com.mycompany.memoria;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.mycompany.memoria.GameController.*;

public class Pre_GameController {
    @FXML
    AnchorPane anchor_game;

    @FXML
    private TextField textfield_amountCards;
    @FXML
    private TextField txtfield_cardMatching;
    @FXML
    private Button btn_play;
    @FXML
    private Button btn_addBot;
    @FXML
    private Button btn_addPlayer;
    @FXML
    private VBox vbox_addplayers;

    int cpuCounter=0;
    @FXML
    private CheckBox chkb_godMode;
    @FXML
    private CheckBox chkb_bonus;
    @FXML
    private CheckBox chkb_punishment;
    @FXML
    private TableColumn col_playerName;
    @FXML
    private TableColumn col_playerScore;
    @FXML
    private TableView tbl_globalscores;


    static ArrayList<PlayerScoreLog> scores = new ArrayList<>();
    @FXML
    private CheckBox chkb_shuffleMidGame;
    @FXML
    private CheckBox chkb_timedTurn;
    @FXML
    private CheckBox chkb_timeLimitOn;
    @FXML
    private Spinner spin_seconds;
    @FXML
    private Spinner spin_minutes;


    public Pre_GameController() {
        System.out.println("Pre_GameController created");
    }



    @FXML
    private void initialize() {

        cardMatching = 2;
        amountCards = 4;
        buttonSize = 100;

        col_playerName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        col_playerScore.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        col_playerName.setCellFactory(column -> new TableCell<PlayerScoreLog, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    System.out.println("Null");
                } else {
                    setText(item);
                    System.out.println("Nombre del jugador: " + item);
                }
            }
        });

        spin_seconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spin_minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Ruleset.maxMinutes, 0));

        /*
        col_playerScore.setCellFactory(column -> new TableCell<PlayerScoreLog, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    System.out.println("Null");
                } else {
                    setText(Float.toString(item));
                    System.out.println("Puntaje: " + item);
                }
            }
        });
         */

        //run later
        Platform.runLater(this::updateScoreTable);
    }

    @FXML
    private void incrementCardMatching(ActionEvent actionEvent) {
        int prev = Integer.parseInt(txtfield_cardMatching.getText());
        cardMatching = prev+1;

        adjustCards();
        txtfield_cardMatching.setText(String.valueOf(cardMatching));
    }

    private void adjustCards() { //Ajusta las cartas para que sean multiplos de cardMatching
        if (amountCards< cardMatching)
            amountCards = cardMatching;
        if (amountCards% cardMatching != 0)
            amountCards = amountCards + (cardMatching - (amountCards% cardMatching));
            textfield_amountCards.setText(String.valueOf(amountCards));
    }

    @FXML
    private void decrementCardMatching(ActionEvent actionEvent) {
        int prev = Integer.parseInt(txtfield_cardMatching.getText());
        if (prev > 2) {
            cardMatching = prev-1;
            adjustCards();
            txtfield_cardMatching.setText(String.valueOf(cardMatching));
        }
    }


    @FXML
    private void incrementCards(ActionEvent actionEvent) {
        int prev = Integer.parseInt(textfield_amountCards.getText());
        amountCards = prev+ cardMatching;
        textfield_amountCards.setText(String.valueOf(amountCards));
    }

    @FXML
    private void decrementCards(ActionEvent actionEvent) {
        int prev = Integer.parseInt(textfield_amountCards.getText());
        if (prev > cardMatching) {
            amountCards = prev- cardMatching;
            textfield_amountCards.setText(String.valueOf(amountCards));
        }
    }

    @FXML
    private void add_player(ActionEvent actionEvent) {
        TextField textField = new TextField();
        Button deleteButton = new Button("Eliminar");

        // Crear un HBox para agregar el TextField y el botón de eliminar
        HBox hBox = new HBox(textField, deleteButton);
        hBox.setSpacing(10);

        // Agregar el HBox en la posición adecuada
        vbox_addplayers.getChildren().add(vbox_addplayers.getChildren().size() - 2, hBox);

        // Funcionalidad del botón de eliminar
        deleteButton.setOnAction(e -> vbox_addplayers.getChildren().remove(hBox));
    }

    @FXML
    private void add_cpu(ActionEvent actionEvent) {
        // Crear TextField, ComboBox y botón de eliminar
        TextField textField = new TextField("CPU#" + cpuCounter++);
        textField.setEditable(false);
        ComboBox<String> difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Fácil", "Medio", "Difícil","Tramposo");
        difficultyComboBox.getSelectionModel().select(0);
        Button deleteButton = new Button("Eliminar");

        // Crear un HBox para agregar el TextField, ComboBox y el botón de eliminar
        HBox hBox = new HBox(textField, deleteButton, difficultyComboBox);
        hBox.setSpacing(10);

        // Agregar el HBox en la posición adecuada
        vbox_addplayers.getChildren().add(vbox_addplayers.getChildren().size() - 2, hBox);

        // Funcionalidad del botón de eliminar
        deleteButton.setOnAction(e -> {
            vbox_addplayers.getChildren().remove(hBox);
            updateCpuNames();
        });
    }

    private void updateCpuNames() {

        cpuCounter = 0;
        for (Node node : vbox_addplayers.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                if (hBox.getChildren().get(0) instanceof TextField) {
                    TextField textField = (TextField) hBox.getChildren().get(0);
                    if (textField.getText().startsWith("CPU#")) {
                        textField.setText("CPU#" + cpuCounter++);
                    }
                }
            }
        }
    }

    @FXML
    private void showGame(ActionEvent actionEvent) {
        try {
            fetchRules();
            // Load the Game.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent root = loader.load();

            GameController game_controller = loader.getController();
            game_controller.setPlayers(collectPlayersData());
            game_controller.setPlayerCounter();
            // Create a new scene with the loaded FXML file
            Scene gameScene = new Scene(root, 1366, 700);
            File stylesheet = new File("/."+utils.projectResourcesPath+"stylesheetCardGame.css");
            boolean added=gameScene.getStylesheets().add("stylesheetCardGame.css");
            System.out.println("Stylesheet added: "+added);
            // Get the current stage from the action event
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Set the new scene and show the stage
            currentStage.setScene(gameScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchRules(){
        Ruleset.godMode = chkb_godMode.isSelected();
        Ruleset.bonus = chkb_bonus.isSelected();
        Ruleset.punishmentExists = chkb_punishment.isSelected();
        Ruleset.shuffleMidGame = chkb_shuffleMidGame.isSelected();
        Ruleset.timeLimitOn = chkb_timeLimitOn.isSelected();
        if(Ruleset.timeLimitOn) Ruleset.matchtime = fetchTimeLimit();
        Ruleset.timePerTurnOn = chkb_timedTurn.isSelected();

    }

    int fetchTimeLimit(){
        int timeLimit = Integer.parseInt(spin_minutes.getValue().toString())*60;
        timeLimit += Integer.parseInt(spin_seconds.getValue().toString());
        return timeLimit;
    }

    private ArrayList<Player> collectPlayersData() {
        ArrayList<Player> players = new ArrayList<>();
        for (Node node : vbox_addplayers.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                TextField textField = (TextField) hBox.getChildren().get(0);
                String playerName = textField.getText();
                boolean isHuman = textField.isEditable();

                if (isHuman) {
                    players.add(new Player(playerName));
                } else {
                    ComboBox<String> difficultyComboBox = (ComboBox<String>) hBox.getChildren().get(2);
                    String difficulty = difficultyComboBox.getValue();
                    double accuracy; boolean cheater=false;
                    switch (difficulty) {
                        case "Fácil":
                            accuracy = 0.3;
                            break;
                        case "Medio":
                            accuracy = 0.5;
                            break;
                        case "Difícil":
                            accuracy = 0.9;
                            break;
                        case "Tramposo":
                            accuracy = 1;
                            cheater = true;
                            break;
                        default:
                            accuracy = 0.5;
                    }
                    players.add(new BotPlayer(playerName, accuracy,cheater));
                }
            }
        }
        return players;
    }

    public void updateScoreTable(){

        //col_playerName.setSortType(TableColumn.SortType.ASCENDING);
        //col_playerScore.setSortType(TableColumn.SortType.ASCENDING);
        tbl_globalscores.refresh();
        tbl_globalscores.setItems(FXCollections.observableArrayList(scores));
        //System.out.println("Col Name"+col_playerName.getCellObservableValue(0).getValue());
        System.out.println("Scores updated" + tbl_globalscores.getItems().size());

    }

    public static void updateScores(ArrayList<Player> players) {
        for (Player player : players) {
            PlayerScoreLog existingScore = findScoreByPlayerName(player.getName());
            if (player.isBot()) continue;
            if (existingScore != null) {
                System.out.println("Existing score found for " + player.getName() + " adding " + player.getScore() + " points");
                existingScore.addScore(player.getScore());
            } else {
                System.out.println("No existing score found for " + player.getName() + " adding " + player.getScore() + " points");
                scores.add(new PlayerScoreLog(player.getName(), player.getScore()));
            }
        }
        //Platform.runLater(this::updateScoreTable);
    }

    private static PlayerScoreLog findScoreByPlayerName(String playerName) {
        for (PlayerScoreLog score : scores) {
            System.out.println("Comparing " + score.getPlayerName() + " with " + playerName);
            if (score.getPlayerName().equals(playerName)) {
                return score;
            }
        }
        return null;
    }

}

