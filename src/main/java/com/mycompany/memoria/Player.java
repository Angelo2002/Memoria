package com.mycompany.memoria;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Player {
    private String name;
    private FloatProperty score;
    private BooleanProperty currentTurn;


    public Player(String name) {
        this.name = name;
        this.score = new SimpleFloatProperty(0);
        this.currentTurn = new SimpleBooleanProperty(false);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public float getScore() {
        return this.score.get();
    }

    public void addScore(int value) {
        this.score.set(this.score.get() + value);
    }

    public void punish(float value) {
        this.score.set(this.score.get() - value);
    }

    public void resetScore() {
        this.score.set(0);
    }

    public String toString() {
        return this.name + ",Score:" + this.score;
    }

    public HBox playerToHBox() {
        HBox hbox = new HBox();
        hbox.setSpacing(10);

        Label playerName = new Label(name);
        Label playerScore = new Label();
        playerScore.textProperty().bind(score.asString("Score: %.2f"));

        hbox.getChildren().addAll(playerName, playerScore);
        hbox.styleProperty().bind(Bindings.when(currentTurn)
                .then("-fx-background-color: #00ff00;")
                .otherwise("-fx-background-color: transparent;"));
        return hbox;
    }

    public void setCurrentTurn(boolean currentTurn) {
        this.currentTurn.set(currentTurn);
    }

    public boolean isCurrentTurn(){
        return this.currentTurn.get();
    }

    public boolean isBot(){
        return false;
    }

}
