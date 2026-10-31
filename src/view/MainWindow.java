package view;

import controller.MainController;
import utils.TimerManager;

import java.awt.*;
import java.awt.event.*;

public class MainWindow extends Frame {
    private MainController mainController; // Central controller for managing application logic.
    private TimerManager timerManager;     // Handles inactivity timer logic.
    private Panel currentPanel;            // Currently displayed panel.
    private WelcomePanel welcomePanel;     // Initial welcome screen panel.

    // Constructor: initializes the main window and sets up all core components.
    public MainWindow() {
        setTitle("Air Traffic Manager"); // Set the application window title.
        setBounds(600, 200, 800, 660);   // Position and size of the window.
        setLayout(new BorderLayout());   // Layout manager for arranging panels.

        // Initialize controllers and panels.
        mainController = new MainController();
        timerManager = new TimerManager(this);
        welcomePanel = new WelcomePanel(this, mainController, timerManager);
        currentPanel = welcomePanel;
        add(currentPanel, BorderLayout.CENTER); // Display the welcome panel by default.

        // Global event listener to reset inactivity timer on user interaction.
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getID() == MouseEvent.MOUSE_CLICKED || event.getID() == KeyEvent.KEY_PRESSED) {
                timerManager.resetTimer(); // Reset the timer whenever user interacts.
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

        // Handle closing of the main window.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	
                if (welcomePanel.getMapPanel() != null) {
                    welcomePanel.getMapPanel().stopTimer();
                }
                timerManager.stopTimer();

                for (Window window : Window.getWindows()) {
                    if (window instanceof Dialog) {
                        window.dispose();
                    }
                }
                dispose(); 
            }
        });
    }

    /* 
     * Switches the currently displayed panel to a new one.
     * Used when navigating between different screens in the app.
     */
    public void switchPanel(Panel newPanel) {
        remove(currentPanel);
        currentPanel = newPanel;
        add(currentPanel, BorderLayout.CENTER);
        validate(); 
        repaint(); 
    }

    /* Displays the WelcomePanel again, typically after a reset or navigation back. */
    public void showWelcomePanel() {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        currentPanel = welcomePanel;
        add(currentPanel, BorderLayout.CENTER);

        validate();
        repaint();
    }

    /* Entry point of the application. */
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }
}