package library.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import library.AppInitializer;
import library.model.Book;
import library.model.IssueBook;
import library.model.IssueBookDetail;
import library.model.Member;
import library.util.CrudUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BookIssueFormController {
    private final String[] str = getIssueId().split("I");
    private final ObservableList<Book> obList = FXCollections.observableArrayList();
    private final ObservableList<IssueBookDetail> issuedBookList = FXCollections.observableArrayList();
    public ImageView imgBookIssue;
    public JFXComboBox<String> txtBookIssueId;
    public JFXButton btnRefresh;
    public Label lblIssueID;
    public Label lblBookAvailability;
    public Label lblNoOfBooks;
    public TextField txtTotalFine;
    public TextField txtCash;
    public TextField txtChange;
    public JFXButton btnIssueBook;
    public JFXButton btnReturnBook;
    public JFXComboBox<String> txtBookID;
    public JFXComboBox<String> txtMemberID;
    public TableView<Book> table;
    public TableColumn colBookID;
    public TableColumn colBookName;
    public TableColumn colAuthorName;
    public TableColumn colPublisher;
    public TableColumn colAction;
    public JFXButton btnAddBook;
    public JFXTextField txtSearch;
    public JFXButton btnSearch;
    public TableView tblReturn;
    public TableColumn colIssueID;
    public TableColumn colMemberID;
    public TableColumn colBkID;
    public TableColumn colIssueDate;
    public TableColumn colDueDate;
    public TableColumn colReturnDate;
    public TableColumn colFine;
    public TableColumn colAct;
    public JFXButton btnPayFine;
    private int id = Integer.parseInt(str[1]);
    private StackPane stackPane;
    private AnchorPane rootPane;

    public BookIssueFormController() throws SQLException, ClassNotFoundException {
    }

    public void setPane(StackPane stackPane, AnchorPane rootPane) {
        this.stackPane = stackPane;
        this.rootPane = rootPane;
    }

    public void initialize() throws SQLException, ClassNotFoundException {
        imgBookIssue.setImage(new Image(AppInitializer.class.getResource("images/bookissue.png").toString()));
        initializeIssueTbl();
        initializeReturnTbl();

        try {
            generateIssueId();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        setIssuedDetails();
        setMemberIds();
        setBookIds();
        txtBookIDOnAction();
    }

    private void initializeReturnTbl() {
        colIssueID.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        colMemberID.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colBkID.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));
        colAct.setCellValueFactory(new PropertyValueFactory<>("action"));
    }

    private void initializeIssueTbl() {
        colBookID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAuthorName.setCellValueFactory(new PropertyValueFactory<>("author"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
    }

    private void setIssuedDetails() {
        try {
            ObservableList<String> issueIdObList = FXCollections.observableArrayList(IssueCrudController.getIssuedIds());
            txtBookIssueId.setItems(issueIdObList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void setMemberIds() {
        try {
            ObservableList<String> mIdObList = FXCollections.observableArrayList(
                    MemberCrudController.getMemberIds()
            );
            txtMemberID.setItems(mIdObList);
            txtMemberID.setValue(mIdObList.get(0));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setBookIds() {
        try {
            ObservableList<String> bIdObList = FXCollections.observableArrayList(BookCrudController.getBookIds());
            txtBookID.setItems(bIdObList);
            txtBookID.setValue(bIdObList.get(0));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String generateIssueId() throws SQLException, ClassNotFoundException {
        String[] parts = getIssueId().split("I");
        lblIssueID.setText("Issue ID : " + "I" + 000 + (id + 1));
        return "I" + 000 + (Integer.parseInt(parts[1]) + 1);
    }

    public String getIssueId() throws SQLException, ClassNotFoundException {
        ResultSet set = CrudUtil.execute("SELECT issueId FROM IssueBook ORDER BY issueId DESC LIMIT 1");
        if (set.next()) {
            return set.getString(1);
        } else {
            return "I000";
        }
    }

    private int getStock() {
        Book b = null;
        try {
            b = BookCrudController.getBook(txtBookID.getValue());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assert b != null;
        return b.getStock();
    }

    public void txtBookIDOnAction() throws SQLException, ClassNotFoundException {
        ResultSet result = CrudUtil.execute("SELECT issueID FROM IssueBook WHERE bookID=? && status=?", txtBookID.getValue(), "Issued");
        ArrayList<String> issuedBooks = new ArrayList<>();

        while (result.next()) {
            issuedBooks.add(result.getString(1));
        }
        int stock = getStock();
        int noOfBooks = 0;

        for (String issueID : issuedBooks) {
            noOfBooks++;
        }
        if (noOfBooks < stock) {
            lblBookAvailability.setText("Book is available ✅");
        } else {
            lblBookAvailability.setText("Book is not available ❎");
        }
    }

    private int setNoOfBooks() {
        int noOfBooks = 0;
        for (Book book : obList) {
            noOfBooks++;
        }
        lblNoOfBooks.setText("No. of Books : " + noOfBooks);
        return noOfBooks;
    }

    private void calculateTotal() {
        double total = 0;
        for (IssueBookDetail tm : issuedBookList) {
            total += tm.getFine();
        }
        txtTotalFine.setText(String.valueOf(total));
    }

    private void calculateChange() {
        double change = Double.parseDouble(txtCash.getText()) - Double.parseDouble(txtTotalFine.getText());
        txtChange.setText(String.valueOf(change));
    }

    public void btnAddBookOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        id++;
        lblIssueID.setText("Issue ID : " + "I" + 000 + (id));
        JFXButton btn = new JFXButton("Delete");
        btn.setStyle("-fx-background-color: #193A5A");
        ResultSet result = CrudUtil.execute("SELECT * FROM Book WHERE id=?", txtBookID.getValue());
        Book book = null;

        while (result.next()) {
            book = new Book(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getInt(6)
            );
        }
        Book b = new Book(book.getId(), book.getName(), book.getAuthor(), book.getPublisher(), btn);

        btn.setOnAction(e -> {
            obList.remove(b);
            setNoOfBooks();
            btnAddBook.setDisable(false);
            id--;
            lblIssueID.setText("Issue ID : " + "I" + 000 + (id));
        });
        if (setNoOfBooks() >= 5) {
            new Alert(Alert.AlertType.WARNING, "A member can borrow only 5 books at a time").show();
            btnAddBook.setDisable(true);
        } else {
            obList.add(b);
            table.setItems(obList);
        }
        table.refresh();
        setNoOfBooks();
    }

    public void btnIssueBookOnAction() {
        int issuedBooks = 0;
        java.sql.Date issueDate = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        Calendar c = Calendar.getInstance();
        c.setTime(issueDate);
        c.add(Calendar.DATE, 14);
        java.sql.Date dueDate = new java.sql.Date(c.getTimeInMillis());

        for (Book tm : obList) {
            try {
                if (CrudUtil.execute("INSERT INTO IssueBook VALUES (?,?,?,?,?,?)",
                        generateIssueId(),
                        tm.getId(),
                        txtMemberID.getValue(),
                        issueDate,
                        dueDate,
                        "Issued")) {
                }
                issuedBooks++;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
        btnAddBook.setDisable(false);
        setIssuedBooks(issuedBooks);
        new Alert(Alert.AlertType.INFORMATION, "Record(s) successfully saved.").show();
        setIssuedDetails();
    }

    public void loadIssuedBooks(String query, String... value) {
        JFXButton btn;
        issuedBookList.clear();
        IssueBookDetail ib;
        try {
            ResultSet result = CrudUtil.execute(query, value);
            while (result.next()) {
                btn = new JFXButton("Delete");
                btn.setStyle("-fx-background-color: #193A5A");
                IssueBook b = new IssueBook(result.getString(1), result.getString(2), result.getString(3), result.getDate(4), result.getDate(5));
                ib = new IssueBookDetail(b.getIssueId(), b.getBookId(), b.getMemberId(), b.getIssueDate(), b.getDueDate(), java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())), calculateFine(b.getDueDate()), btn);
                issuedBookList.add(ib);
                IssueBookDetail finalIb = ib;
                btn.setOnAction(e -> {
                    issuedBookList.remove(finalIb);
                    calculateTotal();
                });
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        if (issuedBookList.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
        }
        tblReturn.setItems(issuedBookList);
        tblReturn.refresh();
        calculateTotal();
    }

    public void txtBookIssueIdOnAction() {
        loadIssuedBooks("SELECT * FROM IssueBook WHERE issueID=?", txtBookIssueId.getValue());
    }

    public void btnReturnBookOnAction(ActionEvent event) {
        java.sql.Date returnDate = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        for (IssueBookDetail tm : issuedBookList) {
            try {
                if (CrudUtil.execute("INSERT INTO IssueBookDetail VALUES (?,?,?,?,?,?,?)",
                        tm.getIssueId(),
                        tm.getBookId(),
                        tm.getMemberId(),
                        tm.getIssueDate(),
                        tm.getDueDate(),
                        returnDate,
                        calculateFine(tm.getDueDate()))) {
                }
                CrudUtil.execute("UPDATE IssueBook SET status=? WHERE issueID=?", "Returned", tm.getIssueId());
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "Record(s) successfully saved.").show();
        setIssuedDetails();
    }

    private double calculateFine(Date dueDate) {
        java.sql.Date returnedDate = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        long deltaDays = returnedDate.getTime() - dueDate.getTime();

        if (deltaDays > 0) {
            return deltaDays * 10;
        } else {
            return 0;
        }
    }

    private void setIssuedBooks(int issuedBooks) {
        Member m = null;
        try {
            m = MemberCrudController.getMember(txtMemberID.getValue());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            CrudUtil.execute("UPDATE Member SET issuedBooks=? WHERE id=?", (m.getIssuedBooks() + issuedBooks), m.getId());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void txtSearchKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            searchRecord();
    }

    private void searchRecord() {
        loadIssuedBooks("SELECT * FROM IssueBook WHERE memberID=? && status=?", txtSearch.getText(), "Issued");
    }

    public void btnSearchOnAction(ActionEvent event) {
        searchRecord();
    }

    public void btnPayFineOnAction(ActionEvent event) {
        double totalFine = Double.parseDouble(txtTotalFine.getText());
        double cash = Double.parseDouble(txtCash.getText());
        double change = Double.parseDouble(txtChange.getText());
        String memberId = txtMemberID.getValue();

        HashMap map = new HashMap();
        map.put("total", totalFine);
        map.put("memberId", memberId);
        map.put("cash", cash);
        map.put("change", change);

        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/library/view/reports/FineInvoice.jasper"));
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, map, new JRBeanCollectionDataSource(issuedBookList));
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public void txtChangeKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            calculateChange();
    }

    public void btnRefreshOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        table.refresh();
        tblReturn.refresh();
    }
}
