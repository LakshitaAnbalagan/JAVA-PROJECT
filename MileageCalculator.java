import java.sql.*;
import java.util.Scanner;

public class MileageCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Mileage Calculator!");

        try {
            // Vehicle type options
            System.out.println("Select the vehicle type:");
            System.out.println("1. Car");
            System.out.println("2. Bike");
            System.out.println("3. Truck");
            System.out.println("4. Bus");
            System.out.print("Enter the number corresponding to your vehicle type: ");
            int vehicleOption = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            // Get vehicle type from option
            String vehicleType = getVehicleType(vehicleOption);
            if (vehicleType == null) {
                throw new IllegalArgumentException("Invalid vehicle type selected.");
            }

            System.out.print("Enter the distance traveled (in kilometers): ");
            double distance = scanner.nextDouble();

            System.out.print("Enter the fuel used (in liters): ");
            double fuel = scanner.nextDouble();

            System.out.print("Enter the fuel price (per liter): ");
            double fuelPrice = scanner.nextDouble();

            // Validate inputs
            if (distance <= 0 || fuel <= 0 || fuelPrice <= 0) {
                throw new IllegalArgumentException("Distance, fuel, and fuel price must be positive values.");
            }

            // Thread to simulate loading delay
            Thread calculationThread = new Thread(() -> {
                try {
                    System.out.println("Calculating mileage...");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("Calculation interrupted.");
                }
            });

            calculationThread.start();
            calculationThread.join();

            double mileage = calculateMileage(distance, fuel);
            double costPerKm = calculateCostPerKm(fuelPrice, mileage);

            System.out.printf("Vehicle Type: %s\n", vehicleType);
            System.out.printf("Your vehicle's mileage is: %.2f km/l\n", mileage);
            System.out.printf("The cost per kilometer is: %.2f currency units\n", costPerKm);

            // Connect to MySQL and insert data
            insertDataToDatabase(vehicleType, distance, fuel, fuelPrice, mileage, costPerKm);

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter numbers only.");
        } finally {
            scanner.close();
        }
    }

    // Method to get vehicle type from the selected option
    public static String getVehicleType(int option) {
        switch (option) {
            case 1: return "Car";
            case 2: return "Bike";
            case 3: return "Truck";
            case 4: return "Bus";
            default: return null;
        }
    }

    // Method to calculate mileage
    public static double calculateMileage(double distance, double fuel) {
        return distance / fuel;
    }

    // Method to calculate cost per kilometer
    public static double calculateCostPerKm(double fuelPrice, double mileage) {
        return fuelPrice / mileage;
    }

    // Method to insert data into MySQL
    public static void insertDataToDatabase(String vehicleType, double distance, double fuel, double fuelPrice, double mileage, double costPerKm) {
        String url = "jdbc:mysql://localhost:3306/mileage_db";
        String username = "root";
        String password = "14-Feb-05";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String query = "INSERT INTO mileage (vehicle_type, distance, fuel, fuel_price, mileage, cost_per_km) " +
                           "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, vehicleType);
            statement.setDouble(2, distance);
            statement.setDouble(3, fuel);
            statement.setDouble(4, fuelPrice);
            statement.setDouble(5, mileage);
            statement.setDouble(6, costPerKm);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully into the database.");
            }

            connection.close();

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
