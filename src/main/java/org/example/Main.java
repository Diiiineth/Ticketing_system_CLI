package org.example;


import java.util.Scanner;

public class Main {
    private static volatile boolean running = true; // Flag to control the main loop execution
    private static Thread ticketingSystemThread;    // Thread to manage the ticketing system
    private static Thread keyListenerThread;       // Thread to listen for user key press

    public static void main(String[] args) {
        // Initialize scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Step 1: Configure the system
        System.out.println("Welcome to the Ticketing System!");

        // Get the maximum ticket pool capacity with validation
        System.out.print("Enter the maximum ticket pool capacity: ");
        int maxTicketCapacity = getPositiveInt(scanner);

        // Step 2: Initialize components for managing the ticketing system
        TicketingSystemManager manager = new TicketingSystemManager(maxTicketCapacity);

        // Step 3: Start command-based controls for user interaction
        while (running) {
            // Display available commands to the user
            System.out.println("\nCommands: ");
            System.out.println("1. Add Vendor");
            System.out.println("2. Add Customer");
            System.out.println("3. Start System(Press any letter key along the enter key to stop)");
            System.out.println("4. Exit");
            System.out.print("Enter your command: ");

            if (!running) break; // Ensure the loop terminates if `running` is set to false
            int command = getPositiveInt(scanner); // Read and validate the command input

            // Handle the user command
            switch (command) {
                case 1:
                    // Add a new vendor to the system
                    System.out.print("Enter vendor name: ");
                    String vendorName = scanner.next();
                    manager.addVendor(vendorName, 5, 2000); // Start vendor thread with default ticket release rate
                    break;

                case 2:
                    // Add a new customer to the system
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.next();
                    manager.addCustomer(customerName, 2000); // Start customer thread with default retrieval rate
                    break;

                case 3:
                    // Start the system in a separate thread
                    if (ticketingSystemThread == null || !ticketingSystemThread.isAlive()) {
                        ticketingSystemThread = new Thread(manager::startSystem); // Lambda to run startSystem
                        ticketingSystemThread.start();

                        // Start a thread to listen for key presses
                        startKeyListener(manager);
                    } else {
                        System.out.println("System is already running.");
                    }
                    break;

                case 4:
                    // Exit the program
                    System.exit(0);
                    break;

                default:
                    // Handle invalid command input
                    System.out.println("Invalid command. Please try again.");
            }
        }

        // Close the scanner to release resources
        scanner.close();
    }

    /**
     * Stops the ticketing system and interrupts any active threads.
     *
     * @param manager The TicketingSystemManager instance to stop.
     */
    private static void stopSystem(TicketingSystemManager manager) {
        manager.stopSystem(); // Gracefully stop vendors and customers
        running = false;      // Set the running flag to false to exit loops

        // Interrupt the ticketing system thread if it's running
        if (ticketingSystemThread != null && ticketingSystemThread.isAlive()) {
            ticketingSystemThread.interrupt();
        }

        // Interrupt the key listener thread if it's running
        if (keyListenerThread != null && keyListenerThread.isAlive()) {
            keyListenerThread.interrupt();
        }

        System.exit(0); // Terminate the program completely
    }

    /**
     * Starts a thread to listen for any key press and stops the system if detected.
     *
     * @param manager The TicketingSystemManager instance to stop.
     */
    private static void startKeyListener(TicketingSystemManager manager) {
        keyListenerThread = new Thread(() -> {
            Scanner inputScanner = new Scanner(System.in); // New Scanner for detecting key press
            System.out.println("Press any key followed by Enter to stop the system...");
            inputScanner.nextLine(); // Wait for user input
            System.out.println("Key detected. System Stopped");
            stopSystem(manager); // Trigger system shutdown
            inputScanner.close(); // Clean up resources
            System.exit(0); // Ensure program exits completely
        });
        keyListenerThread.start(); // Start the key listener thread
    }

    /**
     * Utility method to get a positive integer from the user, with error handling.
     *
     * @param scanner The scanner instance to read user input.
     * @return A positive integer value entered by the user.
     */
    private static int getPositiveInt(Scanner scanner) {
        int value;
        while (true) {
            try {
                // Read input and attempt to parse it as an integer
                value = Integer.parseInt(scanner.nextLine());

                // Ensure the value is positive
                if (value > 0) {
                    break;
                } else {
                    System.out.print("Please enter a positive integer: ");
                }
            } catch (NumberFormatException e) {
                // Handle invalid input (non-integer values)
                System.out.print("Invalid input. Please enter a positive integer: ");
            }
        }
        return value;
    }
}
