package com.mycompany.memoria;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;

public class utils {
    static final String extension = ".png";
    static final double cardRatio = 1.5; //Las cartas son cardRatio veces mas altas que anchas.
    static final String projectImagesPath = "\\src\\main\\java\\com\\mycompany\\images";
    static final String projectResourcesPath = "\\src\\main\\resources\\com\\mycompany\\memory";
    //TODO cambiar a void
    public static boolean setImage(int imageNumber, Card card) {
        File file = retrieveImage(projectImagesPath+imageNumber+extension);
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

    public static void updateButtonGraphics(Button button){
        Card card = (Card) button.getUserData();
        button.setGraphic(card.getCurrentImage());
        button.getStyleClass().add(card.getGodSight() ? "card-button-god-sight" : "card-button");
        button.getStyleClass().remove(card.getGodSight() ? "card-button": "card-button-god-sight");
    }

    public static void updateAllButtonGraphics(ArrayList<Button> buttons){
        for(Button button:buttons){
            updateButtonGraphics(button);
        }
    }

    public static ArrayList<HBox> getPlayersAsHBoxes(ArrayList<Player> players){
        ArrayList<HBox> hBoxes = new ArrayList<>();
        for(Player player:players){
            hBoxes.add(player.playerToHBox());
        }
        return hBoxes;
    }

    public static boolean playerArrayHasDuplicates(ArrayList<Player> players){
        if (players.size() == 0) return false;
        for(int i = 0; i < players.size(); i++){
            for(int j = i+1; j < players.size(); j++){
                if(players.get(i).getName().equals(players.get(j).getName())) return true;
            }
        }
        return false;
    }

    public static boolean playerArrayHasEmptyNames(ArrayList<Player> players){
        if (players.size() == 0) return false;
        for(Player player:players){
            if(player.getName().equals("")) return true;
        }
        return false;
    }

    static boolean cardArrayIsHomogenous(ArrayList<Card> cards){
        if (cards.size() == 0) return true;
        int firstCard = cards.get(0).getCardID();
        for(Card card:cards){
            if(card.getCardID() != firstCard) return false;
        }
        return true;
    }

    static boolean cardIDBelongsToCardArray(int ID, ArrayList<Card> cards){
        if (cards.size()==0) return true;
        for(Card card1:cards){
            if(card1.getCardID() != ID) return false;
        }
        return true;
    }

    static void checkThreads(){
        // Obtener el grupo de hilos principal (main thread group)
        ThreadGroup mainThreadGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup topThreadGroup = mainThreadGroup;

        // Buscar el grupo de hilos de nivel superior
        while (topThreadGroup.getParent() != null) {
            topThreadGroup = topThreadGroup.getParent();
        }

        // Obtener una estimación del número total de hilos activos en la aplicación
        int estimatedThreadCount = topThreadGroup.activeCount();

        // Crear un arreglo de hilos lo suficientemente grande como para contener todos los hilos activos
        Thread[] threads = new Thread[estimatedThreadCount];

        // Llenar el arreglo con todos los hilos activos en la aplicación
        int threadCount = topThreadGroup.enumerate(threads);

        // Recorrer el arreglo e imprimir información sobre cada hilo
        for (int i = 0; i < threadCount; i++) {
            Thread thread = threads[i];
            System.out.println("Hilo " + (i + 1) + ": " + thread.getName() + " (ID: " + thread.getId() + ", Estado: " + thread.getState() + ")");
        }

    }

}
