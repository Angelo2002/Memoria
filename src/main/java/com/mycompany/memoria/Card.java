package com.mycompany.memoria;

public class Card {
    //card class for memory game
    private final int cardID;
    private final String image;
    private boolean isFlipped;
    private boolean isMatched;

    public Card(int cardID, String image){
        this.cardID = cardID;
        this.image = image;
        this.isFlipped = false;
        this.isMatched = false;
    }



    public void match(){
        this.isMatched = true;
    }

    public void unmatch(){
        this.isMatched = false;
    }

    public void reset(){
        this.isFlipped = false;
        this.isMatched = false;
    }

    public String toString(){
        return this.cardID + " " + this.image;
    }



    public int getCardID(){
        return this.cardID;
    }

    public String getImage(){
        if (isMatched) return "file:src/main/resources/com/mycompany/memoria/images/matched.png";
        if (isFlipped) return this.image;
        else return "file:src/main/resources/com/mycompany/memoria/images/hidden.png";

    }

    public void flip(){
        this.isFlipped = !this.isFlipped;
    }

    public boolean IsFlipped(){
        return this.isFlipped;
    }

    public boolean IsMatched(){
        return this.isMatched;
    }



    public void setIsFlipped(boolean isFlipped){
        this.isFlipped = isFlipped;
    }

    public void setIsMatched(boolean isMatched){
        this.isMatched = isMatched;
    }

}
