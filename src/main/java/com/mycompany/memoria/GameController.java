package com.mycompany.memoria;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static com.mycompany.memoria.utils.cardRatio;
import static com.mycompany.memoria.utils.updateAllButtonGraphics;

public class GameController {

    static int cardMatching;
    static int amountCards;
    //static int rows;
    //static int columns;
    static int buttonSize;
    private int streakCounter = 0;
    int flipCounter = 0;
    int turnCounter = 0;
    private boolean cardDelay = false;
    static boolean botTurn = false;
    ArrayList<Button> buttonsOnTable = new ArrayList<>();
    ArrayList<HBox> playerHBoxes;
    int playerCounter = 0;
    ArrayList<Player> players;
    Deck deck;
    private boolean gameEnded = false;
    private boolean gameRunning = false;
    private boolean worldLinesChanging = false;
    private int timeLeft = 0;

    @FXML
    private StackPane stackpane_table;

    private GridPane gpane_table;
    @FXML
    private VBox vbox_gameinfo;
    @FXML
    private Button btn_ready;
    @FXML
    private Label lbl_timer;

    @FXML
    public void initialize(){
        gpane_table = new GridPane();
        gpane_table.setGridLinesVisible(true);
        fillTable();
        stackpane_table.getStyleClass().add("stack-pane-table");
        deck = new Deck(amountCards, cardMatching);
        deck.assignRandomCardsToButtons(buttonsOnTable);
        boolean timercssAdded = lbl_timer.getStyleClass().add("label-timer");
        System.out.printf("Timer css added: %b\n", timercssAdded);
        setImagesToButtons();
        if(Ruleset.bonus){
            Ruleset.bonusValue=1;
        }
    }

    @FXML
    private void startGame(ActionEvent actionEvent) {
        gameRunning = true;
        startIfBotIsFirst();
        btn_ready.setDisable(true);
        if(Ruleset.timeLimitOn) createGameTimer(Ruleset.matchtime).play();
    }


    public void setPlayerCounter() {
        playerCounter = players.size();
    }

    private void setImagesToButtons() {
        for (Button button : buttonsOnTable) {
            button.setGraphic(((Card)button.getUserData()).getCurrentImage());
        }
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
        for(Player player: players){
            if (player.isBot()){
                BotPlayer bot = (BotPlayer) player;
                if(bot.isCheating()){
                    bot.setSeenCards(deck.getCards());
                }
            }
        }
        this.playerHBoxes = utils.getPlayersAsHBoxes(this.players);
        players.get(0).setCurrentTurn(true);
        vbox_gameinfo.getChildren().addAll(playerHBoxes);
    }


    @FXML
    public void fillTable(){
        System.out.println("HelloController.initialize()");
        gpane_table = new GridPane();
        gpane_table.setAlignment(Pos.CENTER); // Alinea el contenido del GridPane en el centro
        double buttonWidth = 100; // or any other desired value
        //Las cartas son cardRatio veces mas altas que anchas.
        int rows = (int) Math.ceil(Math.sqrt(amountCards / cardRatio));
        int columns = (int) Math.ceil((rows * cardRatio));
        VBox.setVgrow(gpane_table, Priority.ALWAYS);
        addButtonsToGridPane(gpane_table, rows, columns, amountCards, buttonWidth);
        stackpane_table.getChildren().add(gpane_table);
        
    }
    //TODO on action
    private void addButtonsToGridPane(GridPane gp, int rows, int columns, int amount, double buttonWidth) {
        int total = 0;
        for (int i = 0; i < rows && total < amount; i++) {
            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER);
            gp.add(hbox, 0, i);
            for (int j = 0; j < columns && total < amount; j++) {
                total++;
                addNewButton(buttonWidth, hbox);
            }
        }

    }

    private void addNewButton(double buttonWidth, HBox hbox) {
        Button btn = new Button();
        btn.setPrefWidth(buttonWidth);
        btn.setPrefHeight(buttonWidth * 1.5);
        btn.setOnMouseClicked(this::ActionFlipCard);
        btn.getStyleClass().add("card-button");
        buttonsOnTable.add(btn);
        hbox.getChildren().add(btn);
    }

    //onAction
    @FXML
    public void ActionFlipCard(MouseEvent mouseEvent) {

        if(cardDelay || botTurn || gameEnded || !gameRunning){
            return;
        }

        Button btn = (Button) mouseEvent.getSource();
        Card card = (Card) btn.getUserData();

        if (card.IsFlipped()) {
            return;
        }

        if (mouseEvent.getButton() == MouseButton.SECONDARY && mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {
            System.out.println("Right click");
            card.switchGodSight();
            if(card.getGodSight()){
                btn.setStyle("-fx-background-color: #00ff00");
            }else{
                btn.setStyle("-fx-background-color: #ffffff");
            }
            btn.setGraphic(card.getCurrentImage());
            return;
        }
        cardFlipLogistics(card);
    }

    private void cardFlipLogistics(Card card) {
        if(worldLinesChanging || cardDelay){
            if(checkBotTurn()) {
                System.out.println("Bot turn affected");botTurn();}
            return;
        }

        addSeenCardAllBots(card);
        card.flip();
        flipCounter++;
        if (flipCounter == cardMatching) {
            botsRandomForget();
            flipCounter = 0;
            if(deck.testAndMatch()){
                matchAction();
            }else{
                failAction();
            }
            }
        if (turnCounter == playerCounter) {
            turnCounter = 0;
        }
        checkAndEndGame();
        players.get(turnCounter).setCurrentTurn(true);
        if(checkBotTurn()) {
            System.out.println("Bot turn");;botTurn();}
        updateAllButtonGraphics(buttonsOnTable);

    }

    private void failAction() {
        if(streakCounter == 0 && Ruleset.punishmentExists){
              players.get(turnCounter).punish(Ruleset.punishmentValue);
         }
        streakCounter = 0;
        cardDelay = true;
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> resumeActions());
        pause.play();
        players.get(turnCounter).setCurrentTurn(false);
        turnCounter++;
    }

    private void matchAction() {
        for(Button button : buttonsOnTable){
            if(((Card)button.getUserData()).IsFlipped()){
                button.setDisable(true);
            }
        }
        int bonus=streakCounter*Ruleset.bonusValue;
        players.get(turnCounter).addScore(cardMatching*Ruleset.pointsPerMatchMult + bonus);
        streakCounter++;
    }

    void resumeActions(){
            deck.unflipUnmatched();
            updateAllButtonGraphics(buttonsOnTable);
            cardDelay = false;
        }

    private boolean checkBotTurn(){
        return players.get(turnCounter).isBot();
    }

    private void botTurn() { //TODO revisar si se puede acomodar mejor resetBotTurn
        if (!gameRunning || gameEnded) {
            return;
        }

        botTurn = true;
        BotPlayer bot = (BotPlayer) players.get(turnCounter);
        PauseTransition pause = new PauseTransition(Duration.seconds(Math.random()*3+1));
        pause.setOnFinished(event -> {
            Card card = bot.chooseCardAlgorithm(cardMatching, deck.getCards());
            cardFlipLogistics(card);
            if (flipCounter == 0) {
                bot.resetBotTurn();
            }
            if (!bot.isCurrentTurn()){
                bot.resetBotTurn();
                botTurn = false;
            }
        });
        pause.play();
    }

    private void addSeenCardAllBots(Card card){
        for(Player player : players){
            if(player.isBot()){
                ((BotPlayer)player).addSeenCard(card);
            }
        }
    }

    private void botsRandomForget(){
        for(Player player : players){
            if(player.isBot()){
                ((BotPlayer)player).randomCardForget();
            }
        }
    }

    private void startIfBotIsFirst(){
        if(players.get(0).isBot()){
            botTurn();
        }
    }

    private String getWinners(){
        float maxScore = 0;
        ArrayList<Player> winners = new ArrayList<>();
        for(Player player : players){
            if(player.getScore() > maxScore){
                maxScore = player.getScore();
                winners.clear();
                winners.add(player);
            }else if(player.getScore() == maxScore){
                winners.add(player);
            }
        }
        StringBuilder winnerString = new StringBuilder();
        for(Player player : winners){
            winnerString.append(player.getName()).append("\n");
        }
        return winnerString.toString();
    }



    private void showAnimatedWinnersPane(){
        Pane winnersPane = new Pane();
        stackpane_table.setAlignment(Pos.CENTER);
        stackpane_table.getChildren().add(winnersPane);
        winnersPane.setStyle("-fx-background-color: #ffffff");
        //añaade sombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        winnersPane.setEffect(dropShadow);

        //pone margenes
        winnersPane.setMinSize(400, 300);
        winnersPane.setMaxSize(400, 300);
        winnersPane.setPrefSize(400, 300);
        winnersPane.setLayoutX(stackpane_table.getWidth() / 2 - winnersPane.getMinWidth() / 2);
        winnersPane.setLayoutY(stackpane_table.getHeight() / 2 - winnersPane.getMinHeight() / 2);
        //genera texto de ganadores
        Label winnersLabel = new Label("Los ganadores son:\n" + getWinners());
        winnersLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #06c209;");
        winnersPane.getChildren().add(winnersLabel);
        //centra el texto
        winnersLabel.layoutXProperty().bind(winnersPane.widthProperty().subtract(winnersLabel.widthProperty()).divide(2));
        winnersLabel.layoutYProperty().bind(winnersPane.heightProperty().subtract(winnersLabel.heightProperty()).divide(2));
        //
        Button backButton = new Button("Regresar al menú");
        backButton.setStyle("-fx-font-size: 18; -fx-text-fill: #000000;");
        winnersPane.getChildren().add(backButton);
        backButton.setLayoutX(winnersPane.getPrefWidth() / 2 - backButton.getWidth() / 2);
        backButton.setLayoutY(winnersPane.getPrefHeight() - backButton.getHeight() - 20);
        backButton.setOnAction(event -> {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Pre_GameStage.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create a new scene with the loaded FXML file
            Scene preGameScene = new Scene(root, 1366, 700);

            // Get the current stage from the action event
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Set the new scene and show the stage
            currentStage.setScene(preGameScene);
            currentStage.show();
        });

        //añade animacion
        //ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), winnersPane);
        //scaleTransition.setFromX(0);
        //scaleTransition.setFromY(0);
        //scaleTransition.setToX(1);
        //scaleTransition.setToY(1);
        //scaleTransition.play();
    }


    private void winnersPopup(){
        showAnimatedWinnersPane();
    }

    private void updatePlayerGlobalScore(){
        Pre_GameController.updateScores(players);
    }

    private void endGame(){
        gameEnded = true;
        gameRunning = false;
        updatePlayerGlobalScore();
        winnersPopup();
    }

    private void checkAndEndGame() {
        if(deck.allCardsMatched()){
            endGame();
        }
    }

    private void eraseSeenCardsFromBots(){
        for(Player player : players){
            if(player.isBot() && !((BotPlayer)player).isCheating()){
                ((BotPlayer)player).setSeenCards(new ArrayList<>());
            }
        }
    }

    private void shuffleMidGame(){
        Ruleset.shuffleMidGame = false;
        deck.shuffleCards();
        eraseSeenCardsFromBots();
        ArrayList<Card> cards = deck.getCards();
        int i =0;
        for(Button button : buttonsOnTable){
            Card card = cards.get(i);
            button.setUserData(card);
            if(button.isDisabled() && !card.IsMatched()) button.setDisable(false);
            else if(!button.isDisabled() && card.IsMatched()) button.setDisable(true);
            i++;
        }
        playSound("/readingsteiner.mp3");
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        showChangingDivergence();
        //updateAllButtonGraphics(buttonsOnTable);
        //showChangingDivergence();
        //Ruleset.shuffleMidGame = false;
        //updateAllButtonGraphics(buttonsOnTable);

    }
    private String generateRandomDivergence() {
        double randomValue = ThreadLocalRandom.current().nextDouble(0.000001, 1.999999);
        return String.format("%.6f", randomValue);
    }



    private Timeline createGameTimer(int timeLimitInSeconds) {
        timeLeft = timeLimitInSeconds;

        //Envuelve un timeline para poder modificarlo dentro del evento
        //El compilador me da error si no lo hago así, "Timeline" puede que no se haya instanciado al modificarlo.
        Timeline[] timelineWrapper = new Timeline[1];
        timelineWrapper[0] = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if ((timeLeft <= Ruleset.matchtime/2) && (timeLeft!=0) && Ruleset.shuffleMidGame) {
                System.out.println(timeLeft/2+ " " + Ruleset.matchtime);
                lbl_timer.setTextFill(Color.RED);
                shuffleMidGame();
            }
            else if (timeLeft > 0) {
                decrementTime();
                lbl_timer.setText(timeLeft / 60 + String.valueOf(timeLeft%60));
            }else if(gameEnded){
              timelineWrapper[0].stop();
            } else {
                timelineWrapper[0].stop();
                endGame();
                updatePlayerGlobalScore();
                winnersPopup();
            }
        }));
        timelineWrapper[0].setCycleCount(Timeline.INDEFINITE);


        return timelineWrapper[0];
    }

    private void decrementTime(){
        timeLeft--;
    }

    private void showChangingDivergence() {

        //Evita que se pueda hacer click en las cartas mientras se muestra el cambio de divergencia
        worldLinesChanging = true;
        System.out.println("Cambio de cartas");
        Pane divergencePane = new Pane();
        divergencePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Color de fondo
        stackpane_table.getChildren().add(divergencePane);

        // Crea la etiqueta para el número de divergencia
        Label divergenceLabel = new Label();
        divergenceLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: orange;"); // Estilo de la etiqueta
        divergenceLabel.setText(generateRandomDivergence());
        divergencePane.getChildren().add(divergenceLabel);
        divergenceLabel.setLayoutX(100); // Ajusta la posición X e Y según sea necesario
        divergenceLabel.setLayoutY(100);

        // Crea una segunda etiqueta que se sobrepone a la primera
        Label secondDivergenceLabel = new Label();
        secondDivergenceLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: orange;"); // Estilo de la etiqueta
        secondDivergenceLabel.setText(generateRandomDivergence());
        divergencePane.getChildren().add(secondDivergenceLabel);
        secondDivergenceLabel.setLayoutX(100); // Ajusta la posición X e Y según sea necesario
        secondDivergenceLabel.setLayoutY(100);

        // Animación de cambio de números para ambas etiquetas
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            divergenceLabel.setText(generateRandomDivergence());
            secondDivergenceLabel.setText(generateRandomDivergence());
        }));
        timeline.setCycleCount(60); //ciclo de animacion

        // Animación aleatoria de opacidad para efecto de parpadeo
        Timeline opacityTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            double randomOpacity = Math.random() * 0.9;
            secondDivergenceLabel.setOpacity(randomOpacity);
        }));
        opacityTimeline.setCycleCount(60);
        opacityTimeline.setOnFinished(event -> {
            divergencePane.getChildren().remove(secondDivergenceLabel);
        });

        // Animación de agitación
        TranslateTransition shakeTransition = new TranslateTransition(Duration.millis(50), divergenceLabel);
        shakeTransition.setByX(10);
        shakeTransition.setCycleCount(6);
        shakeTransition.setAutoReverse(true);
        shakeTransition.setOnFinished(event -> updateAllButtonGraphics(buttonsOnTable));

        // Animación de desvanecimiento del pane
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), divergencePane);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setOnFinished(fadeEvent -> stackpane_table.getChildren().remove(divergencePane));

        // Secuencia de animaciones
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().addAll(new ParallelTransition(timeline, opacityTimeline), shakeTransition, fadeTransition);
        sequentialTransition.setOnFinished(event -> {
            divergenceLabel.setText(generateRandomDivergence());
            stackpane_table.getChildren().remove(divergencePane);
            worldLinesChanging = false;
        });
        sequentialTransition.play();
    }

    private void playSound(String soundResourcePath) {
        try {
            URL resourceURL = getClass().getResource(soundResourcePath);
            if (resourceURL == null) {
                System.out.println("No se ha encontrado el archivo de sonido: " + soundResourcePath);
                return;
            }
            Media sound = new Media(resourceURL.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();

        } catch (Exception e) {
            System.out.println("Error al reproducir el efecto de sonido: " + e.getMessage());
        }
    }

}


