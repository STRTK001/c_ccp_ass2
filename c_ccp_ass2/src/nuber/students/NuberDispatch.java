package nuber.students;

import javax.swing.plaf.synth.Region;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 *
 * @author james
 *
 */
public class NuberDispatch
{
    /**
     * The maximum number of idle drivers that can be awaiting a booking
     */
    private final int MAX_DRIVERS = 999;
    private boolean logEvents = false;
    /**
     * The Queue of idle drivers
     */
    private BlockingQueue<Driver> idleDrivers = new ArrayBlockingQueue<Driver>(MAX_DRIVERS);
    /**
     * Map to store reference to all regions so we can shut them down later.
     */
    private Map<String,NuberRegion> regions = new HashMap<String,NuberRegion>();
    /**
     * Creates a new dispatch objects and instantiates the required regions and any other objects required.
     * It should be able to handle a variable number of regions based on the HashMap provided.
     *
     * @param regionInfo Map of region names and the max simultaneous bookings they can handle
     * @param logEvents Whether logEvent should print out events passed to it
     */
    public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
    {
        this.logEvents = logEvents;
        addRegions(regionInfo);
    }
    /**
     * Adds drivers to a queue of idle driver.
     *
     * Must be able to have drivers added from multiple threads.
     *
     * @param newDriver The driver to add to the queue.
     * @return Returns true if driver was added to the queue
     */
    public synchronized boolean addDriver(Driver newDriver)
    {
        while(idleDrivers.size() >= MAX_DRIVERS)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        idleDrivers.add(newDriver);
        notifyAll();
        return true;
    }
    /**
     * Gets a driver from the front of the queue
     *
     * Must be able to have drivers added from multiple threads.
     *
     * @return A driver that has been removed from the queue
     */
    public synchronized Driver getDriver()
    {
        while(idleDrivers.size() == 0)
        {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        notifyAll();
        return idleDrivers.poll();
    }
    /**
     * Prints out the string
     * 	    booking + ": " + message
     * to the standard output only if the logEvents variable passed into the constructor was true
     *
     * @param booking The booking that's responsible for the event occurring
     * @param message The message to show
     */
    public void logEvent(Booking booking, String message) {

        if (!logEvents) return;

        System.out.println(booking + ": " + message);

    }
    /**
     * Books a given passenger into a given Nuber region.
     *
     * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
     *
     * If the region has been asked to shutdown, the booking should be rejected, and null returned.
     *
     * @param passenger The passenger to book
     * @param region The region to book them into
     * @return returns a Future<BookingResult> object
     */
    public Future<BookingResult> bookPassenger(Passenger passenger, String region)
            throws ExecutionException, InterruptedException
    {
        return regions.get(region).bookPassenger(passenger);
    }
    /**
     * Gets the number of non-completed bookings that are awaiting a driver from dispatch
     *
     * Once a driver is given to a booking, the value in this counter should be reduced by one
     *
     * @return Number of bookings awaiting driver, across ALL regions
     */
    public int getBookingsAwaitingDriver() //need to implement
    {
        int bookingCount = 0;
        for(NuberRegion region : regions.values())
        {
            bookingCount += region.getBookingQueueLength();
        }
        return bookingCount;
    }
    /**
     * Populates the regions map with regions from the regionInfo map.
     * @param regionInfo The map of regionName: String & booking limit: Integer.
     */
    private void addRegions(Map<String,Integer> regionInfo)
    {
        for(String key : regionInfo.keySet())
        {
            if(regions.containsKey(key))
            {
                continue;
            }
            NuberRegion region = new NuberRegion(this,key,regionInfo.get(key));
            regions.put(key,region);
        }
    }
    /**
     * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
     */
    public void shutdown()
    {
        for(String key : regions.keySet())
        {
            regions.get(key).shutdown();
        }
    }

}
