import java.io.*;
import java.util.*;

// Vehicle.java
class Vehicle {
    private String number;
    private String type;
    private String status;
    private String floor;

    public Vehicle(String number, String type, String status, String floor) {
        this.number = number;
        this.type = type;
        this.status = status;
        this.floor = floor;
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getFloor() { return floor; }

    public void setStatus(String status) { this.status = status; }
    public void setFloor(String floor) { this.floor = floor; }

    @Override
    public String toString() {
        return number + "," + type + "," + status + "," + floor;
    }

    public static Vehicle fromString(String data) {
        String[] parts = data.split(",");
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid vehicle data: " + data);
        }
        return new Vehicle(parts[0], parts[1], parts[2], parts[3]);
    }
}

// TransportManager.java
class TransportManager {
    private final String FILE = "data/vehicles.txt";
    private List<Vehicle> vehicles = new ArrayList<>();

    public TransportManager() {
        File file = new File(FILE);
        file.getParentFile().mkdirs(); // Ensure data folder exists
        try {
            file.createNewFile(); // Create file if it doesn't exist
        } catch (IOException e) {
            System.out.println("Could not initialize data file.");
        }
        loadVehicles();
    }

    public void loadVehicles() {
        vehicles.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    vehicles.add(Vehicle.fromString(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    public void saveVehicles() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
            for (Vehicle v : vehicles) {
                writer.println(v);
            }
        } catch (IOException e) {
            System.out.println("Error saving vehicles: " + e.getMessage());
        }
    }

    public void addVehicle(Vehicle v) {
        vehicles.add(v);
        saveVehicles();
    }

    public void viewVehicles() {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            return;
        }
        for (Vehicle v : vehicles) {
            System.out.println("Number: " + v.getNumber() +
                               ", Type: " + v.getType() +
                               ", Status: " + v.getStatus() +
                               ", Floor: " + v.getFloor());
        }
    }

    public Vehicle searchVehicle(String number) {
        for (Vehicle v : vehicles) {
            if (v.getNumber().equalsIgnoreCase(number)) {
                return v;
            }
        }
        return null;
    }

    public void removeVehicle(String number) {
        boolean removed = vehicles.removeIf(v -> v.getNumber().equalsIgnoreCase(number));
        if (removed) {
            saveVehicles();
            System.out.println("Vehicle removed successfully.");
        } else {
            System.out.println("Vehicle not found.");
        }
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        TransportManager manager = new TransportManager();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Transport Management System ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View Vehicles");
            System.out.println("3. Search Vehicle");
            System.out.println("4. Remove Vehicle");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            while (!sc.hasNextInt()) {
                System.out.print("Invalid input. Please enter a number: ");
                sc.next();
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter Vehicle Number: ");
                    String num = sc.nextLine();
                    System.out.print("Enter Vehicle Type: ");
                    String type = sc.nextLine();
                    System.out.print("Enter Status (Available/On Duty/Maintenance): ");
                    String status = sc.nextLine();
                    System.out.print("Enter Floor (e.g., Ground, 1st, 2nd): ");
                    String floor = sc.nextLine();
                    manager.addVehicle(new Vehicle(num, type, status, floor));
                    break;
                case 2:
                    manager.viewVehicles();
                    break;
                case 3:
                    System.out.print("Enter Vehicle Number to Search: ");
                    Vehicle found = manager.searchVehicle(sc.nextLine());
                    if (found != null) {
                        System.out.println("Found: Number: " + found.getNumber() +
                                           ", Type: " + found.getType() +
                                           ", Status: " + found.getStatus() +
                                           ", Floor: " + found.getFloor());
                    } else {
                        System.out.println("Vehicle not found.");
                    }
                    break;
                case 4:
                    System.out.print("Enter Vehicle Number to Remove: ");
                    manager.removeVehicle(sc.nextLine());
                    break;
                case 0:
                    System.out.println("Exiting system...");
                    break;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        } while (choice != 0);

        sc.close();
    }
}
