package group.six.weather4genz;

import java.util.Timer;
import java.util.TimerTask;

public class SetTimeout {

    public SetTimeout(Runnable r, long timeout) {
        // Prepare the thread.
        final Thread t = new Thread(r);
        // Start the timer.
        new Timer(true).schedule(new TimerTask() {

            @Override
            public void run() {
                if (t.isAlive()) {
                    // Abort the thread.
                    t.interrupt();
                }
            }
        }, timeout * 1000);
        // Start the thread.
        t.start();
    }

}
