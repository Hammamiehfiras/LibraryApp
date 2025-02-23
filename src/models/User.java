package models;

public class User {
    private int id;
    private String userName;
    private String role;  // ADMIN eller CUSTOMER

    // Konstruktor
    public User(int id, String userName, String role) {
        this.id = id;
        this.userName = userName;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}