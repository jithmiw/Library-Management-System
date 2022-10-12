package library.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.transitions.JFXFillTransition;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import library.AppInitializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuFormController {
    public final static Image DASHBOARD_BLACK = new Image(
            AppInitializer.class.getResource("images/dashboard_black.png").toString());
    public final static Image DASHBOARD_WHITE = new Image(
            AppInitializer.class.getResource("images/dashboard_white.png").toString());
    public final static Image ISSUE_RETURN_BALCK = new Image(
            AppInitializer.class.getResource("images/return_issueBook_balck.png").toString());
    public final static Image ISSUE_RETURN_WHITE = new Image(
            AppInitializer.class.getResource("images/return_issueBook_white.png").toString());
    public final static Image STUDENTINFO_BLACK = new Image(
            AppInitializer.class.getResource("images/students_black.png").toString());
    public final static Image STUDENTINFO_WHITE = new Image(
            AppInitializer.class.getResource("images/students_white.png").toString());
    public final static Image BOOKINFO_BLACK = new Image(AppInitializer.class.getResource("images/books_black.png").toString());
    public final static Image BOOKINFO_WHITE = new Image(AppInitializer.class.getResource("images/books_white.png").toString());
    public final static Image ABOUT_BLACK = new Image(AppInitializer.class.getResource("images/about_black.png").toString());
    public final static Image ABOUT_WHITE = new Image(AppInitializer.class.getResource("images/about_white.png").toString());

    public StackPane stackRootPane;
    public AnchorPane anchorPane;
    public JFXButton btnDashboard;
    public AnchorPane centerPane;
    public Label lblDate;
    public Label lblTime;
    public ImageView imgDashboard;
    public JFXButton btnIssueReturnBook;
    public ImageView imgIssueReturnBook;
    public JFXButton btnMemberInfo;
    public ImageView imgMemberInfo;
    public JFXButton btnBookInfo;
    public ImageView imgBookInfo;
    public JFXButton btnAbout;
    public ImageView imgAbout;
    public ImageView imgLogo;
    private JFXButton activeMenuButton;
    private JFXFillTransition ft;

    public void initialize() {
        imgLogo.setImage(new Image(AppInitializer.class.getResource("images/logo.png").toString()));
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
            lblDate.setText(LocalDateTime.now().format(dateFormat));
            lblTime.setText(LocalDateTime.now().format(timeFormat));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        ft = new JFXFillTransition();
        activeMenuButton = btnDashboard;
        btnDashboard.fire();
    }

    public void btnBookInfoClick(ActionEvent event) {
        selectMenu("../library/view/BookInfoForm.fxml", event, "BookInfo");
    }

    public void btnMemberInfoClick(ActionEvent event) {
        selectMenu("../library/view/MemberInfoForm.fxml", event, "MemberInfo");
    }

    public void btnIssueReturnBookClick(ActionEvent event) {
        selectMenu("../library/view/BookIssueForm.fxml", event, "BookIssue");
    }

    public void btnAboutClick(ActionEvent event) {
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/library/view/AboutForm.fxml"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.initStyle(StageStyle.UTILITY);
        stage.centerOnScreen();
        stage.show();
    }

    public void btnDashboardClick(ActionEvent event) {
        selectMenu("../library/view/DashboardForm.fxml", event, "Dashboard");
    }

    public void menuButtonMouseEntered(MouseEvent event) {
        if (event.getSource() != activeMenuButton) {
            ft = new JFXFillTransition();
            ft.setRegion((JFXButton) event.getSource());
            ft.setDuration(new Duration(500));
            ft.setFromValue(Color.WHITE);
            ft.setToValue(Color.rgb(151, 136, 134));
            ft.play();
        }
    }

    public void menuButtonMouseExited(MouseEvent event) {
        if (event.getSource() != activeMenuButton) {
            ft = new JFXFillTransition();
            ft.setRegion((JFXButton) event.getSource());
            ft.setDuration(new Duration(500));
            ft.setFromValue(Color.rgb(151, 136, 134));
            ft.setToValue(Color.WHITE);
            ft.play();
        }
    }

    private void selectMenu(String fxmlName, ActionEvent event, String controllerClass) {
        if (ft.getStatus() == Animation.Status.RUNNING)
            ft.stop();
        defaultButtonImage(((JFXButton) event.getSource()).getId());
        activeMenuButton.getStyleClass().remove("menuButtonActive");
        ((JFXButton) event.getSource()).getStyleClass().add("menuButtonActive");
        activeMenuButton = ((JFXButton) event.getSource());
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            fxmlLoader.load(AppInitializer.class.getResource(fxmlName).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (controllerClass.equalsIgnoreCase("Dashboard"))
            ((DashboardFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("BookIssue"))
            ((BookIssueFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("MemberInfo"))
            ((MemberInfoFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        else if (controllerClass.equalsIgnoreCase("BookInfo"))
            ((BookInfoFormController) fxmlLoader.getController()).setPane(stackRootPane, anchorPane);
        AnchorPane root = fxmlLoader.getRoot();
        centerPane.getChildren().clear();
        centerPane.getChildren().add(root);

        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
    }

    private void defaultButtonImage(String btnName) {
        imgDashboard.setImage(DASHBOARD_BLACK);
        imgIssueReturnBook.setImage(ISSUE_RETURN_BALCK);
        imgMemberInfo.setImage(STUDENTINFO_BLACK);
        imgBookInfo.setImage(BOOKINFO_BLACK);
        imgAbout.setImage(ABOUT_BLACK);
        if (btnName.equalsIgnoreCase(btnDashboard.getId()))
            imgDashboard.setImage(DASHBOARD_WHITE);
        else if (btnName.equalsIgnoreCase(btnIssueReturnBook.getId()))
            imgIssueReturnBook.setImage(ISSUE_RETURN_WHITE);
        else if (btnName.equalsIgnoreCase(btnMemberInfo.getId()))
            imgMemberInfo.setImage(STUDENTINFO_WHITE);
        else if (btnName.equalsIgnoreCase(btnBookInfo.getId()))
            imgBookInfo.setImage(BOOKINFO_WHITE);
        else if (btnName.equalsIgnoreCase(btnAbout.getId()))
            imgAbout.setImage(ABOUT_WHITE);
    }
}