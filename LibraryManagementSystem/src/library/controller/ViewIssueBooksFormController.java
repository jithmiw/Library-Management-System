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
import library.model.IssueBook;
import library.util.CrudUtil;
import library.util.Reportable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ViewIssueBooksFormController implements Reportable {

    public TableView<IssueBook> dataTable;
    public TableColumn issueId;
    public TableColumn bookId;
    public TableColumn memberId;
    public TableColumn issueDate;
    public TableColumn dueDate;
    public JFXDatePicker datePicker;
    public JFXButton btnRefresh;
    private StackPane stackPane;
    private AnchorPane rootPane;
    private final ObservableList<IssueBook> list = FXCollections.observableArrayList();

    public void initialize() {
        issueId.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        bookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        memberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        issueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        dueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        datePicker.setValue(LocalDate.now());
        try {
            loadIssuedBooks();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadIssuedBooks() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT * FROM IssueBook");
        ObservableList<IssueBook> obList = FXCollections.observableArrayList();

        while (result.next()) {
            obList.add(
                    new IssueBook(
                            result.getString("issueID"),
                            result.getString("bookID"),
                            result.getString("memberID"),
                            result.getDate("issueDate"),
                            result.getDate("dueDate")
                    )
            );
        }
        dataTable.setItems(obList);
    }

    public void datePickerOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        list.clear();
        LocalDate date = datePicker.getValue();
        ResultSet result = CrudUtil.execute("SELECT * FROM IssueBook WHERE issueDate=? OR dueDate=?", date, date);
        IssueBook ib;

        while (result.next()) {
            ib = new IssueBook(result.getString(1), result.getString(2), result.getString(3), result.getDate(4), result.getDate(5));
            list.add(ib);
        }
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
        }
        dataTable.setItems(list);
    }

    @Override
    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void btnRefreshOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        datePicker.setValue(LocalDate.now());
        loadIssuedBooks();
    }
}

