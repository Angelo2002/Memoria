package com.mycompany.memoria;

import javafx.scene.image.ImageView;


import java.io.File;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.awt.RenderingHints;

public class Card {
    //card class for memory game
    private final int cardID;
    private ImageView image;
    private boolean isFlipped;
    private boolean isMatched;

    public Card(int cardID){
        this.cardID = cardID;
        this.isFlipped = false;
        this.isMatched = false;
    }

    public boolean setImage(String image){
        try{
            File file = new File(image);
            if (!file.exists()){
                System.out.println("Image not found");
                return false;
            }
            this.image = new ImageView(file.toURI().toString());

            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    //chatGPT realizó esta parte, quería imagenes infinitas y me costó encontrar una forma por mi mismo.
    public boolean generateImage(int n) {
        try {
            String number = Integer.toString(n);
            // Create a canvas
            int width = 100;
            int height = 150;
            Canvas canvas = new Canvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Draw the background
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, width, height);

            // Draw the number
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial", 48));

            gc.fillText(number, width / 4, height / 2 + 16);

            // Convert Canvas to Image
            Image fxImage = canvas.snapshot(null, null);

            // Set the image in the ImageView
            image = new ImageView();
            image.setImage(fxImage);
            return true;
        } catch (Exception e) {
            return false;
        }
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

    public ImageView getImage(){
        if (isMatched){
            return null; //matched
        }
        if (isFlipped){
            System.out.println("Image returned");
            return this.image;
        }
        else {
            return null; //hidden
        }

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
