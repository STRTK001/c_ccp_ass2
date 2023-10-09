package nuber.students;

public class Driver extends Person 
{
	private Passenger myPassenger;
	
	private String driverName;
	
	private int maxSleep;
	
	public Driver(String driverName, int maxSleep)
	{
		this.driverName = driverName;
		this.maxSleep = maxSleep;
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
		myPassenger = newPassenger;
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	public void driveToDestination() {
	}
	
}
