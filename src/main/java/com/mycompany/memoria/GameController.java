package com.mycompany.memoria;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.ArrayList;

import static com.mycompany.memoria.utils.cardRatio;
import static com.mycompany.memoria.utils.updateAllButtonGraphics;

public class GameController {

    static int cardMatching;
    static int amountCards;
    //static int rows;
    //static int columns;
    static int buttonSize;
    private int streakCounter = 0;
    int flipCounter = 0;
    int turnCounter = 0;

    ArrayList<Button> buttonsOnTable = new ArrayList<>();
    int playerCounter = 0;

    ArrayList<Player> players;
    Deck deck;
    @FXML
    private StackPane stackpane_table;

    private GridPane gpane_table;

    @FXML
    public void initialize(){
        gpane_table = new GridPane();
        gpane_table.setGridLinesVisible(true);
        fillTable();
        deck = new Deck(amountCards, cardMatching);
        deck.assignRandomCardsToButtons(buttonsOnTable);
        setImagesToButtons();
    }

    public void setPlayerCounter() {
        playerCounter = players.size();
    }

    private void setImagesToButtons() {
        for (Button button : buttonsOnTable) {
            button.setGraphic(((Card)button.getUserData()).getCurrentImage());
        }
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }


    @FXML
    public void fillTable(){
        System.out.println("HelloController.initialize()");
        gpane_table = new GridPane();
        gpane_table.setAlignment(Pos.CENTER); // Alinea el contenido del GridPane en el centro
        double buttonWidth = 100; // or any other desired value

        //Las cartas son cardRatio veces mas altas que anchas.
        int rows = (int) Math.ceil(Math.sqrt(amountCards / cardRatio));
        int columns = (int) Math.ceil((rows * cardRatio));

        VBox.setVgrow(gpane_table, Priority.ALWAYS);
        addButtonsToGridPane(gpane_table, rows, columns, amountCards, buttonWidth);
        stackpane_table.getChildren().add(gpane_table);
    }
    //TODO on action
    private void addButtonsToGridPane(GridPane gp, int rows, int columns, int amount, double buttonWidth) {
        int total = 0;
        for (int i = 0; i < rows && total < amount; i++) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            gp.add(hbox, 0, i);
            for (int j = 0; j < columns && total < amount; j++) {
                total++;
                addNewButton(buttonWidth, hbox);
            }
        }

    }

    private void addNewButton(double buttonWidth, HBox hbox) {
        Button btn = new Button();
        btn.setPrefWidth(buttonWidth);
        btn.setPrefHeight(buttonWidth * 1.5);
        btn.setOnAction(this::ActionFlipCard);
        buttonsOnTable.add(btn);
        hbox.getChildren().add(btn);
    }

    //onAction
    @FXML
    public void ActionFlipCard(javafx.event.ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        Card card = (Card) btn.getUserData();
        card.flip();
        btn.setGraphic(card.getCurrentImage());
        flipCounter++;
        if (flipCounter == cardMatching) {
            flipCounter = 0;
            if(deck.checkMatch()){
                for(Button button : buttonsOnTable){
                    if(((Card)button.getUserData()).IsFlipped()){
                        button.setDisable(true);
                    }
                }
                int bonus=streakCounter*Ruleset.bonusValue;
                players.get(turnCounter).addScore(Ruleset.pointsPerMatch + bonus);
                streakCounter++;

            }else{
               if(streakCounter == 0 && Ruleset.punishmentExists){
                     players.get(turnCounter).punish(Ruleset.punishmentValue);
                }
               deck.unflipUnmatched();
               streakCounter = 0;
               turnCounter++;
               }
            updateAllButtonGraphics(buttonsOnTable);
            }
            if (turnCounter == playerCounter) {
                turnCounter = 0;
            }

        }
}
