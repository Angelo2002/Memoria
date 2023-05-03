package com.mycompany.memoria;

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
    private boolean warning_tooManyCards_Shown;
    private int cpuCounter=0;
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
    @FXML
    private Button btn_about;


    public Pre_GameController() {
        System.out.println("Pre_GameController created");
    }



    @FXML
    private void initialize() {

        cardMatching = 2;
        amountCards = 6;
        buttonSize = 100;
        warning_tooManyCards_Shown = false;
        col_playerName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        col_playerScore.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        System.out.println("Added or not: " + tbl_globalscores.getStyleClass().add("tableview-scores"));

        spin_seconds.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spin_minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Ruleset.maxMinutes, 0));

        //run later
        updateCheckBoxfromRuleset();
       updateScoreTable();
    }

    private void updateCheckBoxfromRuleset() {
        chkb_godMode.setSelected(Ruleset.godMode);
        chkb_bonus.setSelected(Ruleset.bonus);
        chkb_punishment.setSelected(Ruleset.punishmentExists);
        chkb_shuffleMidGame.setSelected(Ruleset.shuffleMidGame);
        chkb_timedTurn.setSelected(Ruleset.timePerTurnOn);
        chkb_timeLimitOn.setSelected(Ruleset.timeLimitOn);
    }

    @FXML
    private void incrementCardMatching(ActionEvent actionEvent) {
        int prev = Integer.parseInt(txtfield_cardMatching.getText());
        cardMatching = prev+1;
        adjustCards();
        txtfield_cardMatching.setText(String.valueOf(cardMatching));

    }

    private void adjustCards() { //Ajusta las cartas para que sean multiplos de cardMatching
        if (amountCards< cardMatching)  amountCards = cardMatching;
        if (amountCards% cardMatching != 0) amountCards = amountCards + (cardMatching - (amountCards% cardMatching));
        textfield_amountCards.setText(String.valueOf(amountCards));
        if(!warning_tooManyCards_Shown && amountCards >= 18){
            warning_tooManyCards_Shown = true;
            warning_TooManyCards();
        }
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
        if(!warning_tooManyCards_Shown && amountCards >= 18){
            warning_tooManyCards_Shown = true;
            warning_TooManyCards();
        }
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
            ArrayList<Player> players = collectPlayersData();
            if(players.size() == 0)
            {
                alert_noPlayers();
                return;
            }
            if(utils.playerArrayHasEmptyNames(players))
            {
                alert_playersWithoutName();
                return;
            }

            if(utils.playerArrayHasDuplicates(players))
            {
                alert_repeatedPlayers();
                return;
            }


            fetchRules();
            if(Ruleset.shuffleMidGame && !Ruleset.timeLimitOn)
            {
                alert_shuffleMidGameNoTimer();
                return;
            }
            if(Ruleset.timeLimitOn && Ruleset.matchtime<20){
                alert_timeLimitTooShort();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent root = loader.load();

            GameController game_controller = loader.getController();

            game_controller.setPlayers(players);
            game_controller.setPlayerCounter();


            Scene gameScene = new Scene(root, 1366, 700);

            boolean added=gameScene.getStylesheets().add("stylesheetCardGame.css");
            System.out.println("Stylesheet added: " + added); //Para ver si se añadió el stylesheet

            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Set the new scene and show the stage
            currentStage.setScene(gameScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void warning_TooManyCards(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText("Demasiadas cartas");
        alert.setContentText("Añadir más cartas puede romper el tablero de juego");
        alert.showAndWait();
        return;
    }

    private void alert_timeLimitTooShort() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No se puede activar Tiempo Partida");
        alert.setContentText("Se requiere un tiempo mínimo de 20 segundos para jugar");
        alert.showAndWait();
        return;
    }

    private void alert_shuffleMidGameNoTimer() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No hay temporizador");
        alert.setContentText("No se puede barajar el tablero a la mitad del juego si no hay un temporizador");
        alert.showAndWait();
        return;
    }

    private void alert_repeatedPlayers() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Jugadores repetidos");
        alert.setContentText("No puede haber jugadores con el mismo nombre");
        alert.showAndWait();
        return;
    }

    private void alert_playersWithoutName() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Jugadores sin nombre");
        alert.setContentText("Todos los jugadores deben tener un nombre");
        alert.showAndWait();
        return;
    }

    private void alert_noPlayers() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("No hay jugadores");
        alert.setContentText("Debe haber al menos un jugador para iniciar el juego");
        alert.showAndWait();
        return;
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

        tbl_globalscores.setItems(FXCollections.observableArrayList(scores));
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

    @FXML
    private void about(ActionEvent actionEvent) {

        Pane aboutPane = new Pane();
        aboutPane.setPrefSize(400, 300);
        Label aboutLabel = new Label("Juego de Cartas\n"+
                "Versión 1.0\n" +
                "Desarrollado por:\n" +
                "Angelo José Marín Granados\n" +
                "Programación II\n"+
                "Ingeniería en Computación\n" +
                "Universidad Nacional de Costa Rica\n" +
                "Primer Ciclo 2023"
                );
        aboutLabel.setLayoutX(50);
        aboutLabel.setLayoutY(50);
        aboutPane.getChildren().add(aboutLabel);
        Scene aboutScene = new Scene(aboutPane);
        Stage aboutStage = new Stage();
        aboutStage.setScene(aboutScene);
        aboutStage.show();
    }


}

