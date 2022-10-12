package library.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import library.AppInitializer;

public class AboutFormController {
    public ImageView imgLogo;
    private StackPane stackPane;
    private AnchorPane rootPane;

    public void initialize() {
        imgLogo.setImage(new Image(AppInitializer.class.getResource("images/logo.png").toString()));
    }

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }
}
