package utils;

import model.Airport;
import model.Flight;
import exception.FileProcessingException;
import exception.ValidationException;
import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/* Utility class for handling CSV file operations related to Airports and Flights. */
public class CsvUtil {

    /* Formatter used for parsing and formatting departure times. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /*
     * Saves the list of airports to a CSV file with a header.
     * @param airports The list of airports to save.
     * @param filePath The file path where the data will be stored.
     * @throws FileProcessingException If there is an error during writing.
     */
    public static void saveAirports(List<Airport> airports, String filePath) throws FileProcessingException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write("code,name,x,y\n"); // header
            for (Airport airport : airports) {
                writer.write(String.format("%s,%s,%.2f,%.2f\n",
                        airport.getCode(),
                        airport.getName(),
                        airport.getX(),
                        airport.getY()));
            }
        } catch (IOException e) {
            throw new FileProcessingException("An error occurred while saving airports in the .csv file", e);
        }
    }

    /*
     * Saves the list of flights to a CSV file with a header.
     * @param flights The list of flights to save.
     * @param filePath The file path where the data will be stored.
     * @throws FileProcessingException If there is an error during writing.
     */
    public static void saveFlights(List<Flight> flights, String filePath) throws FileProcessingException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write("fromCode,toCode,departureTime,duration\n"); // header
            for (Flight flight : flights) {
                writer.write(String.format("%s,%s,%s,%d\n",
                        flight.getOrigin().getCode(),
                        flight.getDestination().getCode(),
                        flight.getDepartureTime().format(TIME_FORMATTER),
                        flight.getDurationMinutes()));
            }
        } catch (IOException e) {
            throw new FileProcessingException("An error occurred while saving flights in the .csv file", e);
        }
    }

    /*
     * Loads airports from a CSV file and validates them before adding to the list.
     * @param filePath The file path of the CSV to load.
     * @return List of validated Airport objects.
     * @throws FileProcessingException If the file format is invalid or unreadable.
     * @throws ValidationException If an airport fails validation.
     */
    public static List<Airport> loadAirports(String filePath) throws FileProcessingException, ValidationException {
        List<Airport> airports = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line = reader.readLine(); // skip header
            if (line == null || !line.equals("code,name,x,y")) {
                throw new FileProcessingException("Invalid airport CSV format: header is missing or incorrect.\n"
                        + "Correct header : code,name,x,y.");
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new FileProcessingException("Invalid .csv line for airport: " + line);
                }

                String code = parts[0].trim();
                String name = parts[1].trim();
                float x = Float.parseFloat(parts[2].trim());
                float y = Float.parseFloat(parts[3].trim());

                Validator.validateAirport(name, code, x, y, airports);
                airports.add(new Airport(name, code, x, y));
            }
        } catch (IOException | NumberFormatException e) {
            throw new FileProcessingException("An error occurred while loading airports from the .csv file", e);
        }
        return airports;
    }

    /*
     * Loads flights from a CSV file, validating data and ensuring referenced airports exist.
     * @param filePath The file path of the CSV to load.
     * @param airports The list of existing airports to match origin and destination codes.
     * @return List of validated Flight objects.
     * @throws FileProcessingException If the file format is invalid or unreadable.
     * @throws ValidationException If a flight fails validation.
     */
    public static List<Flight> loadFlights(String filePath, List<Airport> airports)
            throws FileProcessingException, ValidationException {
        List<Flight> flights = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line = reader.readLine();
            if (line == null || !line.equals("fromCode,toCode,departureTime,duration")) {
                throw new FileProcessingException("Invalid flight .csv format: header is missing or incorrect.\n"
                        + "Correct header : fromCode,toCode,departureTime,duration.");
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    throw new FileProcessingException("Error : Invalid .csv line for flight: " + line);
                }

                String fromCode = parts[0].trim();
                String toCode = parts[1].trim();
                LocalTime departureTime = LocalTime.parse(parts[2].trim(), TIME_FORMATTER);
                int duration = Integer.parseInt(parts[3].trim());

                Airport fromAirport = airports.stream()
                        .filter(a -> a.getCode().equals(fromCode))
                        .findFirst()
                        .orElseThrow(() -> new FileProcessingException(
                                "From airport not found in airport list : " + fromCode + ".\n"
                                        + "Load the airports first so you can enter flights."));

                Airport toAirport = airports.stream()
                        .filter(a -> a.getCode().equals(toCode))
                        .findFirst()
                        .orElseThrow(() -> new FileProcessingException(
                                "To airport not found in airport list : " + toCode + ".\n"
                                        + "Load the airports first so you can enter flights."));

                Validator.validateFlight(fromAirport, toAirport, departureTime, duration, flights);
                flights.add(new Flight(fromAirport, toAirport, departureTime, duration));
            }
        } catch (IOException | NumberFormatException e) {
            throw new FileProcessingException("An error occurred while loading flights from the .csv file", e);
        }
        return flights;
    }
}