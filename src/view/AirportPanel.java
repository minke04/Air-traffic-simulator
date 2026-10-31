package view;

import controller.AirportController;
import exception.ValidationException;
import model.Airport;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AirportPanel extends Panel {
    
    private TextField nameField, codeField, xField, yField;
    private TextArea airportList;
    private final AirportController controller;
    private Frame parentWindow;
    private boolean dataLoadedFromFile = false; 
    private boolean dataEnteredManually = false;

    /* Constructor that initializes panel and its layout */
    public AirportPanel(AirportController airportController, Frame parentWindow) {
        this.controller = airportController;
        this.parentWindow = parentWindow;
        setLayout(new BorderLayout());
        populateWindow();
    }
  
    /* Creates and arranges all UI elements */
    private void populateWindow() {
        Panel splitPanel = new Panel(new GridLayout(1, 2, 10, 0));
        Panel leftPanel = new Panel(new GridLayout(6, 1, 5, 5));
        leftPanel.setBackground(new Color(230, 240, 255));

        // Name input
        Panel namePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new Label("Name:"));
        nameField = new TextField(18);
        namePanel.add(nameField);
        leftPanel.add(namePanel);

        // Code input
        Panel codePanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        codePanel.add(new Label("Code:"));
        codeField = new TextField(3);
        codePanel.add(codeField);
        leftPanel.add(codePanel);

        // Coordinates input
        Panel xyPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        xyPanel.add(new Label("X:"));
        xField = new TextField(5);
        xyPanel.add(xField);
        xyPanel.add(new Label("Y:"));
        yField = new TextField(5);
        xyPanel.add(yField);
        leftPanel.add(xyPanel);

        // Button to add new airport
        Panel addButtonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button addButton = new Button("Add Airport");
        addButton.addActionListener(e -> addAirport());
        addButtonPanel.add(addButton);
        leftPanel.add(addButtonPanel);

        // Buttons for loading/saving CSV files
        Panel csvPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        Button loadCSVButton = new Button("Load from .csv file");
        loadCSVButton.addActionListener(e -> { 	
            if (dataEnteredManually) {
                showErrorDialog("Error : You cannot load airports from a file after adding them manually.");
                return;
            }
            try {
                FileDialog fd = new FileDialog((Frame) null, "Select .csv file to load", FileDialog.LOAD);
                fd.setVisible(true);
                String filePath = fd.getDirectory() + fd.getFile();
                if (filePath != null) {
                    controller.loadAirportsFromCSV(filePath);
                    dataLoadedFromFile = true;    
                    updateAirportList();
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
                    controller.saveAirportsToCSV(filePath);
                }
            } catch (Exception ex) {
                showErrorDialog("Error : " + ex.getMessage());
            }
        });

        csvPanel.add(loadCSVButton);
        csvPanel.add(saveCSVButton);
        leftPanel.add(csvPanel);
      
        // Back button to main menu
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

        // Right panel showing the list of airports
        Panel rightPanel = new Panel(new BorderLayout(5, 5));
        Label listLabel = new Label("Airport List", Label.CENTER);
        listLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(listLabel, BorderLayout.NORTH);

        airportList = new TextArea();
        airportList.setEditable(false);
        rightPanel.add(airportList, BorderLayout.CENTER);

        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);
        add(splitPanel, BorderLayout.CENTER);
    }

    /* Adds a new airport based on user input */
    private void addAirport() {
        if (dataLoadedFromFile) {
            showErrorDialog("Error : You cannot add airports manually after loading from a file.");
            return;
        }

        try {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
            float x = Float.parseFloat(xField.getText().trim());
            float y = Float.parseFloat(yField.getText().trim());

            controller.addAirport(name, code, x, y);
            dataEnteredManually = true;

            // Clear fields after adding
            nameField.setText("");
            codeField.setText("");
            xField.setText("");
            yField.setText("");
            updateAirportList();
        } catch (ValidationException ve) {
            showErrorDialog("Error : " + ve.getMessage());
        } catch (Exception ex) {
            showErrorDialog("Unexpected error : The fields for the new airport were not filled in correctly.");
        }
    }

    /* Updates the displayed list of airports */
    private void updateAirportList() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s | %-18s | %-10s\n", "Code", "Name", "Coordinates (x,y)"));
        sb.append("---------------------------------------------\n");

        List<Airport> airports = new ArrayList<>(controller.getAirports());
        airports.sort(Comparator.comparing(Airport::getCode));

        for (Airport a : airports) {
            sb.append(String.format("%-4s | %-18s | (%-3.2f , %-3.2f)\n",
                    a.getCode(), a.getName(), a.getX(), a.getY()));
        }
        airportList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        airportList.setText(sb.toString());
    }

    /* Displays an error message dialog */
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