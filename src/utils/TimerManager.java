package utils;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Manages an inactivity timer that closes the application if there is no activity
 * for a specified duration.
 */
public class TimerManager {

    private Timer timer;
    private Frame parentFrame;
    private int remaining = 60; // Remaining seconds before timeout.
    private boolean warningShown = false;
    private boolean paused = false;

    /*  
     * Constructor that initializes the timer for the given frame.
     * @param frame The main application frame to be managed by the timer.
     */
    public TimerManager(Frame frame) {
        this.parentFrame = frame;
        startTimer();
    }

    /* Starts the timer and schedules a countdown task. */
    private void startTimer() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                remaining--;

                if (remaining <= 5 && remaining > 0 && !warningShown) {
                    warningShown = true;
                    showWarningDialog();
                } else if (remaining <= 0) {
                    parentFrame.dispose();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    /* Displays a warning dialog when the session is about to expire. */
    private void showWarningDialog() {
        EventQueue.invokeLater(() -> {
            Dialog dlg = new Dialog(parentFrame, "Warning", true);
            dlg.setLayout(new FlowLayout());
            dlg.setSize(300, 120);
            dlg.add(new Label("Closing in " + remaining + " sec. Continue?"));

            Button yes = new Button("Yes");
            yes.addActionListener(e -> {
                resetTimer();
                dlg.dispose();
            });
            dlg.add(yes);

            dlg.setLocationRelativeTo(parentFrame);
            dlg.setVisible(true);

            warningShown = false;
        });
    }

    /* Pauses the countdown timer. */
    public void pauseTimer() {
        paused = true;
    }

    /* Resumes the countdown timer. */
    public void resumeTimer() {
        paused = false;
    }

    /* Resets the countdown back to 60 seconds. */
    public void resetTimer() {
        remaining = 60;
        warningShown = false;
    }

    /* Stops the timer completely. */
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}