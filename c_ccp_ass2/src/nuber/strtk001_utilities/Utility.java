package nuber.strtk001_utilities;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ThreadLocalRandom;

public class Utility
{
    /**
     * Puts the current thread to sleep for a random int seconds
     * @param maxSleep the maximum ammount of millisconds to sleep.
     */
    public static void delay(int maxSleep)
    {
        try
        {
            //using thread local random to reduce overhead instead of java.utils.random or whatever it is.
            int sleepTime = ThreadLocalRandom.current().nextInt(maxSleep);
            Thread.sleep(Duration.ofMillis(sleepTime));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
