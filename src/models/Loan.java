package models;

import java.util.Date;

public class Loan {
    private int id;
    private String userName;
    private int bookId;
    private Date loanDate;
    private Date returnDate;

    // Konstruktor
    public Loan(int id, String userName, int bookId, Date loanDate, Date returnDate) {
        this.id = id;
        this.userName = userName;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    // Getters och setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}