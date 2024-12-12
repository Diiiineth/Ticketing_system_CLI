package org.example;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHandler is responsible for interacting with the database.
 * It handles operations for saving vendor, customer, and ticket data into respective tables.
 */
public class DatabaseHandler {
    private Connection connection;
    /**
     * Constructor: Initializes the database connection.
     */
    public DatabaseHandler() {
        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ticketing_system", // Database URL
                    "root", // Database username
                    "root051038" // Database password
            );
        } catch (SQLException e) {
            // Log error and provide meaningful message
            Logging.log("Error while connecting to the database: " + e.getMessage());
            throw new RuntimeException("Failed to connect to the database. Please check your credentials and database status.", e);
        }
    }

    /**
     * Saves a vendor's information into the vendors table.
     *
     * @param vendor The vendor object to save.
     */
    public void saveVendor(Vendor vendor) {
        String query = "INSERT INTO vendors (name, max_capacity, release_rate) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Set the vendor's name, max capacity, and release rate in the prepared statement
            ps.setString(1, vendor.getVendorName()); // Get vendor name using getter
            ps.setInt(2, vendor.getMaxCapacity()); // Get vendor max capacity
            ps.setInt(3, vendor.getTicketsPerRelease()); // Get vendor tickets per release rate

            // Execute the update to insert the vendor data into the database
            ps.executeUpdate();
            Logging.log("Vendor saved: " + vendor.getVendorName());
        } catch (SQLException e) {
            // Log error with specific details
            Logging.log("Error saving vendor: " + vendor.getVendorName() + " | " + e.getMessage());
        }
    }

    /**
     * Saves a customer's information into the customers table.
     *
     * @param customer The customer object to save.
     */
    public void saveCustomer(Customer customer) {
        String query = "INSERT INTO customers (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Set the customer name in the prepared statement
            ps.setString(1, customer.getCustomerName()); // Get customer name using getter

            // Execute the update to insert the customer data into the database
            ps.executeUpdate();
            Logging.log("Customer saved: " + customer.getCustomerName());
        } catch (SQLException e) {
            // Log error with specific details
            Logging.log("Error saving customer: " + customer.getCustomerName() + " | " + e.getMessage());
        }
    }

    /**
     * Saves a ticket's information into the tickets table.
     *
     * @param ticketName The name of the ticket.
     * @param status     The status of the ticket (e.g., "available" or "sold").
     */
    public void saveTicket(String ticketName, String status) {
        String query = "INSERT INTO tickets (ticket_name, status) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            // Set the ticket name and its status in the prepared statement
            ps.setString(1, ticketName); // Ticket name
            ps.setString(2, status); // Ticket status (available/sold)

            // Execute the update to insert the ticket data into the database
            ps.executeUpdate();
            Logging.log("Ticket saved: " + ticketName + " with status: " + status);
        } catch (SQLException e) {
            // Log error with specific details
            Logging.log("Error saving ticket: " + ticketName + " | " + e.getMessage());
        }
    }

    /**
     * Retrieves all vendors from the database.
     *
     * @return A list of vendor names.
     */
    public List<Vendor> getVendors(TicketPool ticketPool, DatabaseHandler dbHandler) {
        String query = "SELECT name, max_capacity, release_rate FROM vendors";
        List<Vendor> vendors = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Create a Vendor object with data retrieved from the database
                Vendor vendor = new Vendor(
                        ticketPool,
                        10,
                        5000,
                        rs.getInt("max_capacity"),
                        rs.getString("name"),            // Vendor name
                        dbHandler
                );
                vendors.add(vendor); // Add Vendor object to the list
            }
        } catch (SQLException e) {
            Logging.log("Error retrieving vendors: " + e.getMessage());
        }
        return vendors;
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of customer names.
     */
    public List<Customer> getCustomers(TicketPool ticketPool, DatabaseHandler dbHandler) {
        String query = "SELECT name FROM customers";
        List<Customer> customers = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Create a Customer object with the data from the database
                Customer customer = new Customer(ticketPool,1000,rs.getString("name"),dbHandler);
                customers.add(customer); // Add Customer object to the list
            }
        } catch (SQLException e) {
            Logging.log("Error retrieving customers: " + e.getMessage());
        }
        return customers;
    }
}
