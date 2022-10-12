package library.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import library.model.Book;
import library.util.CrudUtil;
import library.util.Reportable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static library.util.LoadBooks.loadAllBooks;

public class ViewAllBooksFormController implements Reportable {

    public TableView<Book> tblBooks;
    public TableColumn idCol;
    public TableColumn bookNameCol;
    public TableColumn authorCol;
    public TableColumn publisherCol;
    public TableColumn descriptionCol;
    public TableColumn stockCol;
    public JFXDatePicker datePicker;
    public JFXButton btnRefresh;

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

        datePicker.setValue(LocalDate.now());
        try {
            loadAllBooks(tblBooks);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void datePickerOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        list.clear();
        LocalDate date = datePicker.getValue();
        ResultSet result = CrudUtil.execute("SELECT * FROM Book WHERE date1=?", date);
        Book b;

        while (result.next()) {
            b = new Book(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getInt(6));
            list.add(b);
        }
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
        }
        tblBooks.setItems(list);
    }

    public void btnRefreshOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        datePicker.setValue(LocalDate.now());
        loadAllBooks(tblBooks);
    }
}

