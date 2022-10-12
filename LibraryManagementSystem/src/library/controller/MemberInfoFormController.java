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
import library.model.Member;
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

public class MemberInfoFormController {
    public TableView<Member> tblMembers;
    public TableColumn idCol;
    public TableColumn memberNameCol;
    public TableColumn emailCol;
    public TableColumn mobileCol;
    public TableColumn addressCol;
    public TableColumn issueBookCol;

    public JFXTextField txtSearch;
    public JFXButton btnSearch;
    public JFXButton btnRefresh;
    public JFXTextField txtMID;
    public JFXTextField txtMemberName;
    public JFXTextField txtEmail;
    public JFXTextField txtMobile;
    public JFXTextField txtAddress;
    public JFXButton btnAdd;
    public JFXButton btnUpdate;
    public JFXButton btnDelete;
    public JFXButton btnPrintAllMembers;
    public LinkedHashMap<JFXTextField, Pattern> map = new LinkedHashMap<>();
    private Member member;
    private StackPane stackPane;
    private AnchorPane rootPane;
    private final ObservableList<Member> list = FXCollections.observableArrayList();

    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobileNo"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        issueBookCol.setCellValueFactory(new PropertyValueFactory<>("issuedBooks"));

        addPattern();

        try {
            generateMemberId();
            loadAllMembers();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPattern() {
        Pattern patternId = Pattern.compile("^(M)[0-9]{3,6}$");
        Pattern patternName = Pattern.compile("^[A-z. ]{3,30}$");
        Pattern patternEmail = Pattern.compile("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
        Pattern patternMobileNo = Pattern.compile("^(?:7|0|(?:\\+94))[0-9]{9,10}$");
        Pattern patternAddress = Pattern.compile("^[A-z0-9 ,/]{4,30}$");

        map.put(txtMID, patternId);
        map.put(txtMemberName, patternName);
        map.put(txtEmail, patternEmail);
        map.put(txtMobile, patternMobileNo);
        map.put(txtAddress, patternAddress);
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

    private void loadAllMembers() throws ClassNotFoundException, SQLException {
        ResultSet result = CrudUtil.execute("SELECT * FROM Member");
        ObservableList<Member> obList = FXCollections.observableArrayList();

        while (result.next()) {
            obList.add(
                    new Member(
                            result.getString("id"),
                            result.getString("name"),
                            result.getString("email"),
                            result.getString("mobileNo"),
                            result.getString("address"),
                            result.getInt("IssuedBooks")
                    )
            );
        }
        tblMembers.setItems(obList);
    }

    private void disableTextFields(boolean aFlag) {
        txtMID.setDisable(aFlag);
        txtMemberName.setDisable(aFlag);
        txtEmail.setDisable(aFlag);
        txtMobile.setDisable(aFlag);
        txtAddress.setDisable(aFlag);
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
        tblMembers.getSelectionModel().select(0);
        member = tblMembers.getSelectionModel().getSelectedItem();
        retrieveObjectData();
    }

    private void generateMemberId() throws SQLException, ClassNotFoundException {
        String[] parts = getMemberId().split("M");
        //String id = parts[1];
        txtMID.setText("M" + 000 + (Integer.parseInt(parts[1]) + 1));
    }

    public String getMemberId() throws SQLException, ClassNotFoundException {
        ResultSet set = CrudUtil.execute("SELECT Id FROM Member ORDER BY Id DESC LIMIT 1");
        if (set.next()) {
            return set.getString(1);
        } else {
            return "M000";
        }
    }

    public void btnAddOnAction() throws SQLException, ClassNotFoundException {
        if (btnAdd.getText().equalsIgnoreCase("Add New")) {
            changeButtonText(btnAdd);
            clearFields();
        } else {
            Member m = new Member(
                    txtMID.getText(), txtMemberName.getText(), txtEmail.getText(), txtMobile.getText(),
                    txtAddress.getText(), 0, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
            );

            try {
                if (CrudUtil.execute("INSERT INTO Member VALUES (?,?,?,?,?,?,?)", m.getId(), m.getName(), m.getEmail(), m.getMobileNo(), m.getAddress(), m.getIssuedBooks(), m.getDate())) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully saved.").show();
                    loadAllMembers();
                    clearFields();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    private void clearFields() throws SQLException, ClassNotFoundException {
        generateMemberId();
        txtMemberName.clear();
        txtEmail.clear();
        txtMobile.clear();
        txtAddress.clear();

        setDefault(txtMID, txtMemberName, txtEmail, txtMobile, txtAddress);
    }

    private void setDefault(JFXTextField... textFields) {
        for (JFXTextField textField : textFields) {
            textField.setStyle("-fx-text-fill: black");
        }
    }

    public void btnUpdateOnAction(ActionEvent event) {
        if (btnUpdate.getText().equalsIgnoreCase("Edit")) {
            changeButtonText(btnUpdate);
        } else {
            Member m = new Member(
                    txtMID.getText(), txtMemberName.getText(), txtEmail.getText(), txtMobile.getText(),
                    txtAddress.getText(), 0
            );

            try {
                boolean isUpdated = CrudUtil.execute("UPDATE Member SET name=?, email=?, mobileNo=?, address=? WHERE id=?", m.getName(), m.getEmail(), m.getMobileNo(), m.getAddress());
                if (isUpdated) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully updated.").show();
                    loadAllMembers();
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
            deleteMember(txtMID.getText());
        }
    }

    private void deleteMember(String... id) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)) {
            try {
                if (CrudUtil.execute("DELETE FROM Member WHERE id=?", id)) {
                    new Alert(Alert.AlertType.INFORMATION, "Record successfully deleted.").show();
                    loadAllMembers();
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
        if (tblMembers.getSelectionModel().getSelectedItem() != null) {
            ObservableList<Member> deletionMemberList = tblMembers.getSelectionModel().getSelectedItems();
            String[] id = new String[deletionMemberList.size()];
            int i = 0;
            for (Member member : deletionMemberList)
                id[i++] = member.getId();
            deleteMember(id);
        } else
            new Alert(Alert.AlertType.NONE, "Please select record(s)").show();
    }

    public void tableMouseClicked(MouseEvent event) {
        if (!tblMembers.getSelectionModel().isEmpty()) {
            member = tblMembers.getSelectionModel().getSelectedItem();
            retrieveObjectData();
        }
    }

    private void retrieveObjectData() {
        txtMID.setText(String.valueOf(member.getId()));
        txtMemberName.setText(member.getName());
        txtEmail.setText(member.getEmail());
        txtMobile.setText(member.getMobileNo());
        txtAddress.setText(String.valueOf(member.getAddress()));
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
        ResultSet result = CrudUtil.execute("SELECT * FROM Member WHERE id=? OR name=? OR email=? OR mobileNo=? OR address=?", txtSearch.getText(), txtSearch.getText(), txtSearch.getText(), txtSearch.getText(), txtSearch.getText());

        while (result.next()) {
            Member m = new Member(result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getInt(6));
            list.add(m);
        }
        if (list.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No search results.").show();
        }
        tblMembers.setItems(list);
    }

    public void btnRefreshOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        txtSearch.setText("");
        loadAllMembers();
    }

    public void btnPrintAllMembersOnAction(ActionEvent event) {
        try {
            JasperReport compiledReport = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/library/view/reports/PrintAllMembers.jasper"));
            Connection connection = DBConnection.getInstance().getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, null, connection);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (JRException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

