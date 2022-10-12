package library.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import library.AppInitializer;
import library.util.CrudUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public JFXTextField txtUsername1;
    public JFXPasswordField txtPassword1;
    public StackPane stackPane;
    public AnchorPane rootPane;
    public ImageView bgImage;
    public JFXButton btnLogin1;
    public JFXButton btnCancel1;


    public void btnCancelClick(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    public void btnLoginClick(ActionEvent event) throws SQLException, ClassNotFoundException {
        if (isAuthorized()) {
            openDashboard();
        } else {
            new Alert(Alert.AlertType.WARNING, "You have entered an invalid username or password. Please try again.").show();
        }
    }

    private boolean isAuthorized() throws SQLException, ClassNotFoundException {
        String username = null;
        String password = null;
        ResultSet result = CrudUtil.execute("SELECT * FROM User");
        if (result.next()) {
            username = result.getString(1);
            password = result.getString(2);
        }
        return txtUsername1.getText().equals(username) && txtPassword1.getText().equals(password);
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppInitializer.class.getResource("../library/view/MenuForm.fxml"));
            loader.load();
            Parent parent = loader.getRoot();
            Scene scene = new Scene(parent);
            Stage dashboardStage = new Stage();
            dashboardStage.setMinHeight(626.0);
            dashboardStage.setMinWidth(926.0);
            dashboardStage.setScene(scene);
            dashboardStage.setMaximized(true);
            dashboardStage.show();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
