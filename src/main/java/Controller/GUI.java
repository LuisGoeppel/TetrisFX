package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class GUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        File iconFile = new File("src/main/resources/Images/TetrisIcon.png");
        Image icon = new Image(iconFile.toURI().toString());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TetrisGame.fxml"));
        Parent root = loader.load();

        TetrisGameController gameController = loader.getController();
        Scene gameScene = new Scene(root);
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, gameController::keyPressed);
        gameController.init();

        stage.setTitle("Tetris");
        stage.getIcons().add(icon);
        stage.setScene(gameScene);
        stage.setResizable(false);
        stage.requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}