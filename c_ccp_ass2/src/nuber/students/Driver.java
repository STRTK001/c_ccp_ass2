package nuber.students;



import static nuber.strtk001_utilities.Utility.delay;

public class Driver extends Person
{
    /**
     * The current passenger
     */

    private Passenger myPassenger;

    public Driver(String driverName, int maxSleep)
    {
        super(driverName, maxSleep);
    }

    /**
     * Stores the provided passenger as the driver's current passenger and then
     * sleeps the thread for between 0-maxDelay milliseconds.
     *
     * @param newPassenger Passenger to collect
     * @throws InterruptedException
     */
    public void pickUpPassenger(Passenger newPassenger)
    {
        System.out.println("This driver is picking up the passenger.");
        myPassenger = newPassenger;
        delay(maxSleep);
        System.out.println("This driver has picking up the passenger.");
    }

    /**
     * Sleeps the thread for the amount of time returned by the current
     * passenger's getTravelTime() function
     *
     * @throws InterruptedException
     */
    public void driveToDestination()
    {
        System.out.println("This driver is driving to dest.");
        delay(myPassenger.getTravelTime());
        System.out.println("This driver has arrived at dest.");
    }

}
