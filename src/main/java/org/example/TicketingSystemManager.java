package org.example;


import java.util.List;
import java.util.concurrent.*;

public class TicketingSystemManager {
    private final TicketPool ticketPool;
    private final DatabaseHandler dbHandler;
    private final ExecutorService vendorExecutor; // Executor for vendor threads
    private final ExecutorService customerExecutor; // Executor for customer threads
    private volatile boolean isRunning;
    private final List<Runnable> activeVendors;
    private final List<Runnable> activeCustomers;

    // Constructor to initialize the manager with max ticket capacity
    public TicketingSystemManager(int maxTicketCapacity) {
        this.ticketPool = new TicketPool(maxTicketCapacity);
        this.dbHandler = new DatabaseHandler();
        this.vendorExecutor = Executors.newFixedThreadPool(30);
        this.customerExecutor = Executors.newFixedThreadPool(30);
        this.isRunning = false;
        this.activeVendors = new CopyOnWriteArrayList<>();
        this.activeCustomers = new CopyOnWriteArrayList<>();
    }
    /**
     * Starts the system with vendors and customers from the database.
     */
    public void startSystem() {
        if (isRunning) {
            System.out.println("System is already running.");
            return;
        }
        isRunning = true;

        // Fetch vendors from the database and start them
        List<Vendor> vendors = dbHandler.getVendors(ticketPool, dbHandler);
        for (Vendor vendor : vendors) {
            activeVendors.add(vendor);
            vendorExecutor.submit(vendor);
        }

        // Fetch customers from the database and start them
        List<Customer> customers = dbHandler.getCustomers(ticketPool, dbHandler);
        for (Customer customer : customers) {
            activeCustomers.add(customer);
            customerExecutor.submit(customer);
        }

        System.out.println("System started with vendors and customers from the database.");
    }

    /**
     * Gracefully stops the system without shutting down executor services.
     */
    public void stopSystem() {
        if (!isRunning) {
            System.out.println("System is not running.");
            return;
        }
        isRunning = false;
        stopAllThreads();

        // Stop all active vendors and customers
        activeVendors.clear();
        activeCustomers.clear();
    }

    /**
     * Dynamically fetch and add a vendor from the database to the system.
     *
     * @param vendorName The name of the vendor to fetch and add.
     */
    public void addVendor(String vendorName, int ticketsPerRelease, int releaseInterval) {
        Vendor vendor = new Vendor(ticketPool, ticketsPerRelease, releaseInterval, ticketPool.getMaxCapacity(), vendorName, dbHandler);
        dbHandler.saveVendor(vendor);
        activeVendors.add(vendor);
        System.out.println("Vendor added " + vendorName);
    }

    /**
     * Dynamically fetch and add a customer from the database to the system.
     *
     * @param customerName The name of the customer to fetch and add.
     */
    public void addCustomer(String customerName, int retrievalInterval) {
        Customer customer = new Customer(ticketPool, retrievalInterval, customerName, dbHandler);
        dbHandler.saveCustomer(customer);
        activeCustomers.add(customer);
        System.out.println("Customer added: " + customerName);
    }

    private void stopAllThreads() {
        try {
            // Shut down vendor and customer executors
            vendorExecutor.shutdownNow(); // Interrupt all vendor tasks
            customerExecutor.shutdownNow(); // Interrupt all customer tasks

            if (!vendorExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Vendor tasks did not terminate in time.");
            }
            if (!customerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Customer tasks did not terminate in time.");
            }

        } catch (InterruptedException e) {
            System.err.println("Error while stopping threads: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }


}
