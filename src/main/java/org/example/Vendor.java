package org.example;


public class Vendor implements Runnable {
    private final TicketPool ticketPool;          // The shared ticket pool
    private final int ticketsPerRelease;          // Number of tickets the vendor releases at a time
    private final int releaseInterval;            // Time interval (ms) between ticket releases
    private final int maxCapacity;                // Maximum ticket capacity of the pool
    private final String vendorName;              // Name of the vendor
    private final DatabaseHandler dbHandler;      // Database handler to save ticket data

    /**
     * Constructor to initialize a Vendor object.
     */
    public Vendor(TicketPool ticketPool, int ticketsPerRelease, int releaseInterval, int maxCapacity, String vendorName, DatabaseHandler dbHandler) {
        this.ticketPool = ticketPool;
        this.ticketsPerRelease = ticketsPerRelease;
        this.releaseInterval = releaseInterval;
        this.maxCapacity = maxCapacity;
        this.vendorName = vendorName;
        this.dbHandler = dbHandler;
    }

    public TicketPool getTicketPool() {
        return ticketPool;
    }

    public int getTicketsPerRelease() {
        return ticketsPerRelease;
    }

    public int getReleaseInterval() {
        return releaseInterval;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getVendorName() {
        return vendorName;
    }

    public DatabaseHandler getDbHandler() {
        return dbHandler;
    }

    /**
     * The run method defines the vendor's actions for ticket releases.
     */
    @Override
    public void run() {
        try {
            // Vendor keeps adding tickets while the thread is not interrupted
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < ticketsPerRelease; i++) {
                    // Check if the ticket pool is not full
                    if (ticketPool.getTicketCount() < maxCapacity) {
                        String ticketName = "Ticket-" + System.currentTimeMillis(); // Generate a unique ticket name
                        if (ticketPool.addTicket(ticketName)) {  // Try to add the ticket to the pool
                            dbHandler.saveTicket(ticketName, "available"); // Save the ticket status in the database
                            System.out.println(vendorName + " added a ticket: " + ticketName); // Log the action
                        }
                    } else {
                        // If the pool is full, stop adding more tickets for now
                        System.out.println(vendorName + ": Ticket pool is full!");
                        break;
                    }
                }
                Thread.sleep(releaseInterval);  // Wait for the specified interval before releasing more tickets
            }
        } catch (InterruptedException e) {
            // If the thread is interrupted, restore the interrupt status and stop gracefully
            Thread.currentThread().interrupt(); // Restore the interrupt status
        } catch (Exception e) {
            // Catch any unexpected errors during execution
            System.err.println("Unexpected error occurred in " + vendorName + ": " + e.getMessage());
            e.printStackTrace(); // Print the stack trace for debugging purposes
        }
    }
}
