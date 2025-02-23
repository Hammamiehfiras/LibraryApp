package services;

import database.DatabaseConnection;
import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookManager {

    // Lägga till en ny bok i biblioteket
    public void addBook(Book book) {
        String query = "INSERT INTO books (title, author, category, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setBoolean(4, book.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ta bort en bok baserat på ID
    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Ta bort en bok baserat på titel
    public boolean deleteBookByTitle(String title) {
        String query = "DELETE FROM books WHERE title = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lista alla böcker
    public List<Book> listBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Sök böcker efter författare eller titel
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}