package com.mycompany.memoria;

import java.util.ArrayList;

public class BotPlayer extends Player{
    private ArrayList<Card> seenCards;
    private double accuracy;

    public BotPlayer(String name, double accuracy) {
        super(name);
        this.accuracy = accuracy;
        this.seenCards = new ArrayList<Card>();
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

    //TODO repartir en varias funciones
    public ArrayList<Card> chooseCards(int quantity, ArrayList<Card> otherCards) {
        removeMatchedCards();
        ArrayList<Card> chosenCards = new ArrayList<>();

        // Agrupa las cartas por ID en un array de arrays.
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

        //  Función sort para ordenar los grupos de cartas por tamaño
        cardGroups.sort((a, b) -> b.size() - a.size()); //funcion lambda para Comparator

        // Escoge cartas según su precision.
        boolean full=false;
        for (ArrayList<Card> cards : cardGroups) {
            for (Card card : cards) {
                if (Math.random() < accuracy) {
                    chosenCards.add(card);
                }
                if (chosenCards.size() >= quantity) {
                    full=true;
                    break;
                }
            }
        }
        //limpia las cartas si no hacen match cuando tiene precision alta
        if(full && accuracy>=0.5){
            boolean match=true;
            for(int i=0;i<chosenCards.size()-1;i++){
                if(chosenCards.get(i).getCardID()!=chosenCards.get(i+1).getCardID()){
                    match=false;
                    break;
                }
            }
            if(!match){
                chosenCards.clear(); //TODO considerar dejar algunas cartas que coincidan con una precision alta
            }
        }

        // Si no se tienen suficiente cartas en chosenCards, escoge cartas aleatorias no vistas.
        while (chosenCards.size() < quantity) {
            Card randomCard = chooseRandomCard(otherCards);
            if (randomCard == null) {
                throw new IllegalArgumentException("No hay suficientes cartas disponibles");
            }
            if (!chosenCards.contains(randomCard)) { //se asegura que no se repitan cartas
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

    @Override
    public boolean isBot(){
        return true;
    }
}

