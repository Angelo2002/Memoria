package com.mycompany.memoria;

import java.util.ArrayList;

public class Player {
    private String name;
    private int score;
    private ArrayList<Card> seenCards;
    private boolean bot;

    public Player(String name){
        this.name = name;
        this.score = 0;
        this.bot = false;
    }

    public Player(String name, boolean isBot){
        this.name = name;
        this.score = 0;
        this.bot = isBot;
        if(isBot){
            this.seenCards = new ArrayList<Card>();
        }
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public int getScore(){
        return this.score;
    }

    public void addScore(int value){
        this.score += value;
    }

    public void punish(int value){
        this.score -= value;
    }

    public void resetScore(){
        this.score = 0;
    }

    public String toString(){
        return this.name + ",Score:" + this.score;
    }

    public boolean isBot(){
        return this.bot;
    }

    public void addSeenCard(Card card){
        this.seenCards.add(card);
    }

    public ArrayList<Card> getSeenCards(){
        return this.seenCards;
    }

    public void resetSeenCards(){
        this.seenCards = new ArrayList<Card>();
    }

    public void removeMatchedCards(){
        seenCards.removeIf(Card::IsMatched);
    }
    public ArrayList<Card> chooseCards(int quantity, double accuracy, ArrayList<Card> otherCards) {
        removeMatchedCards();
        ArrayList<Card> chosenCards = new ArrayList<>();

        // Step 1: Group cards by cardID
        ArrayList<ArrayList<Card>> cardGroups = new ArrayList<>();
        for (Card card : seenCards) {
            if (!card.IsFlipped()) {
                boolean addedToGroup = false;
                for (ArrayList<Card> group : cardGroups) {
                    if (!group.isEmpty() && group.get(0).getCardID() == card.getCardID()) {
                        group.add(card);
                        addedToGroup = true;
                        break;
                    }
                }
                if (!addedToGroup) {
                    ArrayList<Card> newGroup = new ArrayList<>();
                    newGroup.add(card);
                    cardGroups.add(newGroup);
                }
            }
        }

        // Step 2: Sort card groups by size in descending order
        cardGroups.sort((a, b) -> b.size() - a.size()); //funcion lambda para Comparator

        // Step 3: Try to match cards based on the accuracy
        for (ArrayList<Card> cards : cardGroups) {
            for (Card card : cards) {
                if (Math.random() < accuracy) {
                    chosenCards.add(card);
                }
                if (chosenCards.size() >= quantity) {
                    return chosenCards;
                }
            }
        }

        // Step 4: If not enough cards found, randomly choose cards from otherCards
        while (chosenCards.size() < quantity) {
            Card randomCard = chooseRandomCard(otherCards);
            if (randomCard != null && !chosenCards.contains(randomCard)) {
                chosenCards.add(randomCard);
            }
        }
        return chosenCards;
    }

    public Card chooseRandomCard(ArrayList<Card> cards) {
        ArrayList<Card> availableCards = new ArrayList<>();
        for (Card card : cards) {
            if (!card.IsFlipped()) {
                availableCards.add(card);
            }
        }
        if (availableCards.size() == 0) {
            return null;
        }
        int index = (int) (Math.random() * availableCards.size());
        return availableCards.get(index);
    }

}
