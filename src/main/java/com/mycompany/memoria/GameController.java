package com.mycompany.memoria;

import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
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
    boolean gameEnded = false;
    boolean gameRunning = false;
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
        deck = new Deck(amountCards, cardMatching);
        deck.assignRandomCardsToButtons(buttonsOnTable);
        setImagesToButtons();
        if(Ruleset.bonus){
            Ruleset.bonusValue=1;
        }



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
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> {
            Card card = bot.chooseCardAlgorithm(cardMatching, deck.getCards());
            int previousStreak = streakCounter;
            cardFlipLogistics(card);
            if (previousStreak != streakCounter) {
                bot.resetBotTurn();
            }
            if (!bot.isCurrentTurn()){
                bot.resetBotTurn();
                botTurn = false;
            }
        });
        pause.play();

        /*ArrayList<Card> botChoices = bot.chooseCards(cardMatching, deck.getCards());
        for (int i = 0; i < cardMatching; i++) {
            Card card = botChoices.get(i);
            cardFlipLogistics(card);
            }
         */
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

    private void showAnimatedText(String textContent) {
        Text text = new Text(textContent);
        text.setFont(new Font(24));
        text.setLayoutX(stackpane_table.getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(stackpane_table.getHeight() / 2 - text.getLayoutBounds().getHeight() / 2);

        stackpane_table.getChildren().add(text);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), text);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();

        scaleTransition.setOnFinished(event -> {
            // Aquí puedes agregar acciones adicionales cuando termine la animación
        });
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
        winnersLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #000000;");
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
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), winnersPane);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }


    private void winnersPopup(){ //TODO hacer bonito?
       //showAnimatedText("Los ganadores son: " + getWinners());
        showAnimatedWinnersPane();
    }

    private void updatePlayerGlobalScore(){
        Pre_GameController.updateScores(players);
    }

    private void endGame(){
        gameEnded = true;
        gameRunning = false;

    }

    private void checkAndEndGame() {
        if(deck.allCardsMatched()){
            endGame();
            updatePlayerGlobalScore();
            winnersPopup();
        }
    }


    @FXML
    private void startGame(ActionEvent actionEvent) {
        gameRunning = true;
        startIfBotIsFirst();
        btn_ready.setDisable(true);
        createGameTimer(Ruleset.matchtime).play();
    }

    private void shuffleMidGame(){
        deck.shuffleCards();
        ArrayList<Card> cards = deck.getCards();
        int i =0;
        for(Button button : buttonsOnTable){
            Card card = cards.get(i);
            button.setUserData(card);
            if(button.isDisabled() && !card.IsMatched()) button.setDisable(false);
            else if(!button.isDisabled() && card.IsMatched()) button.setDisable(true);
            i++;
        }
        showChangingDivergence();

        Ruleset.timeShuffle = false;
        //make an animation
        //first load "\\src\\main\\java\\com\\mycompany\\images\\timeline_change.gif";
       /*
        String path = "./\\src\\main\\java\\com\\mycompany\\images\\timeline_change.gif";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("File not found");
            return;
        }
        path = file.toURI().toString();
        Image gifImage = new Image(path);
        ImageView gifImageView = new ImageView(gifImage);
        stackpane_table.getChildren().add(gifImageView);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> removeGif(gifImageView));

        });

        */
        updateAllButtonGraphics(buttonsOnTable);

    }
    private String generateRandomDivergence() {
        double randomValue = ThreadLocalRandom.current().nextDouble(0.000001, 1.999999);
        return String.format("%.6f", randomValue);
    }

    private void showChangingDivergence() {
        // Crear el pane y agregarlo a la escena
        Pane divergencePane = new Pane();
        divergencePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);"); // Color de fondo
        stackpane_table.getChildren().add(divergencePane);

        // Crear la etiqueta de texto con números cambiantes
        Label divergenceLabel = new Label();
        divergenceLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: orange;"); // Estilo de la etiqueta
        divergenceLabel.setText(generateRandomDivergence());
        divergencePane.getChildren().add(divergenceLabel);
        divergenceLabel.setLayoutX(100); // Ajusta la posición X e Y según sea necesario
        divergenceLabel.setLayoutY(100);

        // Crear la animación de cambio de números
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            divergenceLabel.setText(generateRandomDivergence());
        }));
        timeline.setCycleCount(50); // Cambia este valor para controlar cuántas veces se actualizan los números
        timeline.setOnFinished(event -> {
            // Crear la animación de desvanecimiento
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), divergencePane);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
            fadeTransition.setOnFinished(fadeEvent -> stackpane_table.getChildren().remove(divergencePane));
            fadeTransition.play();
        });
        timeline.play();
    }


    private void removeGif(ImageView gifImageView){
        stackpane_table.getChildren().remove(gifImageView);
    }

    private Timeline createGameTimer(int timeLimitInSeconds) {
        timeLeft = timeLimitInSeconds;

        //envuelve un timeline para poder modificarlo dentro del evento
        Timeline[] timelineWrapper = new Timeline[1];
        timelineWrapper[0] = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if ((timeLeft <= Ruleset.matchtime/2) && (timeLeft!=0) && Ruleset.timeShuffle) {
                System.out.println(timeLeft/2+ " " + Ruleset.matchtime);
                lbl_timer.setTextFill(Color.RED);
                shuffleMidGame();
            }
            else if (timeLeft > 0) {
                decrementTime();
                lbl_timer.setText(String.valueOf(timeLeft));
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
}
