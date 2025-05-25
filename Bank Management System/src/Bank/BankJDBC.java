package Bank;
import java.sql.*;
import java.nio.file.*;
import java.io.*;

public class BankJDBC {
    static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    static final String USER = "MySQL80";
    static final String PASS = "ascmmmmcsa131216*mysql";

    // Connect to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Run SQL file (schema + initial data)
    public static void runSQLFile(String filePath) {
        try {
            String sql = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] queries = sql.split(";");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASS);
                 Statement stmt = conn.createStatement()) {

                for (String query : queries) {
                    query = query.trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                }
                System.out.println("SQL schema executed successfully.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new account
    public static void createAccount(String name, String type, double balance) {
        String sql = "INSERT INTO accounts (name, account_type, balance) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setDouble(3, balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Deposit
    public static void deposit(int accountId, double amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Withdraw
    public static void withdraw(int accountId, double amount) {
        String checkSql = "SELECT balance FROM accounts WHERE account_id = ?";
        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance >= amount) {
                    updateStmt.setDouble(1, amount);
                    updateStmt.setInt(2, accountId);
                    updateStmt.executeUpdate();
                } else {
                    System.out.println("Insufficient funds.");
                }
            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View account
    public static void viewAccount(int accountId) {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Account ID: " + rs.getInt("account_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Type: " + rs.getString("account_type"));
                System.out.println("Balance: " + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
