package com.mycompany.memoria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.mycompany.memoria.GameController.cardMatching;

public class Deck {
    //deck class for memory game
    private final Card[][] matrixCards;
    private final ArrayList<Card> cards;
    private final int size;


    public Deck(int size){
        this.size = size;
        //int idx=0;
        int cardsPerRow = size/cardMatching;
        matrixCards =new Card[cardsPerRow][cardMatching];
        for(int i=0;i<cardsPerRow;i++){
            for(int j=0;j<cardMatching;j++){
                this.matrixCards[i][j]=new Card(i,"file:src/main/resources/com/mycompany/memoria/images/"+i+".png");
                //idx++; //TODO revisar si tiene algún uso. Originalmente era para llevar la cuenta de las cartas.
            }
        }
        cards = copyToArray(size);
    }

    private ArrayList<Card> copyToArray(int size) {

        ArrayList<Card> cards = new ArrayList<Card>(size);
        for(Card[] row:matrixCards) cards.addAll(Arrays.asList(row));
        return cards;
    }

    public boolean checkMatch(){
        for(Card[] row:matrixCards){
            boolean found=true;
            for(Card card:row){
                if(!card.IsFlipped()){
                    found=false;
                    break;
                }
            }
            if(found){
                for(Card card:row){
                    card.match();
                }
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
        for(Card[] row:matrixCards) Arrays.fill(row,null);
    }

    public void resetCards(){
        for(int i = 0; i < this.size; i++){
            this.cards.get(i).reset();
        }
    }

}