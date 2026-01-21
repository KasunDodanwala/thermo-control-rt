package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Timer
{
    // <==Attributes==>

    private static int threadSleepTimeMilliSeconds = 0;       // The sleep time between each simulated thread update in milliseconds
    private static int speedMultiplier = 0;                 // Speed multiplier that affects the speed of the simulation (e.g., 0.1x, 1x, 10x)
    private static long runTime = 0;                        // The total runtime for the simulation in milliseconds
    private static volatile long currentTime = 0;                    // The current time of the simulation in milliseconds
    private static boolean running = false;                 // A flag indicating whether the simulation is running or not
    private static int tickTime = 0;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  // Executor to manage scheduled tasks

    // <==Attributes==>

    // <==Constructors==>

    // Private constructor to prevent instantiation of the Timer class (static class)
    private Timer(){}

    // <==Constructors==>

    // <==Methods==>

    /**
     * Initializes the timer with the given parameters.
     *
     * @param speedMultiplier The speed multiplier for the simulation (controls the speed).
     * @param runTime The total runtime for the simulation in milliseconds.
     */
    public static void Init(int threadSleepTimeMilliSeconds, int speedMultiplier, long runTime, int tickTime)
    {
        if(!running) // Initialize only if the timer is not already running
        {
            Timer.threadSleepTimeMilliSeconds = threadSleepTimeMilliSeconds;
            Timer.speedMultiplier = speedMultiplier <= 0 ? 10 : speedMultiplier; // Default to 10 if invalid multiplier
            Timer.runTime = runTime; // Set the total runtime
            Timer.tickTime = tickTime;
            currentTime = 0; // Initialize current time to zero
        }
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return The current simulation time in milliseconds.
     */
    public static long GetTimeInMilliSeconds()
    {
        return currentTime;
    }


    /**
     * Returns the current time in a formatted string (minutes and seconds).
     *
     * @return The formatted time as a string (e.g., "5m30s").
     */
    public static String GetTimeInMinutesAndSeconds()
    {
        return (currentTime / 1000 / 60) + "m" + (currentTime / 1000 % 60) + "s";
    }

    /**
     * Returns the speed multiplier that affects the simulation speed.
     *
     * @return The speed multiplier (e.g., 1, 10).
     */
    public static int GetSpeedMultiplier()
    {
        return speedMultiplier;
    }

    /**
     * Returns the total runtime of the simulation.
     *
     * @return The total runtime in milliseconds.
     */
    public static long GetRunTime()
    {
        return runTime;
    }

    /**
     * Returns the remaining time for the simulation to run.
     *
     * @return The remaining time in milliseconds.
     */
    public static long GetRemainingTime()
    {
        return (runTime - currentTime) >= 0 ? (runTime - currentTime) : 0;
    }

    /**
     * Starts the timer and begins scheduling tasks.
     */
    public static void Start()
    {
        if(!running) // Prevent starting the timer if it's already running
        {
            running = true;
            scheduler = Executors.newScheduledThreadPool(1);  // Reinitialize the scheduler
            // Schedule the tick method to update the simulation time every {tickTime} / speedMultiplier milliseconds
            scheduler.scheduleAtFixedRate(Timer::Tick, 0, tickTime / speedMultiplier, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the timer and cancels all scheduled tasks.
     * It ensures that the scheduler shuts down properly and outputs the final car park stats.
     */
    public static void Stop()
    {
        if (!running)
            return;  // Prevent multiple calls

        running = false;
        scheduler.shutdown();
        try
        {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS))
            {
                scheduler.shutdownNow(); // Shut down the scheduler
            }
        }
        catch (InterruptedException e)
        {
            System.out.println(Exceptions.TIMER_STOP_INTERRUPTED);
            Thread.currentThread().interrupt();  // Restore interrupted status
        }
    }

    /**
     * Increments the timers.
     * Stops the timer if it reaches the run time.
     */
    private static void Tick()
    {
        currentTime += tickTime;
        if(currentTime >= runTime)
            Stop();
    }

    /**
     * Returns the timer's running status.
     *
     * @return True if running, otherwise false.
     */
    public static boolean IsRunning()
    {
        return running;
    }


    /**
     * Sleeps the calling thread for a given amount of time.
     */
    public static void WaitFor(long sleepTime)
    {
        try
        {
            Thread.sleep(sleepTime / Timer.GetSpeedMultiplier());
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Waits until the timer has started before continuing.
     */
    public static void WaitTillTimerStarts()
    {
        while(!Timer.IsRunning())
        {
            WaitFor(threadSleepTimeMilliSeconds);
        }
    }

    /**
     * Wait when encountered resource access issue.
     */
    public static void WaitSimulatedThreadSleepTime()
    {
        WaitFor(threadSleepTimeMilliSeconds);
    }

    // <==Methods==>
}