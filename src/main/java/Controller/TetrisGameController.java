package Controller;

import Model.*;
;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TetrisGameController {

    private TetrisField field;
    private HighscoreWriter highscoreWriter;

    @FXML
    AnchorPane anchorPaneGame, nextShapePane;
    @FXML
    Rectangle gameRect;
    @FXML
    Rectangle rect_score, rect_highscore;
    @FXML
    Rectangle rect_speed, rect_nextShape;
    @FXML
    Label score_label, highscore_label, speed_label;
    @FXML
    Label fieldLabel;
    @FXML
    ImageView pauseImageView;
    private double gameWidth, gameHeight;
    private double tileSize;
    private boolean isPaused;
    private int highscore = -1;

    private final int width = 10;
    private final int height = 20;
    private int speed = 500;
    private final int minSpeed = 200;
    private final int speedUpFactor = 5;

    private Image[] tiles;
    private ImageView[][] tileImageViews;
    private ImageView[][] nextShapeImageViews;
    private Image pauseIcon, continueIcon;
    private String directory;
    private Random random;
    private int startColor;
    private Timer timer;
    private TimerTask task;


    public void init() {
        field = new Field();
        gameWidth = gameRect.getWidth() - 2;
        gameHeight = gameRect.getHeight() - 2;
        tileSize = gameWidth / 10;
        isPaused = false;
        fieldLabel.setVisible(false);
        highscoreWriter = new HighscoreWriter();
        highscore = highscoreWriter.getHighscore();

        random = new Random();
        startColor = random.nextInt(7) + 1;

        directory = System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\";

        tiles = new Image[8];
        for (int i = 0; i < 8; i++) {
            tiles[i] = new Image(directory + "TetrisTile" + i + ".png");
        }
        tileImageViews = new ImageView[width][height];

        //Load Images
        pauseIcon = new Image(directory + "PauseIcon.png");
        continueIcon = new Image(directory + "ContinueIcon.png");

        //Create gameGrid
        GridPane gridPane = new GridPane();
        for (int i = 0; i < width; i++) {
            gridPane.addColumn(i);
        }
        for (int i = 0; i < height; i++) {
            gridPane.addRow(i);
        }

        for (int i = 0; i < width; i++) {
            for (int k = 0; k < height; k++) {

                tileImageViews[i][k] = new ImageView(tiles[0]);
                tileImageViews[i][k].setFitWidth(tileSize);
                tileImageViews[i][k].setFitHeight(tileSize);

                gridPane.add(tileImageViews[i][k], i, k);
            }
        }

        anchorPaneGame.getChildren().add(gridPane);

        //Extra Information
        score_label.setText(Integer.toString(field.getScore()));
        highscore_label.setText(Integer.toString(highscore));
        speed_label.setText(Integer.toString(speed));

        //Next Shape Information
        GridPane nextShapeGrid = new GridPane();
        for (int i = 0; i < 3; i++) {
            nextShapeGrid.addColumn(i);
        }
        for (int i = 0; i < 2; i++) {
            nextShapeGrid.addRow(i);
        }
        nextShapeImageViews = new ImageView[4][2];
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 2; k++) {
                nextShapeImageViews[i][k] = new ImageView(tiles[0]);
                nextShapeImageViews[i][k].setFitWidth(27);
                nextShapeImageViews[i][k].setFitHeight(27);

                nextShapeGrid.add(nextShapeImageViews[i][k], i, k);
            }
        }
        nextShapePane.getChildren().add(nextShapeGrid);

        //Game Ticks
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        };
        field.startGame();
        updateNextShape();
        timer.schedule(task, 1500, speed);
    }

    @FXML
    void keyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case S:
            case DOWN:
            case NUMPAD8:
                if (!isPaused && !field.isLost()) {
                    updateGame();
                }
                break;
            case W:
            case SPACE:
            case NUMPAD0:
            case UP:
                if (!isPaused && !field.isLost()) {
                    field.turnActiveShape();
                    updateTiles();
                }
                break;
            case A:
            case LEFT:
            case NUMPAD4:
                if (!isPaused && !field.isLost()) {
                    field.moveLeft();
                    updateTiles();
                }
                break;
            case NUMPAD6:
            case RIGHT:
            case D:
                if (!isPaused && !field.isLost()) {
                    field.moveRight();
                    updateTiles();
                }
                break;
            case ESCAPE:
                pauseGame();
                break;
        }
    }

    @FXML
    public void pauseGame() {
        if (!field.isLost()) {
            if (isPaused) {
                speed += speedUpFactor;
                speedUp();
                setOpacityForTiles(1);
                fieldLabel.setVisible(false);
                pauseImageView.setImage(pauseIcon);
            } else {
                setOpacityForTiles(0.5);
                fieldLabel.setVisible(true);
                pauseImageView.setImage(continueIcon);
                timer.cancel();
            }
            isPaused = !isPaused;
        }
    }

    @FXML
    public void restartGame() {
        if (field.isLost() || isPaused) {
            checkAndWriteHighscore(field.getScore());

            field.reset();
            field.startGame();
            startColor = random.nextInt(7) + 1;
            fieldLabel.setText("Game Paused");
            fieldLabel.setVisible(false);
            pauseImageView.setImage(pauseIcon);
            speed = 500 + speedUpFactor;
            isPaused = false;

            Platform.runLater(() -> highscore_label.setText
                    (Integer.toString(highscore)));

            setOpacityForTiles(1);
            updateNextShape();
            speedUp();
            updateGame();
        }
    }

    private void setOpacityForTiles(double opacity) {
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < height; k++) {
                tileImageViews[i][k].setOpacity(opacity);
            }
        }
    }

    private void updateTiles() {
        int[][] curField = field.getField();
        for (int i = 0; i < width; i++) {
            for (int k = 0; k < height; k++) {
                int color;
                if (curField[i][height - k - 1] == 0) {
                    color = 0;
                } else {
                    color = ((curField[i][height - k - 1] + startColor) % 7) + 1;
                }
                tileImageViews[i][k].setImage(tiles[color]);
            }
        }
    }

    private void updateLabels() {
        Platform.runLater(() -> score_label.setText(Integer.toString(field.getScore())));
        Platform.runLater(() -> speed_label.setText(Integer.toString(speed)));
    }

    private void speedUp() {
        speed = Math.max(speed - speedUpFactor, minSpeed);
        TimerTask taskNewSpeed = new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        };
        timer.cancel();
        timer = new Timer();
        timer.schedule(taskNewSpeed, speed, speed);
    }

    private void updateGame() {
        boolean newTileSpawned = field.gameTick();
        updateTiles();
        updateLabels();
        if (newTileSpawned) {
            speedUp();
            updateNextShape();
        }
        if (field.isLost()) {
            timer.cancel();
            highscoreWriter.setHighscore(field.getScore());
            setOpacityForTiles(0.5);
            Platform.runLater(() -> fieldLabel.setText("You lost!"));
            fieldLabel.setVisible(true);
        }
    }

    private void updateNextShape() {
        Shape nextShape = field.getNextShape();
        int color = ((field.getNextShapeId() + startColor) % 7) + 1;

        Pos top1 = new Pos(0, 0);
        Pos top2 = new Pos(1, 0);
        Pos top3 = new Pos(2, 0);
        Pos bottom1 = new Pos(0, 1);
        Pos bottom2 = new Pos(1, 1);
        Pos bottom3 = new Pos(2, 1);
        Pos bottom4 = new Pos(3, 1);
        Pos[] positions;

        switch (nextShape) {
            case L_SHAPE:
                changeNextShapeGridWith(27);
                positions = new Pos[]{top3, bottom1, bottom2, bottom3};
                fillNextShapeGrid(positions, color);
                break;
            case J_SHAPE:
                changeNextShapeGridWith(27);
                positions = new Pos[]{top1, bottom1, bottom2, bottom3};
                fillNextShapeGrid(positions, color);
                break;
            case PYRAMID:
                changeNextShapeGridWith(27);
                positions = new Pos[]{top2, bottom1, bottom2, bottom3};
                fillNextShapeGrid(positions, color);
                break;
            case ZIGZAG_LEFT:
                changeNextShapeGridWith(27);
                positions = new Pos[]{top2, top3, bottom1, bottom2};
                fillNextShapeGrid(positions, color);
                break;
            case ZIGZAG_RIGHT:
                changeNextShapeGridWith(27);
                positions = new Pos[]{top1, top2, bottom2, bottom3};
                fillNextShapeGrid(positions, color);
                break;
            case LINE:
                changeNextShapeGridWith(21);
                positions = new Pos[]{bottom1, bottom2, bottom3, bottom4};
                fillNextShapeGrid(positions, color);
                break;
            case SQUARE:
                changeNextShapeGridWith(21);
                positions = new Pos[]{top2, top3, bottom2, bottom3};
                fillNextShapeGrid(positions, color);
                break;
        }
    }

    private void changeNextShapeGridWith(int width) {

        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 2; k++) {
                nextShapeImageViews[i][k].setFitWidth(width);
                nextShapeImageViews[i][k].setFitHeight(width);
            }
        }
    }

    private void fillNextShapeGrid(Pos[] positions, int color) {
        for (int i = 0; i < 4; i++) {
            for (int k = 0; k < 2; k++) {
                nextShapeImageViews[i][k].setImage(tiles[0]);
            }
        }
        for (Pos pos : positions) {
            nextShapeImageViews[pos.x][pos.y].setImage(tiles[color]);
        }
    }

    private void checkAndWriteHighscore(int potentialHighscore) {

        if (potentialHighscore > highscore) {
            highscoreWriter.setHighscore(potentialHighscore);
            highscore = potentialHighscore;
        }
    }
}