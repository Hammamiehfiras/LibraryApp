import services.BookManager;
import services.LoanManager;
import services.UserManager;
import models.Book;
import models.Loan;
import models.User;

import java.util.List;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookManager bookManager = new BookManager();
        LoanManager loanManager = new LoanManager();
        UserManager userManager = new UserManager();

        System.out.print("Ange ditt användarnamn: ");
        String userName = scanner.nextLine();
        User currentUser = userManager.getUser(userName);

        if (currentUser == null) {
            System.out.println("Användaren finns inte. Vill du skapa en ny användare? (ja/nej)");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("ja")) {
                System.out.print("Ange roll (ADMIN/CUSTOMER): ");
                String role = scanner.nextLine();
                userManager.createUser(userName, role);
                currentUser = userManager.getUser(userName);
            } else {
                System.out.println("Avslutar...");
                return;
            }
        }

        while (true) {
            if (currentUser.getRole().equals("ADMIN")) {
                System.out.println("\nVälkommen Admin");
                System.out.println("1. Lägg till bok");
                System.out.println("2. Ta bort bok");
                System.out.println("3. Lista alla böcker");
                System.out.println("4. Lista alla användare");
                System.out.println("5. Ta bort användare");
                System.out.println("6. Avsluta");
            } else {
                System.out.println("\nVälkommen Kund");
                System.out.println("1. Lista böcker");
                System.out.println("2. Låna en bok");
                System.out.println("3. Lämna tillbaka en bok");
                System.out.println("4. Lista mina lån");
                System.out.println("5. Sök böcker");
                System.out.println("6. Avsluta");
            }

            int choice = scanner.nextInt();
            scanner.nextLine();  // För att läsa bort newline-karaktären

            switch (choice) {
                case 1:
                    if (currentUser.getRole().equals("ADMIN")) {
                        System.out.print("Ange titel: ");
                        String title = scanner.nextLine();
                        System.out.print("Ange författare: ");
                        String author = scanner.nextLine();
                        System.out.print("Ange kategori: ");
                        String category = scanner.nextLine();
                        bookManager.addBook(new Book(0, title, author, category, true));
                        System.out.println("Bok tillagd!");
                    } else {
                        List<Book> books = bookManager.listBooks();
                        for (Book book : books) {
                            System.out.println(book.getId() + ". " + book.getTitle() + " - " + (book.isAvailable() ? "Tillgänglig" : "Utlånad"));
                        }
                    }
                    break;

                case 2:
                    if (currentUser.getRole().equals("ADMIN")) {
                        System.out.print("Ange bok-ID för att ta bort: ");
                        int bookId = scanner.nextInt();
                        scanner.nextLine();
                        if (bookManager.deleteBook(bookId)) {
                            System.out.println("Bok borttagen!");
                        } else {
                            System.out.println("Boken kunde inte tas bort.");
                        }
                    } else {
                        // Lista tillgängliga böcker
                        List<Book> availableBooks = bookManager.listBooks().stream()
                                .filter(Book::isAvailable)
                                .toList();

                        if (availableBooks.isEmpty()) {
                            System.out.println("Inga tillgängliga böcker just nu.");
                        } else {
                            System.out.println("Tillgängliga böcker:");
                            for (int i = 0; i < availableBooks.size(); i++) {
                                Book book = availableBooks.get(i);
                                System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor());
                            }

                            // Be användaren välja en bok
                            System.out.print("Välj en bok att låna (ange listnummer): ");
                            int bookChoice = scanner.nextInt();
                            scanner.nextLine();  // För att läsa bort newline-karaktären

                            if (bookChoice > 0 && bookChoice <= availableBooks.size()) {
                                Book selectedBook = availableBooks.get(bookChoice - 1);
                                if (loanManager.loanBook(selectedBook.getId(), currentUser.getUserName())) {
                                    System.out.println("Bok lånad: " + selectedBook.getTitle());
                                } else {
                                    System.out.println("Boken kunde inte lånas.");
                                }
                            } else {
                                System.out.println("Ogiltigt val.");
                            }
                        }
                    }
                    break;

                case 3:
                    if (currentUser.getRole().equals("ADMIN")) {
                        List<Book> books = bookManager.listBooks();
                        for (Book book : books) {
                            System.out.println(book.getId() + ". " + book.getTitle() + " - " + (book.isAvailable() ? "Tillgänglig" : "Utlånad"));
                        }
                    } else {
                        // Lista användarens lånade böcker
                        List<Loan> userLoans = loanManager.listUserLoans(currentUser.getUserName());

                        if (userLoans.isEmpty()) {
                            System.out.println("Du har inga lånade böcker.");
                        } else {
                            System.out.println("Dina lånade böcker:");
                            for (int i = 0; i < userLoans.size(); i++) {
                                Loan loan = userLoans.get(i);
                                Book book = bookManager.listBooks().stream()
                                        .filter(b -> b.getId() == loan.getBookId())
                                        .findFirst()
                                        .orElse(null);
                                if (book != null) {
                                    System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor() + " (Lån-ID: " + loan.getId() + ")");
                                }
                            }

                            // Be användaren välja ett lån att lämna tillbaka
                            System.out.print("Välj ett lån att lämna tillbaka (ange listnummer): ");
                            int returnChoice = scanner.nextInt();
                            scanner.nextLine();  // För att läsa bort newline-karaktären

                            if (returnChoice > 0 && returnChoice <= userLoans.size()) {
                                Loan selectedLoan = userLoans.get(returnChoice - 1);
                                if (loanManager.returnBook(selectedLoan.getId(), currentUser.getUserName())) {
                                    System.out.println("Bok återlämnad: " + selectedLoan.getBookId());
                                } else {
                                    System.out.println("Boken kunde inte återlämnas.");
                                }
                            } else {
                                System.out.println("Ogiltigt val.");
                            }
                        }
                    }
                    break;

                case 4:
                    if (currentUser.getRole().equals("ADMIN")) {
                        List<User> users = userManager.listUsers();
                        for (User user : users) {
                            System.out.println(user.getId() + ". " + user.getUserName() + " - " + user.getRole());
                        }
                    } else {
                        List<Loan> loans = loanManager.listUserLoans(currentUser.getUserName());
                        if (loans.isEmpty()) {
                            System.out.println("Inga lån hittades.");
                        } else {
                            for (Loan loan : loans) {
                                System.out.println("Lånat bok-ID: " + loan.getBookId() + ", Lånedatum: " + loan.getLoanDate());
                            }
                        }
                    }
                    break;

                case 5:
                    if (currentUser.getRole().equals("ADMIN")) {
                        System.out.print("Ange användarnamn för att ta bort: ");
                        String userToDelete = scanner.nextLine();
                        if (userManager.deleteUser(userToDelete)) {
                            System.out.println("Användare borttagen!");
                        } else {
                            System.out.println("Användaren kunde inte tas bort.");
                        }
                    } else {
                        System.out.print("Ange sökterm: ");
                        String searchTerm = scanner.nextLine();
                        List<Book> books = bookManager.searchBooks(searchTerm);
                        for (Book book : books) {
                            System.out.println(book.getId() + ". " + book.getTitle() + " - " + (book.isAvailable() ? "Tillgänglig" : "Utlånad"));
                        }
                    }
                    break;

                case 6:
                    System.out.println("Avslutar...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Ogiltigt val, försök igen.");
            }
        }
    }
}