package nuber.students;

import java.util.concurrent.*;

/**
 * A single Nuber region that operates independently of other regions, other than getting
 * drivers from bookings from the central dispatch.
 *
 * A region has a maxSimultaneousJobs setting that defines the maximum number of bookings
 * that can be active with a driver at any time. For passengers booked that exceed that
 * active count, the booking is accepted, but must wait until a position is available, and
 * a driver is available.
 *
 * Bookings do NOT have to be completed in FIFO order.
 *
 * @author james
 *
 */
public class NuberRegion
{
    /**
     * reference to the central dispatch
     */
    private NuberDispatch dispatch;
    /**
     * Name of this region
     */
    private String regionName;
    private int maxSimultaneousJobs;
    /**
     * our threadpool to execute our tasks concurrently
     */
    private ExecutorService threadPool;
    /**
     * The queue to hold the tasks for the threadpool to execute
     */
    private BlockingQueue<Runnable> bookingsQueue;
    /**
     * The amount of threads that the threadpool will instantiate initially.
     */
    private final int CORE_SIZE = 3;
    /**
     * The life time of an idle thread in seconds
     */
    private final int LIFETIME = 5;
    /**
     * Creates a new Nuber region
     *
     * @param dispatch The central dispatch to use for obtaining drivers, and logging events
     * @param regionName The regions name, unique for the dispatch instance
     * @param maxSimultaneousJobs The maximum number of simultaneous bookings the region is allowed to process
     */
    public NuberRegion(NuberDispatch dispatch, String regionName, int maxSimultaneousJobs)
    {
        this.dispatch = dispatch;
        this.regionName = regionName;
        this.maxSimultaneousJobs = maxSimultaneousJobs;
        bookingsQueue = new ArrayBlockingQueue<Runnable>(maxSimultaneousJobs);
        threadPool = new ThreadPoolExecutor(
                CORE_SIZE,
                maxSimultaneousJobs,
                LIFETIME,
                TimeUnit.SECONDS,
                bookingsQueue);
    }
    /**
     * Creates a booking for given passenger, and adds the booking to the
     * collection of jobs to process. Once the region has a position available, and a driver is available,
     * the booking should commence automatically.
     *
     * If the region has been told to shutdown, this function should return null, and log a message to the
     * console that the booking was rejected.
     *
     * @param waitingPassenger
     * @return a Future that will provide the final BookingResult object from the completed booking
     */
    public Future<BookingResult> bookPassenger(Passenger waitingPassenger)
    {
        try
        {
            Booking booking = new Booking(dispatch,waitingPassenger);
            return threadPool.submit(booking);
        }catch (RejectedExecutionException e)
        {
            return null;
        }
    }
    /**
     * Getter method for retrieving the number of bookings waiting in queue.
     *
     * @return bookingQueue.Size()
     */
    public int getBookingQueueLength(){return bookingsQueue.size();}
    /**
     * Called by dispatch to tell the region to complete its existing bookings and stop accepting any new bookings
     */
    public void shutdown()
    {
        threadPool.shutdown();
    }

}
