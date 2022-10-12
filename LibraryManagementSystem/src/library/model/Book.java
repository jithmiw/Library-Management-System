package library.model;

import com.jfoenix.controls.JFXButton;

import java.util.Date;

public class Book {
    private String id;
    private String name;
    private String author;
    private String publisher;
    private String description;
    private int stock;
    private Date date;
    private JFXButton action;

    public Book() {
    }

    public Book(String id, String name, String author, String publisher, String description, int stock) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.stock = stock;
    }

    public Book(String id, String name, String author, String publisher, String description, int stock, Date date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.stock = stock;
        this.date = date;
    }

    public Book(String id, String name, String author, String publisher, JFXButton action) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.publisher = publisher;
        this.action = action;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JFXButton getAction() {
        return action;
    }

    public void setAction(JFXButton action) {
        this.action = action;
    }
}
