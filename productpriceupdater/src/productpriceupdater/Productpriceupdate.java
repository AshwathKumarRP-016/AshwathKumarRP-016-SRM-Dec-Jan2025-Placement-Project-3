package productpriceupdater;
import java.sql.*;
import java.util.Scanner;

public class Productpriceupdate {

	public static void main(String[] args) {
		
		        Scanner scanner = new Scanner(System.in);
		        
		       
		        String url = "jdbc:mysql://localhost:3306/product_db";
		        String username = "root";
		        String password = "root";
		        Connection connection = null;
		        
		        System.out.println("======================================");
		        System.out.println("     PRODUCT INVENTORY MANAGER        ");
		        System.out.println("======================================");
		        
		        try {
		            
		            Class.forName("com.mysql.cj.jdbc.Driver");
		       
		            connection = DriverManager.getConnection(url, username, password);
		            
		          
		            while (true) {
		                System.out.println("\n------------------------------------");
		                System.out.println("             MAIN MENU                  ");
		                System.out.println("--------------------------------------\n");
		                System.out.println("1. Add New Product");
		                System.out.println("2. Update Product Price");
		                System.out.println("3. View All Products");
		                System.out.println("4. Exit");
		                System.out.print("Choose option (1-4): ");
		                
		                int choice = getValidInt(scanner);
		                
		                switch (choice) {
		                    case 1:
		                        addNewProduct(connection, scanner);
		                        break;
		                    case 2:
		                        updateProductPrice(connection, scanner);
		                        break;
		                    case 3:
		                        viewAllProducts(connection);
		                        break;
		                    case 4:
		                        System.out.println("\n Thank you for using Product Manager!\n");
		                        scanner.close();
		                        if (connection != null) connection.close();
		                        return;
		                    default:
		                        System.out.println("Invalid choice! Please enter 1-4.");
		                }
		            }
		            
		        } catch (ClassNotFoundException e) {
		            System.out.println(" MySQL Driver not found!");
		        } catch (SQLException e) {
		            System.out.println(" Database error: " + e.getMessage());
		        } finally {
		            try {
		                if (connection != null) connection.close();
		            } catch (SQLException e) {
		                System.out.println("Error closing connection: " + e.getMessage());
		            }
		        }
		    }
		    
		    
		    // 1. ADD NEW PRODUCT
		    
		    private static void addNewProduct(Connection connection, Scanner scanner) throws SQLException {
		        System.out.println("\n====================================");
		        System.out.println("         ADD NEW PRODUCT");
		        System.out.println("======================================");
		        
		        scanner.nextLine(); 
		        
		      
		        System.out.print("Enter Product Name: ");
		        String productName = scanner.nextLine().trim();
		        
		        
		        if (productExists(connection, productName)) {
		            System.out.println("\n Product '" + productName + "' already exists!");
		            System.out.println("Use 'Update Product Price' to modify existing products.");
		            return;
		        }
		        
		        
		        double price = 0;
		        while (true) {
		            System.out.print("Enter Product Price: ₹");
		            try {
		                price = scanner.nextDouble();
		                scanner.nextLine(); 
		                
		                if (price < 0) {
		                    System.out.println(" Price cannot be negative!");
		                } else {
		                    break;
		                }
		            } catch (Exception e) {
		                System.out.println(" Invalid price! Please enter numbers only.");
		                scanner.nextLine(); 
		            }
		        }
		        
		        
		        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setString(1, productName);
		        pstmt.setDouble(2, price);
		        
		        int rowsInserted = pstmt.executeUpdate();
		        pstmt.close();
		        
		        if (rowsInserted > 0) {
		            System.out.println("\n PRODUCT ADDED SUCCESSFULLY!");
		            System.out.println("══════════════════════════════════════");
		            System.out.println("Product: " + productName);
		            System.out.println("Price: ₹" + price);
		            System.out.println("══════════════════════════════════════");
		        }
		    }
		    
		    //
		    // 2. UPDATE PRODUCT PRICE
		    
		    private static void updateProductPrice(Connection connection, Scanner scanner) throws SQLException {
		        System.out.println("\n══════════════════════════════════════");
		        System.out.println("       UPDATE PRODUCT PRICE");
		        System.out.println("════════════════════════════════════════");
		        
		        scanner.nextLine(); 
		        
		        
		        System.out.print("Enter Product Name: ");
		        String productName = scanner.nextLine().trim();
		        
		        
		        if (!productExists(connection, productName)) {
		            System.out.println("\n Product '" + productName + "' not found!\n");
		            System.out.println("Use 'Add New Product' to create it first.");
		            return;
		        }
		        
		        
		        double currentPrice = getCurrentPrice(connection, productName);
		        System.out.println("Current Price: ₹" + currentPrice);
		        
		       
		        double newPrice = 0;
		        while (true) {
		            System.out.print("Enter New Price: ₹");
		            try {
		                newPrice = scanner.nextDouble();
		                scanner.nextLine(); 
		                
		                if (newPrice < 0) {
		                    System.out.println(" Price cannot be negative!");
		                } else if (newPrice == currentPrice) {
		                    System.out.println(" New price is same as current price!");
		                } else {
		                    break;
		                }
		            } catch (Exception e) {
		                System.out.println("❌ Invalid price! Please enter numbers only.");
		                scanner.nextLine(); 
		            }
		        }
		        
		       
		        System.out.print("\nUpdate price from ₹" + currentPrice + " to ₹" + newPrice + "? (yes/no): ");
		        String confirm = scanner.nextLine().toLowerCase();
		        
		        if (!confirm.equals("yes") && !confirm.equals("y")) {
		            System.out.println("❌ Price update cancelled.");
		            return;
		        }
		        
		        
		        String sql = "UPDATE products SET price = ? WHERE name = ?";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setDouble(1, newPrice);
		        pstmt.setString(2, productName);
		        
		        int rowsUpdated = pstmt.executeUpdate();
		        pstmt.close();
		        
		        if (rowsUpdated > 0) {
		            System.out.println("\n PRICE UPDATED SUCCESSFULLY!\n");
		            System.out.println("══════════════════════════════════════");
		            System.out.println("Product: " + productName);
		            System.out.println("Old Price: ₹" + currentPrice);
		            System.out.println("New Price: ₹" + newPrice);
		            System.out.println("══════════════════════════════════════");
		            
		            
		            double changePercent = ((newPrice - currentPrice) / currentPrice) * 100;
		            if (changePercent > 0) {
		                System.out.printf(" Price increased by: %.1f%%\n", changePercent);
		            } else {
		                System.out.printf(" Price decreased by: %.1f%%\n", Math.abs(changePercent));
		            }
		        }
		    }
		    
		    
		    // 3. VIEW ALL PRODUCTS
		    
		    private static void viewAllProducts(Connection connection) throws SQLException {
		        System.out.println("\n══════════════════════════════════════════════════════════════");
		        System.out.println("                      ALL PRODUCTS");
		        System.out.println("══════════════════════════════════════════════════════════════");
		        
		        String sql = "SELECT * FROM products ORDER BY id";
		        Statement stmt = connection.createStatement();
		        ResultSet rs = stmt.executeQuery(sql);
		        
		        System.out.printf("%-5s %-25s %-15s %-20s\n", 
		            "ID", "PRODUCT NAME", "PRICE", "LAST UPDATED");
		        System.out.println("══════════════════════════════════════════════════════════════");
		        
		        boolean hasProducts = false;
		        double totalValue = 0;
		        int count = 0;
		        
		        while (rs.next()) {
		            hasProducts = true;
		            count++;
		            
		            int id = rs.getInt("id");
		            String name = rs.getString("name");
		            double price = rs.getDouble("price");
		            String lastUpdated = rs.getTimestamp("last_updated").toString().substring(0, 16);
		            
		            System.out.printf("%-5d %-25s ₹%-14.2f %-20s\n", 
		                id, name, price, lastUpdated);
		            
		            totalValue += price;
		        }
		        
		        if (!hasProducts) {
		            System.out.println("No products found in inventory.");
		        } else {
		            System.out.println("══════════════════════════════════════════════════════════════");
		            System.out.printf("Total Products: %d\n", count);
		            System.out.printf("Total Inventory Value: ₹%.2f\n", totalValue);
		            System.out.printf("Average Price: ₹%.2f\n", totalValue/count);
		            
		            
		            showMostExpensiveProduct(connection);
		        }
		        
		        rs.close();
		        stmt.close();
		    }
		    
		    
		    // Tools
		    
		    // Check if product exists
		    private static boolean productExists(Connection connection, String productName) throws SQLException {
		        String sql = "SELECT COUNT(*) as count FROM products WHERE LOWER(name) = LOWER(?)";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setString(1, productName);
		        ResultSet rs = pstmt.executeQuery();
		        
		        boolean exists = false;
		        if (rs.next() && rs.getInt("count") > 0) {
		            exists = true;
		        }
		        
		        rs.close();
		        pstmt.close();
		        return exists;
		    }
		    
		    // Get current price of product
		    private static double getCurrentPrice(Connection connection, String productName) throws SQLException {
		        String sql = "SELECT price FROM products WHERE LOWER(name) = LOWER(?)";
		        PreparedStatement pstmt = connection.prepareStatement(sql);
		        pstmt.setString(1, productName);
		        ResultSet rs = pstmt.executeQuery();
		        
		        double price = 0;
		        if (rs.next()) {
		            price = rs.getDouble("price");
		        }
		        
		        rs.close();
		        pstmt.close();
		        return price;
		    }
		    
		    // Show most expensive product
		    private static void showMostExpensiveProduct(Connection connection) throws SQLException {
		        String sql = "SELECT name, price FROM products ORDER BY price DESC LIMIT 1";
		        Statement stmt = connection.createStatement();
		        ResultSet rs = stmt.executeQuery(sql);
		        
		        if (rs.next()) {
		            System.out.printf("Most Expensive: %s (₹%.2f)\n", 
		                rs.getString("name"), rs.getDouble("price"));
		        }
		        
		        rs.close();
		        stmt.close();
		    }
		    
		    // Get valid integer input
		    private static int getValidInt(Scanner scanner) {
		        while (true) {
		            try {
		                return scanner.nextInt();
		            } catch (Exception e) {
		                System.out.print("❌ Invalid input! Enter a number (1-4): ");
		                scanner.nextLine(); // Clear invalid input
		            }
		        }
		    }
		}
