package com.mycompany.memoria;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class SetRulesController {

    @FXML
    private Spinner<Integer> minPlayersSpinner;

    @FXML
    private Spinner<Integer> maxPlayersSpinner;

    // Agrega más campos FXML aquí según las reglas que desees configurar

    @FXML
    public void initialize() {
        // Inicializa los campos con los valores predeterminados de las reglas

        minPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Ruleset.minPlayers, Ruleset.maxPlayers, Ruleset.minPlayers));
        maxPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Ruleset.minPlayers, Ruleset.maxPlayers, Ruleset.maxPlayers));
        // ...
    }

    @FXML
    private void saveRules() {
        // Guarda los valores de los campos en las reglas
        Ruleset.minPlayers = minPlayersSpinner.getValue();
        Ruleset.maxPlayers = maxPlayersSpinner.getValue();
        // ...

        closeWindow();
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        // Cierra la ventana
        Stage stage = (Stage) minPlayersSpinner.getScene().getWindow();
        stage.close();
    }
}
