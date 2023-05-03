package com.mycompany.memoria;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerScoreLog {
    private SimpleStringProperty playerName;
    private SimpleFloatProperty totalScore;




    public PlayerScoreLog(String name){
        this.playerName = new SimpleStringProperty(name);
        this.totalScore = new SimpleFloatProperty(0);

    }

    public PlayerScoreLog(String name, float score){
        this.playerName = new SimpleStringProperty(name);
        this.totalScore = new SimpleFloatProperty(score);

    }





    public void addScore(float score){
        this.totalScore.set(this.totalScore.get() + score);
    }

    public String toString(){
        return this.playerName + "," + this.totalScore;
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public SimpleStringProperty playerNameProperty() {
        return playerName;
    }

    public float getTotalScore() {
        return totalScore.get();
    }

    public SimpleFloatProperty totalScoreProperty() {
        return totalScore;
    }
}
