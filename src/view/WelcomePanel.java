package view;

import controller.MainController;
import model.Airport;
import utils.TimerManager;

import java.awt.*;

public class WelcomePanel extends Panel {
    private MainWindow parentWindow;     
    private MainController mainController;
    private TimerManager timerManager;    
    private AirportPanel airportPanel;    
    private FlightPanel flightPanel;      
    private MapPanel mapPanel;            
    private Panel descriptionPanel;       
    private Panel footerPanel;           

    // Constructor: initializes the welcome screen layout.
    public WelcomePanel(MainWindow parentWindow, MainController mainController, TimerManager timerManager) {
        this.parentWindow = parentWindow;
        this.mainController = mainController;
        this.timerManager = timerManager;

        setLayout(new BorderLayout());
        createHeader();
        createDescriptionSection();
        createFooterSection();
    }

    /* Provides access to the map panel. */
    public MapPanel getMapPanel() {
        return mapPanel;
    }

    /* Creates and styles the header title. */
    private void createHeader() {
        Label title = new Label("Air Traffic Manager", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.DARK_GRAY);
        title.setBackground(new Color(220, 220, 250));
        add(title, BorderLayout.NORTH);
    }

    /* Adds description text for each project phase. */
    private void createDescriptionSection() {
        descriptionPanel = new Panel(new GridLayout(3, 1, 10, 10));
        descriptionPanel.setBackground(Color.WHITE);
        descriptionPanel.setPreferredSize(new Dimension(800, 400));
        descriptionPanel.add(createTextArea(getPhaseAText()));
        descriptionPanel.add(createTextArea(getPhaseBText()));
        descriptionPanel.add(createTextArea(getPhaseCText()));
        add(descriptionPanel, BorderLayout.CENTER);
    }

    /* Helper method for creating styled text areas. */
    private TextArea createTextArea(String text) {
        TextArea area = new TextArea(text, 5, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        return area;
    }

    /* Footer with navigation buttons and instructions. */
    private void createFooterSection() {
        footerPanel = new Panel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        footerPanel.setPreferredSize(new Dimension(800, 100));

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // Main buttons
        Button airportBtn = new Button("Airports");
        Button flightBtn = new Button("Flights");
        Button mapBtn = new Button("Map");
        Button resetBtn = new Button("Reset");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        airportBtn.setFont(buttonFont);
        flightBtn.setFont(buttonFont);
        mapBtn.setFont(buttonFont);
        resetBtn.setFont(buttonFont);

        // Button color-coding.
        airportBtn.setBackground(new Color(180, 200, 255));
        flightBtn.setBackground(new Color(180, 255, 200));
        mapBtn.setBackground(new Color(255, 230, 180));
        resetBtn.setBackground(new Color(255, 180, 180));

        buttonPanel.add(airportBtn);
        buttonPanel.add(flightBtn);
        buttonPanel.add(mapBtn);
        buttonPanel.add(resetBtn);

        Label instructionLabel = new Label(
                "Please select one of the options above to continue working with the application.",
                Label.CENTER
        );
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        instructionLabel.setForeground(Color.DARK_GRAY);

        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        footerPanel.add(instructionLabel, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        // Button actions.
        airportBtn.addActionListener(e -> {
            if (airportPanel == null) {
                airportPanel = new AirportPanel(mainController.getAirportController(), parentWindow);
            }
            parentWindow.switchPanel(airportPanel);
        });

        flightBtn.addActionListener(e -> {
            if (flightPanel == null) {
                flightPanel = new FlightPanel(
                    mainController.getFlightController(),
                    mainController.getAirportController(),
                    parentWindow
                );
            }
            flightPanel.refreshAirports();
            parentWindow.switchPanel(flightPanel);
        });

        mapBtn.addActionListener(e -> {
            if (mapPanel == null) {
                mapPanel = new MapPanel(mainController.getAirportController(), parentWindow, timerManager);
            }
            mapPanel.refreshAirports();
            parentWindow.switchPanel(mapPanel);
        });

        // Reset button confirmation dialog.
        resetBtn.addActionListener(e -> {
            Dialog confirmDialog = new Dialog(parentWindow, "Confirm Reset", true);
            confirmDialog.setLayout(new FlowLayout());
            confirmDialog.add(new Label("Are you sure you want to reset the application?"));

            Button yes = new Button("Yes");
            Button no = new Button("No");

            yes.addActionListener(ev -> {
                mainController.resetApp();
                timerManager.stopTimer();
                airportPanel = null;
                flightPanel = null;
                mapPanel = null;
                parentWindow.showWelcomePanel();
                confirmDialog.dispose();
            });
            no.addActionListener(ev -> confirmDialog.dispose());

            confirmDialog.add(yes);
            confirmDialog.add(no);
            confirmDialog.setSize(300, 150);
            confirmDialog.setLocationRelativeTo(parentWindow);
            confirmDialog.setVisible(true);
        });
    }

    /* Description text for Phase A. */
    private String getPhaseAText() {
        return "					Data Entry and Display\n\n" +
                "In this phase, the user enters data about airports and flights using a graphical interface.\n" +
                "For each airport, enter its name, unique three-letter code (uppercase only), and coordinates (X, Y) within the range [-90, 90].\n" +
                "For each flight, enter departure airport, arrival airport, departure time (HH:MM), and flight duration (minutes).\n\n" +
                "The data is displayed in a table and can be saved or loaded from CSV files.\n" +
                "If invalid input or file errors occur, clear and helpful error messages will be shown.";
    }

    /* Description text for Phase B. */
    private String getPhaseBText() {
        return "					Airport Map Visualization\n\n" +
                "This phase shows a visual map of airports.\n" +
                "Each airport appears as a gray square positioned according to its coordinates.\n" +
                "Clicking on an airport highlights it with a blinking red effect.\n" +
                "You can filter which airports are visible using a list with checkboxes.\n\n" +
                "The inactivity timer pauses while an airport is selected.";
    }

    /* Description text for Phase C. */
    private String getPhaseCText() {
        return "					Flight Simulation\n\n" +
                "The simulation starts at 00:00.\n" +
                "One real-time second equals 10 minutes of simulation time.\n" +
                "At most one plane can take off from an airport every 10 minutes.\n" +
                "Planes are displayed as blue circles moving between airports at constant speed.\n\n" +
                "You can start, pause, or reset the simulation at any time.";
    }
}