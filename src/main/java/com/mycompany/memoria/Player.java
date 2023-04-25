package com.mycompany.memoria;

import java.util.ArrayList;

public class Player {
    private String name;
    private int score;


    public Player(String name) {
        this.name = name;
        this.score = 0;

    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int value) {
        this.score += value;
    }

    public void punish(int value) {
        this.score -= value;
    }

    public void resetScore() {
        this.score = 0;
    }

    public String toString() {
        return this.name + ",Score:" + this.score;
    }


}
