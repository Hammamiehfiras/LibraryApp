package services;

import database.DatabaseConnection;
import models.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanManager {

    // Låna en bok
    public boolean loanBook(int bookId, String userName) {
        if (!bookExists(bookId)) {
            System.out.println("Ogiltigt bok-ID.");
            return false;
        }

        String checkQuery = "SELECT available FROM books WHERE id = ?";
        String insertLoanQuery = "INSERT INTO loans (user_name, book_id, loan_date) VALUES (?, ?, NOW())";
        String updateBookQuery = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getBoolean("available")) {
                // Bok finns och är tillgänglig
                try (PreparedStatement insertStmt = conn.prepareStatement(insertLoanQuery);
                     PreparedStatement updateStmt = conn.prepareStatement(updateBookQuery)) {
                    // Skapa lån
                    insertStmt.setString(1, userName);
                    insertStmt.setInt(2, bookId);  // Här sparas bok-ID korrekt
                    insertStmt.executeUpdate();

                    // Uppdatera bokens tillgänglighet
                    updateStmt.setBoolean(1, false);
                    updateStmt.setInt(2, bookId);
                    updateStmt.executeUpdate();

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Bok är inte tillgänglig eller annat fel
    }

    // Lämna tillbaka en bok
    public boolean returnBook(int loanId, String userName) {
        String selectLoanQuery = "SELECT id, book_id FROM loans WHERE id = ? AND user_name = ? AND return_date IS NULL";
        String updateLoanQuery = "UPDATE loans SET return_date = NOW() WHERE id = ?";
        String updateBookQuery = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectLoanQuery)) {
            selectStmt.setInt(1, loanId);
            selectStmt.setString(2, userName);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int bookId = rs.getInt("book_id");

                // Uppdatera lån för att sätta tillbaka det
                try (PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanQuery);
                     PreparedStatement updateBookStmt = conn.prepareStatement(updateBookQuery)) {
                    updateLoanStmt.setInt(1, loanId);
                    updateLoanStmt.executeUpdate();

                    // Återställ bokens tillgänglighet
                    updateBookStmt.setBoolean(1, true);
                    updateBookStmt.setInt(2, bookId);
                    updateBookStmt.executeUpdate();

                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Bok är inte lånad eller något gick fel
    }

    // Lista alla lånade böcker för en användare
    public List<Loan> listUserLoans(String userName) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE user_name = ? AND return_date IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id"),
                        rs.getString("user_name"),
                        rs.getInt("book_id"),
                        rs.getTimestamp("loan_date"),
                        rs.getTimestamp("return_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Kontrollera om en bok finns
    public boolean bookExists(int bookId) {
        String query = "SELECT id FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();  // Returnerar true om boken finns
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}