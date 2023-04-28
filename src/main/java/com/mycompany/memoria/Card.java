package com.mycompany.memoria;

import javafx.scene.image.ImageView;

import java.io.File;

public class Card {
    private final int cardID;

    private boolean isFlipped;
    private boolean isMatched;
    static String MatchedPath = "\\src\\main\\java\\com\\mycompany\\images\\matched.png";
    static String HiddenPath = "\\src\\main\\java\\com\\mycompany\\images\\hidden.png";

    private ImageView matchedImage;
    private ImageView hiddenImage;
    private ImageView frontImage;
    private ImageView currentImage;
    public Card(int cardID){
        this.cardID = cardID;
        this.isFlipped = false;
        this.isMatched = false;
        //String absolutePath = new File("./").getAbsolutePath();
        File file = new File("./" + HiddenPath);
        System.out.println(file.getAbsolutePath() + " " + file.exists());
        setDefaultImages(file);
    }

    //TODO refractor images, I'm going insane
    private void setDefaultImages(File file) {
        if (file.exists()){
            hiddenImage = new ImageView(file.toURI().toString());
            hiddenImage.setFitWidth(100);
            hiddenImage.setFitHeight(150);
        }
        else hiddenImage = null;
    }

    public void setFrontImage(ImageView image){
        this.frontImage = image;
    }


    public void match(){
        this.isMatched = true;
        //this.currentImage = matchedImage;
    }

    public void unmatch(){
        this.isMatched = false;
        //this.currentImage = hiddenImage;
    }

    public void reset(){
        this.isFlipped = false;
        this.isMatched = false;
    }

    public String toString(){
        return this.cardID + " " + this.frontImage;
    }



    public int getCardID(){
        return this.cardID;
    }

    public ImageView getCurrentImage(){
        return this.currentImage;

    }

    public void flip(){
        this.isFlipped = !this.isFlipped;
        if (this.isFlipped){
            this.currentImage = this.frontImage;
        }
        else {
            this.currentImage = hiddenImage;
        }
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
