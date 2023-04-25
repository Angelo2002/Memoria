package com.mycompany.memoria;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class GameController {

    static int cardMatching;
    static int amountCards;
    //static int rows;
    //static int columns;
    static int buttonSize;
    int flipCounter = 0;

    ArrayList<Player> players;
    Deck deck;
    @FXML
    private StackPane stackpane_table;

    private GridPane gpane_table;

    @FXML
    public void initialize(){
        gpane_table = new GridPane();
        gpane_table.setGridLinesVisible(true);
        fillTable();
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }



    @FXML
    public void fillTable(){
        System.out.println("HelloController.initialize()");
        gpane_table = new GridPane();
        gpane_table.setAlignment(Pos.CENTER); // Alinea el contenido del GridPane en el centro
        double buttonWidth = 100; // or any other desired value


        //1.5 more columns than rows
        int rows = (int) Math.ceil(Math.sqrt(amountCards / 1.5));
        int columns = (int) Math.ceil((rows * 1.5));

        //StackPane stackPane = new StackPane();
        //stackPane.setAlignment(Pos.CENTER); // Set the alignment of the GridPane within the StackPane to center
        //VBox.setVgrow(stackPane, Priority.ALWAYS);
        //stackPane.getChildren().add(gp);
        VBox.setVgrow(gpane_table, Priority.ALWAYS);

        addButtonsToGridPane(gpane_table, rows, columns, amountCards, buttonWidth);

        System.out.println("total: " + amountCards);

        //vbox.getChildren().add(stackPane);
        stackpane_table.getChildren().add(gpane_table);
    }
    //TODO on action
    //TODO Deck
    //etc
    private void addButtonsToGridPane(GridPane gp, int rows, int columns, int amount, double buttonWidth) {
        int total = 0;
        for (int i = 0; i < rows && total < amount; i++) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            gp.add(hbox, 0, i);
            for (int j = 0; j < columns && total < amount; j++) {
                total++;
                Button btn = new Button("r" + i + "c" + j + "p");
                btn.setPrefWidth(buttonWidth);
                btn.setPrefHeight(buttonWidth * 1.5);
                hbox.getChildren().add(btn);
            }
        }
        System.out.println("total2: " + total);
    }

}
