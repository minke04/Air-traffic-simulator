package controller;

import model.Flight;
import utils.CsvUtil;
import utils.Validator;
import model.Airport;
import exception.FileProcessingException;
import exception.ValidationException;

import java.time.LocalTime;
import java.util.List;

public class FlightController {
    private final List<Flight> flights;
    private final List<Airport> airports;

    /* Constructor initializes the controller with shared lists of flights and airports. */
    public FlightController(List<Flight> flights, List<Airport> airports) {
        this.flights = flights;
        this.airports = airports;
    }

    /* 
     * Adds a new flight after validating it.
     * Throws ValidationException if flight data is invalid or duplicate.
     */
    public void addFlight(Airport origin, Airport destination, LocalTime departureTime, int durationMinutes) throws ValidationException {
        Validator.validateFlight(origin, destination, departureTime, durationMinutes, flights);
        flights.add(new Flight(origin, destination, departureTime, durationMinutes));
    }

    /* Removes a specific flight from the list. */
    public void removeFlight(Flight flight) {
        flights.remove(flight);
    }
    
    /* Saves the current list of flights to a CSV file. */
    public void saveFlightsToCSV(String filePath) throws FileProcessingException {
        CsvUtil.saveFlights(flights, filePath);
    }
    
    /* 
     * Loads flights from a CSV file.
     * Clears the current list and replaces it with the loaded data.
     */
    public void loadFlightsFromCsv(String filePath) throws FileProcessingException, ValidationException {
        List<Flight> loadedFlights = CsvUtil.loadFlights(filePath, airports);
        flights.clear();
        flights.addAll(loadedFlights);
    }
    
    /* Returns the list of all flights. */
    public List<Flight> getFlights() {
        return flights;
    }
    
    /* Clears all flights from the list. */
    public void clearFlights() {
        flights.clear();
    }
}