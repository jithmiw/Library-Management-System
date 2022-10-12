package library.model;

import com.jfoenix.controls.JFXButton;

import java.sql.Date;

public class Member {
    private String id;
    private String name;
    private String email;
    private String mobileNo;
    private String address;
    private int issuedBooks;
    private Date date;
    private JFXButton onAction;

    public Member() {
    }

    public Member(String id, String name, String email, String mobileNo, String address, int issuedBooks) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.address = address;
        this.issuedBooks = issuedBooks;
    }

    public Member(String id, String name, String email, String mobileNo, String address, int issuedBooks, Date date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobileNo = mobileNo;
        this.address = address;
        this.issuedBooks = issuedBooks;
        this.date = date;
        onAction = new JFXButton();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIssuedBooks() {
        return issuedBooks;
    }

    public void setIssuedBooks(int issuedBooks) {
        this.issuedBooks = issuedBooks;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JFXButton getOnAction() {
        return onAction;
    }

    public void setOnAction(JFXButton onAction) {
        this.onAction = onAction;
    }
}
