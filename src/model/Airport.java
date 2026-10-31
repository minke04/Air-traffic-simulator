package model;

public class Airport {
    private String name;
    private String code; 
    private float x; 
    private float y;

    /* Constructor initializes an airport with name, code, and coordinates. */
    public Airport(String name, String code, float x, float y) {
        this.name = name;
        this.code = code;
        this.x = x;
        this.y = y;
    }

    /* Returns the airport name. */
    public String getName() { return name; }

    /* Sets a new name for the airport. */
    public void setName(String name) { this.name = name; }

    /* Returns the unique three-letter code of the airport. */
    public String getCode() { return code; }

    /* Sets a new code for the airport. */
    public void setCode(String code) { this.code = code; }

    /* Returns the X coordinate. */
    public float getX() { return x; }

    /* Sets the X coordinate. */
    public void setX(float x) { this.x = x; }

    /* Returns the Y coordinate. */
    public float getY() { return y; }

    /* Sets the Y coordinate. */
    public void setY(float y) { this.y = y; }

    /* Returns a formatted string representing the airport. */
    @Override
    public String toString() {
        return code + " - " + name;
    }
}