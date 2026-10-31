package view;

import controller.FlightController;
import controller.AirportController;
import exception.ValidationException;
import model.Airport;
import model.Flight;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlightPanel extends Panel {

    private Choice originChoice, destinationChoice;
    private TextField departureTimeField, durationField;
    private TextArea flightListArea;
    private final FlightController flightController;
    private final AirportController airportController;
    private Frame parentWindow;
    private boolean dataLoadedFromFile = false; 
    private boolean dataEnteredManually = false;

    /* Constructor that sets up controllers and initializes the layout */
    public FlightPanel(FlightController flightController, AirportController airportController, Frame parentWindow) {
        this.flightController = flightController;
        this.airportController = airportController;
        this.parentWindow = parentWindow;
        setLayout(new BorderLayout());
        populateWindow();
    }

    /* Creates and arranges all UI components */
    private void populateWindow() {
        Panel splitPanel = new Panel(new GridLayout(1, 2, 10, 0));
        Panel leftPanel = new Panel(new GridLayout(5, 1, 5, 5));
        leftPanel.setBackground(new Color(230, 240, 255));

        // Origin and destination selectors
        Panel originDestPanel = new Panel(new GridLayout(2, 1, 5, 5));
        Panel originPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        originPanel.add(new Label("Origin:"));
        originChoice = new Choice();
        originChoice.setPreferredSize(new Dimension(120, 22));
        originPanel.add(originChoice);
        originDestPanel.add(originPanel);

        Panel destinationPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        destinationPanel.add(new Label("Destination:"));
        destinationChoice = new Choice();
        destinationChoice.setPreferredSize(new Dimension(120, 22));
        destinationPanel.add(destinationChoice);
        originDestPanel.add(destinationPanel);
        leftPanel.add(originDestPanel);

        // Departure time and duration fields
        Panel timeDurationPanel = new Panel(new GridLayout(2, 1, 5, 5));
        Panel departurePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        departurePanel.add(new Label("Departure (HH:MM):"));
        departureTimeField = new TextField(6);
        departurePanel.add(departureTimeField);
        timeDurationPanel.add(departurePanel);

        Panel durationPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        durationPanel.add(new Label("Duration (min):"));
        durationField = new TextField(6);
        durationPanel.add(durationField);
        timeDurationPanel.add(durationPanel);
        leftPanel.add(timeDurationPanel);

        // Add flight button
        Panel addButtonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button addFlightButton = new Button("Add Flight");
        addFlightButton.addActionListener(e -> addFlight());
        addButtonPanel.add(addFlightButton);
        leftPanel.add(addButtonPanel);

        // Load and save CSV
        Panel csvPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        Button loadCSVButton = new Button("Load from .csv file");
        loadCSVButton.addActionListener(e -> {
            if (dataEnteredManually) {
                showErrorDialog("Error : You cannot load flights from a file after adding them manually.");
                return;
            }
            try {
                FileDialog fd = new FileDialog((Frame) null, "Choose .csv file to load", FileDialog.LOAD);
                fd.setVisible(true);
                String filePath = fd.getDirectory() + fd.getFile();
                if (filePath != null) {
                    flightController.loadFlightsFromCsv(filePath);
                    dataLoadedFromFile = true;
                    updateFlightList();
                }
            } catch (Exception ex) {
                showErrorDialog("Error : " + ex.getMessage());
            }
        });

        Button saveCSVButton = new Button("Save to .csv file");
        saveCSVButton.addActionListener(e -> {
            try {
                FileDialog fd = new FileDialog((Frame) null, "Save .csv file", FileDialog.SAVE);
                fd.setVisible(true);
                String filePath = fd.getDirectory() + fd.getFile();
                if (filePath != null) {
                    flightController.saveFlightsToCSV(filePath);
                }
            } catch (Exception ex) {
                showErrorDialog("Error : " + ex.getMessage());
            }
        });
        csvPanel.add(loadCSVButton);
        csvPanel.add(saveCSVButton);
        leftPanel.add(csvPanel);

        // Back button
        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button backButton = new Button("Main Menu");
        backButton.addActionListener(e -> {
            if (parentWindow instanceof MainWindow) {
                MainWindow mainWin = (MainWindow) parentWindow;
                mainWin.showWelcomePanel();
            }
        });
        bottomPanel.add(backButton);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Right panel showing list of flights
        Panel rightPanel = new Panel(new BorderLayout(5, 5));
        Label listLabel = new Label("Flight List", Label.CENTER);
        listLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(listLabel, BorderLayout.NORTH);

        flightListArea = new TextArea();
        flightListArea.setEditable(false);
        rightPanel.add(flightListArea, BorderLayout.CENTER);

        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);
        add(splitPanel, BorderLayout.CENTER);
    }

    /* Adds a new flight using user input */
    private void addFlight() {
        if (dataLoadedFromFile) {
            showErrorDialog("Error : You cannot add flights manually after loading from a file.");
            return;
        }

        try {
            String originCode = originChoice.getSelectedItem();
            String destinationCode = destinationChoice.getSelectedItem();

            Airport origin = airportController.getAirports().stream()
                    .filter(a -> a.getCode().equals(originCode))
                    .findFirst().orElse(null);

            Airport destination = airportController.getAirports().stream()
                    .filter(a -> a.getCode().equals(destinationCode))
                    .findFirst().orElse(null);

            LocalTime departure = LocalTime.parse(departureTimeField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());

            flightController.addFlight(origin, destination, departure, duration);

            departureTimeField.setText("");
            durationField.setText("");
            updateFlightList();

        } catch (ValidationException ve) {
            showErrorDialog("Error : " + ve.getMessage());
        } catch (Exception ex) {
            showErrorDialog("Unexpected error : The fields for the new flight were not filled in correctly.");
        }
    }

    /* Updates the displayed list of flights */
    private void updateFlightList() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-10s | %-10s | %-10s\n", "Flight", "Departure (h)", "Duration (min)"));
        sb.append("-------------------------------------------\n");

        List<Flight> flights = new ArrayList<>(flightController.getFlights());
        flights.sort(Comparator.comparing(Flight::getDurationMinutes));

        for (Flight f : flights) {
            sb.append(String.format("%-3s -> %-3s | %-5sh        | %-2d min     \n",
                    f.getOrigin().getCode(),
                    f.getDestination().getCode(),
                    f.getDepartureTime(),
                    f.getDurationMinutes()));
        }
        flightListArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        flightListArea.setText(sb.toString());
    }

    /* Refreshes dropdown lists with available airports */
    public void refreshAirports() {
        originChoice.removeAll();
        destinationChoice.removeAll();
        for (Airport airport : airportController.getAirports()) {
            originChoice.add(airport.getCode());
            destinationChoice.add(airport.getCode());
        }
    }

    /* Displays an error dialog */
    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog((Frame) null, "Error", true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label(message));
        Button ok = new Button("OK");
        ok.addActionListener(e -> dialog.dispose());
        dialog.add(ok);
        dialog.setSize(450, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}