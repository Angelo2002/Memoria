package com.mycompany.memoria;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private final ArrayList<cardSet> cardSets;
    private final ArrayList<Card> cards;
    private final int size;


    public Deck(int size, int cardMatching){
        this.size = size;
        //int idx=0;
        int cardsPerRow = size/cardMatching;


        cardSets = new ArrayList<>();
        for(int i=0;i<cardsPerRow;i++){
            cardSets.add(new cardSet());
            for(int j=0;j<cardMatching;j++){
                Card card = new Card(i);
                utils.setImage(i,card);
                cardSets.get(i).addCard(card);
            }
        }

        cards = copyToArray(size);
    }


    private ArrayList<Card> copyToArray(int size) {
        ArrayList<Card> cards = new ArrayList<Card>(size);
        for(cardSet cardset:cardSets) cards.addAll(cardset.getCardArray());
        return cards;
    }

    public boolean testAndMatch(){
        for(cardSet cardset:cardSets){
            if(cardset.checkMatch() && !cardset.isMatched()){
                cardset.match();
                return true;
            }
        }
        return false;

    }

    public int getSize(){
        return this.size;
    }

    public void shuffleCards(){
        Collections.shuffle(this.cards);
    }

    public void eraseDeck(){
        this.cards.clear();
        this.cardSets.clear();
        //for(Card[] row:matrixCards) Arrays.fill(row,null);
    }

    public void resetCardsStatus(){
        for(int i = 0; i < this.size; i++){
            this.cards.get(i).reset();
        }
    }

    /*
    Revuelve las cartas y las guarda como metadatos en los botones
     */
    public void assignRandomCardsToButtons(ArrayList<Button> buttonArrayList){
        Collections.shuffle(cards);
        for(int i = 0; i < this.size; i++){
            buttonArrayList.get(i).setUserData(this.cards.get(i));
        }
    }

    public void unflipUnmatched() {
        for(Card card:cards){
            if(!card.IsMatched() && card.IsFlipped()) {card.flip();
            }

        }
    }

    public ArrayList<Card> getFlippedCards(){
        ArrayList<Card> flippedCards = new ArrayList<>();
        for(Card card:cards){
            if(card.IsFlipped()) flippedCards.add(card);
        }
        return flippedCards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public boolean allCardsMatched(){
        for(cardSet cardSet:cardSets){
            if(!cardSet.isMatched()) return false;
        }
        return true;
    }

    class cardSet{
        ArrayList<Card> cardArray;
        boolean matched;
        public cardSet(){
            cardArray = new ArrayList<>();
        }
        public void addCard(Card card){
            cardArray.add(card);
        }
        public boolean checkMatch(){
            for (Card card:cardArray){
                if(!card.IsFlipped()) return false;
            }
            return true;
        }
        public void match(){
            for (Card card:cardArray){
                card.match();
            }
            this.matched=true;
        }
        public boolean isMatched(){
            return this.matched;
        }
        public ArrayList<Card> getCardArray() {
            return this.cardArray;
        }
    }
}
