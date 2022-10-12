package library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle rect = new Rectangle(661, 325);
        rect.setArcHeight(26.0);
        rect.setArcWidth(26.0);
        Parent root = FXMLLoader.load(getClass().getResource("view/SplashScreenForm.fxml"));
        root.setClip(rect);
        Scene scene = new Scene(root, 661, 325);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}