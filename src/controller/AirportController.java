package controller;

import model.Airport;
import utils.CsvUtil;
import utils.Validator;
import exception.FileProcessingException;
import exception.ValidationException;

import java.util.List;

public class AirportController {
    private final List<Airport> airports;

    /* Constructor initializes the controller with a shared list of airports. */
    public AirportController(List<Airport> airports) {
        this.airports = airports;
    }

    /* 
     * Adds a new airport after validating its data.
     * Throws ValidationException if the data is invalid or duplicate.
     */
    public void addAirport(String name, String code, float x, float y) throws ValidationException {
        Validator.validateAirport(name, code, x, y, airports);
        airports.add(new Airport(name, code, x, y));
    }
    
    /* Removes a specific airport from the list. */
    public void removeAirport(Airport airport) {
        airports.remove(airport);
    }
    
    /* Saves the current list of airports to a CSV file. */
    public void saveAirportsToCSV(String filePath) throws FileProcessingException {
        CsvUtil.saveAirports(airports, filePath);
    }
    
    /*
     * Loads airports from a CSV file.
     * Clears the current list and replaces it with the loaded data.
     */
    public void loadAirportsFromCSV(String filePath) throws FileProcessingException, ValidationException {
        List<Airport> loadedAirports = CsvUtil.loadAirports(filePath);
        airports.clear();
        airports.addAll(loadedAirports);
    }

    /* Returns the list of all airports. */
    public List<Airport> getAirports() {
        return airports;
    }
    
    /* Clears all airports from the list. */
    public void clearAirports() {
        airports.clear();
    }
}