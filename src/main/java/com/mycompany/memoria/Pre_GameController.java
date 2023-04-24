package com.mycompany.memoria;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.mycompany.memoria.GameController.*;

public class Pre_GameController {
    @FXML
    AnchorPane anchor_game;

    @FXML
    private TextField textfield_amountCards;
    @FXML
    private TextField txtfield_cardMatching;

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
    private void showGame(ActionEvent actionEvent) {
        try {
            // Load the Game.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent root = loader.load();
            // Create a new scene with the loaded FXML file
            Scene gameScene = new Scene(root);
            // Get the current stage from the action event
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Set the new scene and show the stage
            currentStage.setScene(gameScene);
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
