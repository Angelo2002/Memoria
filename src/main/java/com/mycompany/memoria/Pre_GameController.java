package com.mycompany.memoria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private void initialize() {
        cardMatching = 2;
        amountCards = 4;
        buttonSize = 100;
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
        difficultyComboBox.getItems().addAll("Fácil", "Medio", "Difícil");
        difficultyComboBox.getSelectionModel().select(0);
        Button deleteButton = new Button("Eliminar");

        // Crear un HBox para agregar el TextField, ComboBox y el botón de eliminar
        HBox hBox = new HBox(textField, difficultyComboBox, deleteButton);
        hBox.setSpacing(10);

        // Agregar el HBox en la posición adecuada
        vbox_addplayers.getChildren().add(vbox_addplayers.getChildren().size() - 2, hBox);

        // Funcionalidad del botón de eliminar
        deleteButton.setOnAction(e -> {
            vbox_addplayers.getChildren().remove(hBox);
            cpuCounter--;
            updateCpuNames();
        });
    }

    private void updateCpuNames() {

        int cpuNum = 1;
        for (Node node : vbox_addplayers.getChildren()) {
            if (node instanceof HBox) {
                HBox hBox = (HBox) node;
                if (hBox.getChildren().get(0) instanceof TextField) {
                    TextField textField = (TextField) hBox.getChildren().get(0);
                    if (textField.getText().startsWith("CPU#")) {
                        textField.setText("CPU#" + cpuNum++);
                    }
                }
            }
        }
    }

    @FXML
    private void showGame(ActionEvent actionEvent) {
        try {
            // Load the Game.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent root = loader.load();

            GameController game_controller = loader.getController();
            game_controller.setPlayers(collectPlayersData());
            // Create a new scene with the loaded FXML file
            Scene gameScene = new Scene(root, 1366, 700);

            // Get the current stage from the action event
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Set the new scene and show the stage
            currentStage.setScene(gameScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    ComboBox<String> difficultyComboBox = (ComboBox<String>) hBox.getChildren().get(1);
                    String difficulty = difficultyComboBox.getValue();
                    double accuracy;
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
                        default:
                            accuracy = 0.5;
                    }
                    players.add(new BotPlayer(playerName, accuracy));
                }
            }
        }
        return players;
    }
}

