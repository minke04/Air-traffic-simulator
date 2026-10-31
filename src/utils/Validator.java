package utils;

import model.Airport;
import model.Flight;
import exception.ValidationException;
import java.time.LocalTime;
import java.util.List;

/*
 * Utility class that provides methods for validating user input related to
 * airports and flights.
 */
public class Validator {

    /*
     * Validates that the airport code is in the correct format and unique.
     * @param code The airport code to validate.
     * @param existingAirports The list of current airports to check for duplicates.
     * @throws ValidationException If the code is invalid or already exists.
     */
    public static void validateAirportCode(String code, List<Airport> existingAirports) throws ValidationException {
        if (code == null || !code.matches("[A-Z]{3}")) {
            throw new ValidationException("Error: Airport code must be exactly 3 uppercase letters (A-Z).");
        }
        for (Airport a : existingAirports) {
            if (a.getCode().equals(code)) {
                throw new ValidationException("Error: An airport with this code already exists (" + code + ").");
            }
        }
    }

    /*
     * Validates that the airport name is not null or empty.
     * @param name The airport name to validate.
     * @throws ValidationException If the name is empty or null.
     */
    public static void validateAirportName(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Airport name cannot be empty.");
        }
    }

    /*
     * Validates the X and Y coordinates of an airport.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @throws ValidationException If the coordinates are out of range (-90 to 90).
     */
    public static void validateCoordinates(float x, float y) throws ValidationException {
        if (x < -90.0 || x > 90.0 || y < -90.0 || y > 90.0) {
            throw new ValidationException("Coordinates must be between -90.0 and 90.0 for both X and Y.");
        }
    }

    /*
     * Validates all attributes of an airport together.
     * @param name Airport name.
     * @param code Airport code.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param existingAirports Current list of airports for duplicate checking.
     * @throws ValidationException If any validation fails.
     */
    public static void validateAirport(String name, String code, float x, float y, List<Airport> existingAirports)
            throws ValidationException {
        validateAirportName(name);
        validateAirportCode(code, existingAirports);
        validateCoordinates(x, y);
    }

    /*
     * Validates flight details such as origin, destination, time, and duration.
     * @param origin The starting airport.
     * @param destination The destination airport.
     * @param departureTime Departure time of the flight.
     * @param durationMinutes Duration of the flight in minutes.
     * @param existingFlights Current list of flights for duplicate checking.
     * @throws ValidationException If any validation fails.
     */
    public static void validateFlight(Airport origin, Airport destination, LocalTime departureTime,
                                      int durationMinutes, List<Flight> existingFlights)
            throws ValidationException {

        if (origin == null || destination == null) {
            throw new ValidationException("Both origin and destination airports must be selected.");
        }

        if (origin.equals(destination)) {
            throw new ValidationException("Origin and destination airports cannot be the same.");
        }

        if (departureTime == null) {
            throw new ValidationException("Departure time must be specified.");
        }

        if (durationMinutes <= 0) {
            throw new ValidationException("Flight duration must be a positive number of minutes.");
        }

        for (Flight f : existingFlights) {
            if (f.getOrigin().equals(origin) &&
                f.getDestination().equals(destination)) {
                throw new ValidationException(
                    "A flight from " + origin.getCode() + " to " + destination.getCode() + " already exists."
                );
            }
        }
    }
}