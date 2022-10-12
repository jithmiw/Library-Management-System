package library.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import library.database.DBConnection;
import library.model.Book;
import library.util.CrudUtil;
import library.util.ValidationUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Pattern;

import static javafx.scene.input.KeyCode.ENTER;
import static library.util.LoadBooks.loadAllBooks;

public class BookInfoFormController {
    public TableView<Book> tblBooks;
    public TableColumn idCol;
    public TableColumn bookNameCol;
    public TableColumn authorCol;
    public TableColumn publisherCol;
    public TableColumn descriptionCol;
    public TableColumn stockCol;
    public JFXTextField txtSearch;
    public JFXButton btnSearch;
    public JFXButton btnRefresh;
    public JFXTextField txtBookId;
    public JFXTextField txtBookName;
    public JFXTextField txtAuthorName;
    public JFXTextField txtPublisher;
    public JFXTextField txtStocks;
    public JFXTextField txtDescription;
    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;
    public JFXButton btnPrintAllBooks;
    public LinkedHashMap<JFXTextField, Pattern> map = new LinkedHashMap<>();
    private Book book;
    private StackPane stackPane;
    private AnchorPane rootPane;
    private final ObservableList<Book> list = FXCollections.observableArrayList();

    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        addPattern();

        try {
            generateBookId();
            loadAllBooks(tblBooks);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPattern() {
        Pattern patternId = Pattern.compile("^(B)[0-9]{3,6}$");
        Pattern patternName = Pattern.compile("^[A-z ]{3,40}$");
        Pattern patternAuthor = Pattern.compile("^[A-z. ]{3,40}$");
        Pattern patternPublisher = Pattern.compile("^[A-z. ]{3,40}$");
        Pattern patternStock = Pattern.compile("^[0-9]{1,3}$");
        Pattern patternDesc = Pattern.compile("^[A-z ]{3,100}$");

        map.put(txtBookId, patternId);
        map.put(txtBookName, patternName);
        map.put(txtAuthorName, patternAuthor);
        map.put(txtPublisher, patternPublisher);
        map.put(txtStocks, patternStock);
        map.put(txtDescription, patternDesc);
    }

    public void textFieldsKeyReleased(KeyEvent keyEvent) throws SQLException, ClassNotFoundException {
        ValidationUtil.validate(map, btnAdd);

        if (keyEvent.getCode() == ENTER) {
            Object response = ValidationUtil.validate(map, btnAdd);

            if (response instanceof TextField) {
                TextField textField = (TextField) response;
                textField.requestFocus();
            } else if (response instanceof Boolean) {
                btnAddOnAction();
            }
        }
    }

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    private void disableTextFields(boolean aFlag) {
        txtBookId.setDisable(aFlag);
        txtBookName.setDisable(aFlag);
        txtAuthorName.setDisable(aFlag);
        txtPublisher.setDisable(aFlag);
        txtStocks.setDisable(aFlag);
        txtDescription.setDisable(aFlag);
    }

    private void changeButtonText(JFXButton btn) {
        disableTextFields(false);
        btnAdd.setDisable(true);
        btnUpdate.setDisable(true);
        btn.setText("Save");
        btn.setDisable(false);
        btnDelete.setText("Cancel");
    }

    private void enableButtons() {
        disableTextFields(true);
        btnAdd.setText("Add New");
        btnUpdate.setText("Edit");
        btnDelete.setText("Delete");
        btnAdd.setDisable(false);
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
        tblBooks.getSelectionModel().select(0);
        book = tblBooks.getSelectionModel().getSelectedItem();
        retrieveObjectData();
    }

    private void generateBookId() throws SQLException, ClassNotFoundException {
        String[] parts = getBookId().split("B");
        //String id = parts[1];
        txtBookId.setText("B" + 000 + (Integer.parseInt(parts[1]) + 1));
    }

    public String getBookId() throws SQLException, ClassNotFoundException {
        ResultSet set = CrudUtil.execute("SELECT Id FROM Book ORDER BY Id DESC LIMIT 1");
        if (set.next()) {
            return set.getString(1);
        } else {
            return "B000";
        }
    }

    public void btnAddOnAction() throws SQLException, ClassNotFoundException {
        if (btnAdd.getText().equalsIgnoreCase("Add New")) {
            changeButtonText(btnAdd);
            clearFields();
        } else {
            Book b = new Book(
                    txtBookId.getText(), txtBookName.getText(), txtAuthorName.getText(), txtPublisher.getText(),
                    txtDescription.getText(), Integer.parseInt(txtStocks.getText()), java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
            );

            try {
                if (CrudUtil.execute("INSERT INTO Book VALUES (?,?,?,?,?,?,?)", b.getId(), b.getName(), b.getAuthor(), b.getPublisher(), b.getDescription(), b.getStock(), b.getDate())) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully saved.").show();
                    loadAllBooks(tblBooks);
                    clearFields();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    private void setDefault(JFXTextField... textFields) {
        for (JFXTextField textField : textFields) {
            textField.setStyle("-fx-text-fill: black");
        }
    }

    private void clearFields() throws SQLException, ClassNotFoundException {
        generateBookId();
        txtBookName.clear();
        txtAuthorName.clear();
        txtPublisher.clear();
        txtStocks.clear();
        txtDescription.clear();
        txtBookId.requestFocus();

        setDefault(txtBookId, txtBookName, txtAuthorName, txtPublisher, txtStocks, txtDescription);
    }

    public void btnUpdateOnAction(ActionEvent event) {
        if (btnUpdate.getText().equalsIgnoreCase("Edit")) {
            changeButtonText(btnUpdate);
        } else {
            Book b = new Book(
                    txtBookId.getText(), txtBookName.getText(), txtAuthorName.getText(), txtPublisher.getText(),
                    txtDescription.getText(), Integer.parseInt(txtStocks.getText())
            );

            try {
                boolean isUpdated = CrudUtil.execute("UPDATE Book SET name=?, author=?, publisher=?, description=?, stock=? WHERE id=?", b.getName(), b.getAuthor(), b.getPublisher(), b.getDescription(), b.getStock(), txtBookId.getText());
                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully updated.").show();
                    loadAllBooks(tblBooks);
                } else {
                    new Alert(Alert.AlertType.WARNING, "Record not be updated.").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent event) {
        if (btnDelete.getText().equalsIgnoreCase("Cancel")) {
            enableButtons();
        } else {
            deleteBook(txtBookId.getText());
        }
    }

    private void deleteBook(String... id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            try {
                if (CrudUtil.execute("DELETE FROM Book WHERE id=?", id)) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully deleted.").show();
                    loadAllBooks(tblBooks);
                } else {
                    new Alert(Alert.AlertType.WARNING, "Record not be deleted.").show();
                }
            } catch (SQLException | ClassNotFoundException ignored) {
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Deletion process canceled.").show();
        }
    }

    public void menuDeleteOnAction(ActionEvent event) {
        if (tblBooks.getSelectionModel().getSelectedItem() != null) {
            ObservableList<Book> deletionBookList = tblBooks.getSelectionModel().getSelectedItems();
            String[] id = new String[deletionBookList.size()];
            int i = 0;
            for (Book book : deletionBookList)
                id[i++] = book.getId();
            deleteBook(id);
        } else
            new Alert(Alert.AlertType.NONE, "Please select record(s)").show();
    }

    public void tableMouseClicked(MouseEvent event) {
        if (!tblBooks.getSelectionModel().isEmpty()) {
            book = tblBooks.getSelectionModel().getSelectedItem();
            retrieveObjectData();
        }
    }

    private void retrieveObjectData() {
        txtBookId.setText(String.valueOf(book.getId()));
        txtBookName.setText(book.getName());
        txtAuthorName.setText(book.getAuthor());
        txtPublisher.setText(book.getPublisher());
        txtStocks.setText(String.valueOf(book.getStock()));
        txtDescription.setText(book.getDescription());
    }

    public void btnSearchOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        searchRecord();
    }

    public void txtSearchKeyPressed(KeyEvent event) throws SQLException, ClassNotFoundException {
        if (event.getCode() == KeyCode.ENTER)
            searchRecord();
    }

    private void searchRecord() throws SQLException, ClassNotFoundException {
        list.clear();
        ResultSet result = CrudUtil.execute("SELECT * FROM Book WHERE id=? OR name=? OR author=? OR publisher=? OR description=?", txtSearch.getText(), txtSearch.getText(), txtSearch.getText(), txtSearch.getText(), txtSearch.getText());

        while (result.next()) {
            Book b = new Book(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getInt(6));
            list.add(b);
        }
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
        }
        tblBooks.setItems(list);
    }

    public void btnRefreshOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearch.clear();
        loadAllBooks(tblBooks);
    }

    public void btnPrintAllBooksOnAction(ActionEvent event) {
        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/library/view/reports/PrintAllBooks.jasper"));
            Connection connection = DBConnection.getInstance().getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}