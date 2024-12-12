package org.example;


public class Customer implements Runnable {
    private final TicketPool ticketPool;           // The shared ticket pool
    private final int retrievalInterval;           // Time interval (ms) between ticket retrievals
    private final String customerName;             // Name of the customer
    private final DatabaseHandler dbHandler;       // Database handler to save ticket data

    /**
     * Constructor to initialize a Customer object.

     */
    public Customer(TicketPool ticketPool, int retrievalInterval, String customerName, DatabaseHandler dbHandler) {
        this.ticketPool = ticketPool;
        this.retrievalInterval = retrievalInterval;
        this.customerName = customerName;
        this.dbHandler = dbHandler;
    }

    public TicketPool getTicketPool() {
        return ticketPool;
    }

    public int getRetrievalInterval() {
        return retrievalInterval;
    }

    public String getCustomerName() {
        return customerName;
    }

    public DatabaseHandler getDbHandler() {
        return dbHandler;
    }

    /**
     * The run method defines the customer's actions for retrieving tickets.
     */
    @Override
    public void run() {
        try {
            // Customer keeps retrieving tickets while the thread is not interrupted
            while (!Thread.currentThread().isInterrupted()) {
                // Try to retrieve a ticket from the ticket pool
                String ticket = ticketPool.retrieveTicket();
                if (ticket != null) {
                    dbHandler.saveTicket(ticket, "sold"); // Save the ticket status in the database as "sold"
                    System.out.println(customerName + " retrieved: " + ticket); // Log the action
                } else {
                    System.out.println("No tickets available for " + customerName + "!"); // Log when no tickets are available
                }
                Thread.sleep(retrievalInterval); // Wait for the specified interval before attempting to retrieve again
            }
        } catch (InterruptedException e) {
            // If the thread is interrupted, restore the interrupt status and stop gracefully
            Thread.currentThread().interrupt(); // Restore the interrupt status
        } catch (Exception e) {
            // Catch any unexpected errors during execution
            System.err.println("Unexpected error occurred for " + customerName + ": " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }
    }
}
