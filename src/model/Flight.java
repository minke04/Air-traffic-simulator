package model;

import java.time.LocalTime;

public class Flight {
    private Airport origin;
    private Airport destination;
    private LocalTime departureTime;
    private int durationMinutes; 

    /* Constructor initializes a flight with origin, destination, time, and duration. */
    public Flight(Airport origin, Airport destination, LocalTime departureTime, int durationMinutes) {
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.durationMinutes = durationMinutes;
    }
    
    /* Returns the origin airport. */
    public Airport getOrigin() { return origin; }

    /* Sets the origin airport. */
    public void setOrigin(Airport origin) { this.origin = origin; }

    /* Returns the destination airport. */
    public Airport getDestination() { return destination; }

    /* Sets the destination airport. */
    public void setDestination(Airport destination) { this.destination = destination; }

    /* Returns the departure time. */
    public LocalTime getDepartureTime() { return departureTime; }

    /* Sets the departure time. */
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }

    /* Returns the duration of the flight in minutes. */
    public int getDurationMinutes() { return durationMinutes; }

    /* Sets the duration of the flight in minutes. */
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    /* Returns a formatted string describing the flight. */
    @Override
    public String toString() {
        return origin.getCode() + " -> " + destination.getCode() + 
               " at " + departureTime + " (" + durationMinutes + " min)";
    }
}