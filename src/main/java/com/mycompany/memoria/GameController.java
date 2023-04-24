package com.mycompany.memoria;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class GameController {

    static final float cardWidth = 1F;
    static final float cardRelativeHeight = 1.5F;
    static int cardMatching;
    static int amountCards;
    static int rows;
    static int columns;
    static int buttonSize;
    ArrayList<Player> players = new ArrayList<>();
    Deck deck;
    @FXML
    private Pane pane_table;

    private GridPane gpane_table;

    @FXML
    public void initialize(){
        gpane_table = new GridPane();
        gpane_table.setGridLinesVisible(true);
        makeButtons();
    }





    @FXML
    public void makeButtons(){

        gpane_table.setMinSize(600, 600);
        gpane_table.setPrefSize(600, 600);
        gpane_table.setMaxSize(600, 600);

        gpane_table.setGridLinesVisible(true);

        int buttonSize = 100;

        //TODO arreglar

        // Set up column constraints
        System.out.println(columns);
        for (int i = 0; i < columns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / columns);
            gpane_table.getColumnConstraints().add(columnConstraints);
        }

        // Set up row constraints
        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / rows);
            gpane_table.getRowConstraints().add(rowConstraints);
        }


        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Button button = new Button(i + " " + j);
                button.setPrefSize(buttonSize, buttonSize);
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); //llena la celda
                gpane_table.add(button, i, j);
            }
        }
        pane_table.getChildren().add(gpane_table);

        double anchorValue = 0.0;
        AnchorPane.setTopAnchor(gpane_table, anchorValue);
        AnchorPane.setRightAnchor(gpane_table, anchorValue);
        AnchorPane.setBottomAnchor(gpane_table, anchorValue);
        AnchorPane.setLeftAnchor(gpane_table,anchorValue);
    }
}
