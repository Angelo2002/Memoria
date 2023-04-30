package com.mycompany.memoria;

import java.util.ArrayList;

public class BotPlayer extends Player{
    private ArrayList<Card> cardsChosen;
    private ArrayList<Card> seenCards;
    private double accuracy;

    public BotPlayer(String name, double accuracy) {
        super(name);
        this.accuracy = accuracy;
        cardsChosen = new ArrayList<>();
        seenCards = new ArrayList<>();
    }


    public void resetSeenCards(){
        seenCards = new ArrayList<Card>();
    }

    public void removeMatchedCards(){
        seenCards.removeIf(Card::IsMatched);
    }

    public void addSeenCard(Card card){
        if(!seenCards.contains(card)){
            System.out.println("Card added to seen cards" + card.getCardID());
            System.out.println("List so far" + seenCards.toString());
            seenCards.add(card);
        }
    }

    //TODO repartir en varias funciones
    public ArrayList<Card> chooseCards(int quantity, ArrayList<Card> otherCards) {
        removeMatchedCards();
        ArrayList<Card> chosenCards = new ArrayList<>();

        ArrayList<ArrayList<Card>> cardGroups = getCardArraysbySize();

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

    private ArrayList<ArrayList<Card>> getCardArraysbySize() {
        // Agrupa las cartas por ID en un array de arrays.
        ArrayList<ArrayList<Card>> cardGroups = new ArrayList<>();
        for (Card card : seenCards) {
            if ( ! card.IsFlipped() ) {
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
        return cardGroups;
    }

    public Card chooseCardAlgorithm(int cardMatching, ArrayList<Card> otherCards){
        removeMatchedCards();
        ArrayList<ArrayList<Card>> cardGroups = getCardArraysbySize();
        int cardsChosenQuantity = cardsChosen.size();
        Card card;
        for (ArrayList<Card> cardGroup : cardGroups) {
            //si no hay suficientes cartas vistas para completar el grupo sale del loop
            if(cardGroup.size()<cardMatching-cardsChosenQuantity){
                break;
            }
            if (utils.cardIDBelongsToCardArray(cardGroup.get(0).getCardID(), cardsChosen)){
                if (cardGroup.size() == cardMatching - cardsChosenQuantity) { //para saber si puede completar el grupo
                    for(Card cardOption: cardGroup){
                        if(!cardsChosen.contains(cardOption)){
                            cardsChosen.add(cardOption);
                            return cardOption;
                        }
                    }
                }break; //no hay suficientes cartas para completar el grupo
            }

        }
        //si no hay cartas que coincidan con el algoritmo, escoge una carta aleatoria
        ArrayList<Card> chooseFrom = new ArrayList<>(otherCards);
        if(accuracy>=0.5 && cardsChosenQuantity==0){
            chooseFrom.removeAll(seenCards);
            chooseRandomCard(otherCards);
        }
        card = chooseRandomCard(chooseFrom);
        cardsChosen.add(card);
        return card;
    }

    /*
     Simula el olvidar cartas de los humanos con un valor forgetChance que se reduce cada vez que se olvida una carta
     y toma en cuenta la precision del bot.
     */
    public void randomCardForget(){
        float forgetChance=0.8f;
        while(!seenCards.isEmpty() && Math.random()>(accuracy+0.1) && forgetChance>Math.random()){

            seenCards.remove((int)(Math.random()*seenCards.size()));
            System.out.println("Card forgotten");
            System.out.println("List so far" + seenCards.toString());
            forgetChance/=2;
        }
    }


    public Card chooseRandomCard(ArrayList<Card> cards) { //TODO make a deck parameter to get available cards
        ArrayList<Card> availableCards = new ArrayList<>();
        for (Card card : cards) {
            if (!card.IsFlipped() && !card.IsMatched()) {
                availableCards.add(card);
            }
        }
        if (availableCards.size() == 0) {
            return null;
        }
        int index = (int) (Math.random() * availableCards.size());
        return availableCards.get(index);
    }

    public void resetBotTurn(){
        cardsChosen = new ArrayList<>();
    }

    @Override
    public boolean isBot(){
        return true;
    }
}

