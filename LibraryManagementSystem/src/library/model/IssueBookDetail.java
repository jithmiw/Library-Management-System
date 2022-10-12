package library.model;

import com.jfoenix.controls.JFXButton;

import java.sql.Date;

public class IssueBookDetail {
    private String issueId;
    private String bookId;
    private String memberId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private double fine;
    private JFXButton action;

    public IssueBookDetail() {
    }

    public IssueBookDetail(String issueId, String bookId, String memberId, Date issueDate, Date dueDate, Date returnDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public IssueBookDetail(String issueId, String bookId, String memberId, Date issueDate, Date dueDate, Date returnDate, double fine, JFXButton action) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.action = action;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public JFXButton getAction() {
        return action;
    }

    public void setAction(JFXButton action) {
        this.action = action;
    }
}
