package library.controller;

import com.jfoenix.transitions.JFXFillTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import library.AppInitializer;
import library.database.DBConnection;
import library.util.Reportable;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DashboardFormController {
    public AnchorPane totalBook3dAnchor;
    public Label lblBook;
    public AnchorPane totalBookRoundAnchor;
    public ImageView imgTotalBook;
    public AnchorPane issue3dAnchor;
    public Label lblIssue;
    public AnchorPane issueRoundAnchor;
    public ImageView imgIssueBook;
    public AnchorPane returnBook3dAnchor;
    public Label lblReturn;
    public AnchorPane returnBookRoundAnchor;
    public ImageView imgReturnBook;
    public AnchorPane dataReportViewer;


    private JFXFillTransition ft;
    private StackPane stackPane;
    private AnchorPane rootPane;

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void initialize() {
        imgTotalBook.setImage(new Image(AppInitializer.class.getResource("images/book-stack.png").toString()));
        imgIssueBook.setImage(new Image(AppInitializer.class.getResource("images/issue_book.png").toString()));
        imgReturnBook.setImage(new Image(AppInitializer.class.getResource("images/return_book.png").toString()));

        openStage();
    }

    private void openStage() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        String path = "../library/view/ViewAllBooksForm.fxml";
        try {
            fxmlLoader.load(AppInitializer.class.getResource(path).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane root = fxmlLoader.getRoot();
        Reportable controller = fxmlLoader.getController();
        controller.setPane(stackPane, rootPane);
        dataReportViewer.getChildren().clear();
        dataReportViewer.getChildren().add(root);
        // this method is used to auto adjust height and weight
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
    }

    public void anchorMouseEntered(MouseEvent event) {
        getAnchorPane((AnchorPane) event.getSource(), Color.rgb(0, 185, 255), Color.rgb(100, 255, 218));
    }

    public void anchorMouseExited(MouseEvent event) {
        getAnchorPane((AnchorPane) event.getSource(), Color.rgb(100, 255, 218), Color.rgb(0, 185, 255));
    }

    public void anchorMouseClick(MouseEvent event) {
        selectReport(((AnchorPane) event.getSource()).getId());
    }

    private void getAnchorPane(AnchorPane source, Color start, Color end) {
        if (source.getId().equalsIgnoreCase(totalBook3dAnchor.getId())
                || source.getId().equalsIgnoreCase(totalBookRoundAnchor.getId())) {
            fillTransition(totalBook3dAnchor, start, end);
            fillTransition(totalBookRoundAnchor, start, end);
        } else if (source.getId().equalsIgnoreCase(issue3dAnchor.getId())
                || source.getId().equalsIgnoreCase(issueRoundAnchor.getId())) {
            fillTransition(issue3dAnchor, start, end);
            fillTransition(issueRoundAnchor, start, end);
        } else if (source.getId().equalsIgnoreCase(returnBook3dAnchor.getId())
                || source.getId().equalsIgnoreCase(returnBookRoundAnchor.getId())) {
            fillTransition(returnBook3dAnchor, start, end);
            fillTransition(returnBookRoundAnchor, start, end);
        }
    }

    private void fillTransition(AnchorPane anchorPane, Color start, Color end) {
        ft = new JFXFillTransition();
        ft.setRegion(anchorPane);
        ft.setDuration(new Duration(600));
        ft.setFromValue(start);
        ft.setToValue(end);
        ft.play();
    }

    private void selectReport(String id) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        String path = null;
        if (id.startsWith("totalBook"))
            path = "../library/view/ViewAllBooksForm.fxml";
        else if (id.startsWith("issue"))
            path = "../library/view/ViewIssueBooksForm.fxml";
        else if (id.startsWith("returnBook"))
            path = "../library/view/ViewReturnBooksForm.fxml";
        try {
            fxmlLoader.load(AppInitializer.class.getResource(path).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AnchorPane root = fxmlLoader.getRoot();
        Reportable controller = fxmlLoader.getController();
        controller.setPane(stackPane, rootPane);
        dataReportViewer.getChildren().clear();
        dataReportViewer.getChildren().add(root);
        // this method is used to auto adjust height and weight
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
    }

    public void btnViewChartOnAction(ActionEvent event) {
        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/library/view/reports/SQLChartReport.jasper"));
            Connection connection = DBConnection.getInstance().getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

