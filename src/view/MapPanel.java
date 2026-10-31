package view;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import controller.AirportController;
import model.Airport;
import utils.TimerManager;

public class MapPanel extends Panel {

    private Frame parentWindow;                       
    private AirportController airportController;      
    private MapCanvas mapCanvas;                      
    private Map<String, Checkbox> airportCheckboxes = new HashMap<>(); 
    private Panel rightPanel;                        
    private TimerManager timerManager;                

    /* Constructor: initializes the map panel and populates the window. */
    public MapPanel(AirportController airportController, Frame parentWindow, TimerManager timerManager) {
        this.airportController = airportController;
        this.parentWindow = parentWindow;
        this.timerManager = timerManager;
        
        setLayout(new BorderLayout());
        populateWindow(); // Setup UI components
    }

    /*
     * Creates the UI layout:
     * - Map in the center.
     * - Scrollable list of airports on the right.
     */
    private void populateWindow() {
        mapCanvas = new MapCanvas();
        mapCanvas.setBackground(new Color(230, 240, 255)); // Light background for map.
        add(mapCanvas, BorderLayout.CENTER);

        rightPanel = new Panel();
        rightPanel.setLayout(new GridLayout(0, 1, 5, 5)); // Vertical list.
        rightPanel.setBackground(new Color(240, 240, 240));

        for (Airport a : airportController.getAirports()) {
            addAirportToRightPanel(a); // Add each airport.
        }
        
        addBackButtonToRightPanel(); // Add navigation back button.

        ScrollPane scroll = new ScrollPane();
        scroll.add(rightPanel);
        scroll.setPreferredSize(new Dimension(150, 0)); // Fixed width for side panel.
        add(scroll, BorderLayout.EAST);
    }

    /** Adds an airport entry to the right panel with checkbox and labels **/
    private void addAirportToRightPanel(Airport a) {
        Panel airportItemPanel = new Panel(new GridLayout(3, 1));

        Checkbox cb = new Checkbox(a.getCode(), true);
        cb.setFont(new Font("Arial", Font.BOLD, 12));
        cb.addItemListener(e -> mapCanvas.repaint()); // Repaint map when toggled.
        airportCheckboxes.put(a.getCode(), cb);

        Label nameLabel = new Label(a.getName(), Label.CENTER);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        Label coordinateLabel = new Label("(" + a.getX() + ", " + a.getY() + ")", Label.CENTER);
        coordinateLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        airportItemPanel.add(cb);
        airportItemPanel.add(nameLabel);
        airportItemPanel.add(coordinateLabel);

        rightPanel.add(airportItemPanel);
    }

    /** Adds a button to return back to the main menu **/
    private void addBackButtonToRightPanel() {
        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        Button backButton = new Button("Main Menu");

        backButton.addActionListener(e -> {
            if (parentWindow instanceof MainWindow) {
                ((MainWindow) parentWindow).showWelcomePanel(); // Navigate back.
            }
        });
        bottomPanel.add(backButton);
        rightPanel.add(bottomPanel);
    }

    /* Refresh the list of airports and repaint the map. */
    public void refreshAirports() {
        rightPanel.removeAll();
        airportCheckboxes.clear();

        for (Airport a : airportController.getAirports()) {
            addAirportToRightPanel(a);
        }
        addBackButtonToRightPanel();
        rightPanel.revalidate();
        rightPanel.repaint();
        mapCanvas.repaint();
    }

    /* Stops the blinking timer when panel is closed or switched. */
    public void stopTimer() {
        if (mapCanvas != null) {
            mapCanvas.stopBlinkTimer();
        }
    }

    /* Inner class responsible for rendering the map and handling airport clicks. */
    private class MapCanvas extends Canvas implements MouseListener {

        private Airport selectedAirport = null; // Currently highlighted airport.
        private boolean blinkOn = true;         // State for blinking effect.
        private java.util.Timer blinkTimer;     // Timer for blinking effect.

        public MapCanvas() {
            addMouseListener(this);
            startBlinkTimer(); // Start blinking animation.
        }

        /* Starts the timer that toggles blinking every 500ms. */
        private void startBlinkTimer() {
            blinkTimer = new java.util.Timer(true);
            blinkTimer.scheduleAtFixedRate(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (selectedAirport != null) {
                        blinkOn = !blinkOn;

                        int px = mapX(selectedAirport.getX(), getWidth());
                        int py = mapY(selectedAirport.getY(), getHeight());

                        // Only repaint the area around the blinking airport.
                        repaint(px - 6, py - 6, 12, 12);
                    }
                }
            }, 0, 500);
        }

        /* Stops the blinking timer. */
        public void stopBlinkTimer() {
            if (blinkTimer != null) {
                blinkTimer.cancel();
                blinkTimer = null;
            }
        }

        /* Draws the map, airports, and blinking effect. */
        @Override
        public void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            // Background and grid.
            g.setColor(new Color(230, 240, 255));
            g.fillRect(0, 0, w, h);
            drawGrid(g, w, h);

            // Draw each airport.
            List<Airport> airports = airportController.getAirports();
            for (Airport a : airports) {
                Checkbox cb = airportCheckboxes.get(a.getCode());
                if (cb != null && !cb.getState()) continue; // Skip if checkbox is unchecked.

                int px = mapX(a.getX(), w);
                int py = mapY(a.getY(), h);

                // Selected airport blinks red.
                if (a.equals(selectedAirport) && blinkOn) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.GRAY);
                }

                g.fillRect(px - 5, py - 5, 10, 10);
                g.setColor(Color.BLACK);
                g.drawString(a.getCode(), px + 8, py - 8);
            }
        }

        /* Draws the coordinate grid and labels. */
        private void drawGrid(Graphics g, int w, int h) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(0, h / 2, w, h / 2); // Horizontal center line.
            g.drawLine(w / 2, 0, w / 2, h); // Vertical center line.
            g.setColor(Color.BLACK);

            for (int i = -90; i <= 90; i += 5) {
                int px = mapX(i, w);
                g.drawLine(px, h / 2 - 3, px, h / 2 + 3);
                g.drawString(String.valueOf(i), px - 7, h / 2 + 15);
            }
            
            for (int i = -90; i <= 90; i += 5) {
                int py = mapY(i, h);
                g.drawLine(w / 2 - 3, py, w / 2 + 3, py);
                g.drawString(String.valueOf(i), w / 2 + 8, py + 5);
            }
        }

        /* Converts X coordinate to screen position. */
        private int mapX(float x, int width) {
            return Math.round(width / 2f + x * (width / 185f));
        }

        /* Converts Y coordinate to screen position. */
        private int mapY(float y, int height) {
            return Math.round(height / 2f - y * (height / 185f));
        }

        /* Handles mouse clicks for selecting/deselecting airports. */
        @Override
        public void mouseClicked(MouseEvent e) {
            int mx = e.getX();
            int my = e.getY();
            List<Airport> airports = airportController.getAirports();

            for (Airport a : airports) {
                Checkbox cb = airportCheckboxes.get(a.getCode());
                if (cb != null && !cb.getState()) continue;

                int px = mapX(a.getX(), getWidth());
                int py = mapY(a.getY(), getHeight());

                Rectangle rect = new Rectangle(px - 5, py - 5, 10, 10);

                if (rect.contains(mx, my)) {
                    Airport previous = selectedAirport;
                    selectedAirport = a.equals(selectedAirport) ? null : a;

                    // Pause timer while an airport is selected.
                    if (selectedAirport != null) {
                        timerManager.pauseTimer();
                    } else {
                        timerManager.resumeTimer();
                    }

                    // Repaint previously selected and newly selected airports.
                    if (previous != null) {
                        int prevX = mapX(previous.getX(), getWidth());
                        int prevY = mapY(previous.getY(), getHeight());
                        repaint(prevX - 6, prevY - 6, 12, 12);
                    }
                    repaint(px - 6, py - 6, 12, 12);
                    break;
                }
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }
}