import java.util.Timer;
import java.util.TimerTask;

public class SIGAlarm {
    private Timer timer;

    /**
     * Sets a new alarm. If an existing alarm is running, it is canceled.
     *
     * @param seconds The time in seconds after which the program should terminate.
     */
    public void setAlarm(int seconds) {
        // Cancel the previous timer if it exists
        if (timer != null) {
            timer.cancel();
        }

        // Create a new timer
        timer = new Timer();

        // Schedule the task to terminate the program after the given seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("The time limit of 60 seconds has passed. Exiting...");
                System.exit(0); // Forcefully exit the program
            }
        }, seconds * 1000L);
    }

    /**
     * Cancels the current alarm.
     */
    public void cancelAlarm() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
