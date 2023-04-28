package com.mycompany.memoria;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;

public class utils {
    static final String path = "/images/";
    static final String extension = ".png";
    static final double cardRatio = 1.5; //Las cartas son cardRatio veces mas altas que anchas.

    //TODO cambiar a void
    public static boolean setImage(int imageNumber, Card card) {
        File file = retrieveImage(path+imageNumber+extension);
        if (file.exists()) {
            System.out.println("Image found");
            ImageView image = new ImageView(new Image(file.toURI().toString()));
            card.setFrontImage(image);
            return true;
        } else {
            System.out.println("Image not found, generating image");
            card.setFrontImage(generateImage(imageNumber));
            return false;
        }

    }

    public static File retrieveImage(String imageString) {
        return new File(imageString);
    }

    public static ImageView generateImage(int n) {
        String number = Integer.toString(n);
        // Create a canvas
        int width = 90;
        int height = (int) (width*cardRatio);
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
        ImageView image = new javafx.scene.image.ImageView();
        image.setImage(fxImage);
        return image;
    }
}
