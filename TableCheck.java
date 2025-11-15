import java.sql.*;

public class TableCheck {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/yancao?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "zjz1012.";

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 Statement stmt = conn.createStatement()) {
            
            // Check verification_data table structure
            System.out.println("Verification_data Table Structure:");
            ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE verification_data");
            if (rs.next()) {
                System.out.println(rs.getString(2));
            }
            
            // Check columns
            System.out.println("\nColumn List:");
            ResultSetMetaData metaData = stmt.executeQuery("SELECT * FROM verification_data LIMIT 0").getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(metaData.getColumnName(i));
            }
            
            // Get latest records from verification_data
            System.out.println("\nLatest Records (Limited to 10):");
            rs = stmt.executeQuery("SELECT * FROM verification_data ORDER BY verified_time DESC LIMIT 10");
            
            // Print column names
            System.out.printf("%-10s %-20s %-20s %-30s %-15s %-10s %-20s\n", 
                            "ID", "BATCH_ID", "BRAND", "SEGMENT", "DATA_COUNT", "OPERATOR_ID", "VERIFIED_TIME");
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            
            // Print data
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s %-30s %-15d %-10d %-20s\n", 
                                rs.getLong("id"),
                                rs.getString("batch_id"),
                                rs.getString("brand"),
                                rs.getString("segment"),
                                rs.getInt("data_count"),
                                rs.getInt("operator_id"),
                                rs.getTimestamp("verified_time"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}