import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/arenaedge";
        String user = "root";  // Replace with your MySQL username
        String password = "Nirmal@123";  // Replace with your MySQL password
        
        try {
            System.out.println("Connecting to MySQL...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            Connection conn = DriverManager.getConnection(url, user, password);
            
            if (conn != null) {
                System.out.println("MySQL connection successful!");
                
                // Create a test table to verify write access
                Statement stmt = conn.createStatement();
                
                // Try a simple query
                ResultSet rs = stmt.executeQuery("SHOW TABLES");
                System.out.println("Tables in database:");
                while (rs.next()) {
                    System.out.println("- " + rs.getString(1));
                }
                
                conn.close();
                System.out.println("Connection closed successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error connecting to MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}