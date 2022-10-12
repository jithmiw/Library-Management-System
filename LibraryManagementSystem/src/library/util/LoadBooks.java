package library.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import library.model.Book;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadBooks {

    public static void loadAllBooks(TableView tblBooks) throws ClassNotFoundException, SQLException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Book");
        ObservableList<Book> obList = FXCollections.observableArrayList();

        while (result.next()) {
            obList.add(
                    new Book(
                            result.getString("id"),
                            result.getString("name"),
                            result.getString("author"),
                            result.getString("publisher"),
                            result.getString("description"),
                            result.getInt("stock")
                    )
            );
        }
        tblBooks.setItems(obList);
    }
}
