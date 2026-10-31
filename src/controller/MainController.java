package controller;

import model.Airport;
import model.Flight;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private final AirportController airportController;
    private final FlightController flightController;

    /* 
     * Constructor initializes shared airport and flight lists 
     * and creates both controllers using the same shared data.
     */
    public MainController() {
        List<Airport> sharedAirports = new ArrayList<>();
        List<Flight> sharedFlights = new ArrayList<>();

        this.airportController = new AirportController(sharedAirports);
        this.flightController = new FlightController(sharedFlights, sharedAirports);
    }

    /* Returns the airport controller instance. */
    public AirportController getAirportController() {
        return airportController;
    }

    /* Returns the flight controller instance. */
    public FlightController getFlightController() {
        return flightController;
    }
    
    /* 
     * Resets the entire application by clearing both.
     * airports and flights.
     */
    public void resetApp() {
        airportController.clearAirports();
        flightController.clearFlights();
    }
}