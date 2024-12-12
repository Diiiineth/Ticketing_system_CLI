package org.example;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The TicketPool class manages the pool of tickets. It supports adding, retrieving tickets,
 * and checking the current size of the ticket pool.
 */
public class TicketPool {
    private final int maxCapacity;               // Maximum capacity of the pool
    private final BlockingQueue<String> tickets; // Blocking queue to hold the tickets

    /**
     * Constructor to initialize the ticket pool with a given maximum capacity.
     *
     * @param maxCapacity The maximum capacity of the ticket pool.
     */
    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.tickets = new LinkedBlockingQueue<>(maxCapacity);
    }

    /**
     * Adds a ticket to the pool if there is available capacity.
     * This method ensures that the operation is thread-safe and atomic.
     *
     * @param ticket The ticket to be added.
     * @return True if the ticket was added successfully, false otherwise.
     */
    public synchronized boolean addTicket(String ticket) {
        try {
            if (tickets.size() < maxCapacity) { // Ensure capacity logic is atomic
                boolean added = tickets.offer(ticket);
                if (added) {
                    System.out.println("Ticket added: " + ticket + " | Current Size: " + tickets.size());
                }
                return added;
            }
            return false; // Return false if the pool is full
        } catch (Exception e) {
            System.err.println("Error adding ticket: " + ticket + " | Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a ticket from the pool. If no tickets are available, it returns null.
     * The method is thread-safe.
     *
     * @return The retrieved ticket, or null if no tickets are available.
     */
    public synchronized String retrieveTicket() {
        try {
            String ticket = tickets.poll();
            if (ticket != null) {
                System.out.println("Ticket retrieved: " + ticket + " | Current Size: " + tickets.size());
            }
            return ticket;
        } catch (Exception e) {
            System.err.println("Error retrieving ticket: " + e.getMessage());
            return null; // Return null if there's an error
        }
    }

    /**
     * Returns the current count of tickets in the pool. This method is thread-safe.
     *
     * @return The number of tickets currently in the pool.
     */
    public synchronized int getTicketCount() {
        try {
            return tickets.size(); // Thread-safe for monitoring the current ticket count
        } catch (Exception e) {
            System.err.println("Error getting ticket count: " + e.getMessage());
            return 0; // Return 0 if there's an error
        }
    }

    /**
     * Returns the maximum capacity of the ticket pool.
     *
     * @return The maximum capacity of the pool.
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}
