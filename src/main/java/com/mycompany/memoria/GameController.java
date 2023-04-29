package com.mycompany.memoria;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

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
    private boolean cardDelay = false;
    ArrayList<Button> buttonsOnTable = new ArrayList<>();
    ArrayList<HBox> playerHBoxes;
    int playerCounter = 0;
    ArrayList<Player> players;
    Deck deck;
    @FXML
    private StackPane stackpane_table;

    private GridPane gpane_table;
    @FXML
    private VBox vbox_gameinfo;

    @FXML
    public void initialize(){
        gpane_table = new GridPane();
        gpane_table.setGridLinesVisible(true);
        fillTable();
        deck = new Deck(amountCards, cardMatching);
        deck.assignRandomCardsToButtons(buttonsOnTable);
        setImagesToButtons();
        if(Ruleset.bonus){
            Ruleset.bonusValue=1;
        }
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
        this.playerHBoxes = utils.getPlayersAsHBoxes(this.players);
        players.get(0).setCurrentTurn(true);
        vbox_gameinfo.getChildren().addAll(playerHBoxes);
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
        btn.setOnMouseClicked(this::ActionFlipCard);
        buttonsOnTable.add(btn);
        hbox.getChildren().add(btn);
    }

    //onAction
    @FXML
    public void ActionFlipCard(MouseEvent mouseEvent) {

        if(cardDelay){
            return;
        }

        Button btn = (Button) mouseEvent.getSource();
        Card card = (Card) btn.getUserData();

        if (mouseEvent.getButton() == MouseButton.SECONDARY && mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            System.out.println("Right click");
            card.switchGodSight();
            if(card.getGodSight()){
                btn.setStyle("-fx-background-color: #00ff00");
            }else{
                btn.setStyle("-fx-background-color: #ffffff");
            }
            btn.setGraphic(card.getCurrentImage());
            return;
        }
        card.flip();
        btn.setGraphic(card.getCurrentImage());
        flipCounter++;
        if (flipCounter == cardMatching) {
            flipCounter = 0;
            if(deck.checkMatch()){
                matchAction();
            }else{
                failAction();
            }
            updateAllButtonGraphics(buttonsOnTable);
            }
            if (turnCounter == playerCounter) {
                turnCounter = 0;
            }
            players.get(turnCounter).setCurrentTurn(true);
        }

    private void failAction() {
        if(streakCounter == 0 && Ruleset.punishmentExists){
              players.get(turnCounter).punish(Ruleset.punishmentValue);
         }
        streakCounter = 0;
        cardDelay = true;
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> resumeActions());
        pause.play();
        players.get(turnCounter).setCurrentTurn(false);
        turnCounter++;
    }

    private void matchAction() {
        for(Button button : buttonsOnTable){
            if(((Card)button.getUserData()).IsFlipped()){
                button.setDisable(true);
            }
        }
        int bonus=streakCounter*Ruleset.bonusValue;
        players.get(turnCounter).addScore(cardMatching*Ruleset.pointsPerMatchMult + bonus);
        streakCounter++;
    }

    void resumeActions(){
            deck.unflipUnmatched();
            updateAllButtonGraphics(buttonsOnTable);
            cardDelay = false;
        }

    private boolean checkBotTurn(){
        return players.get(turnCounter).isBot();
    }

    private void botTurn() {
        BotPlayer bot = (BotPlayer) players.get(turnCounter);
        //bot.playTurn(buttonsOnTable);
    }


}
