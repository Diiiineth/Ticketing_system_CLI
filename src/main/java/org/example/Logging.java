package org.example;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Logging class provides methods for logging messages to both the console and a log file.
 */
public class Logging {
    private static final String LOG_FILE = "system.log";  // Log file location
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a message to both the console and the log file with a timestamp.
     *
     * @param message The message to log.
     */
    public static void log(String message) {
        String timestampedMessage = formatMessage(message);

        // Output the timestamped message to the console
        System.out.println(timestampedMessage);

        // Attempt to write the message to the log file
        try {
            writeToFile(timestampedMessage);
        } catch (IOException e) {
            // Catch IOException and log an error message to the console if the file write fails
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    /**
     * Formats the log message by appending a timestamp.
     *
     * @param message The message to format.
     * @return The formatted message with a timestamp.
     */
    private static String formatMessage(String message) {
        // Get the current timestamp and format it
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return "[" + timestamp + "] " + message;
    }

    /**
     * Writes the log message to a file in append mode.
     *
     * @param message The message to write to the file.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    private static void writeToFile(String message) throws IOException {
        // Use a try-with-resources statement to automatically close the PrintWriter
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(message);  // Append the message to the log file
        }
    }
}
