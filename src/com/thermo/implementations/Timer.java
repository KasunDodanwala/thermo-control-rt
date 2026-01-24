package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Static simulation timer that controls the passage of simulated time.
 *
 * This class provides functionality for starting, stopping, and tracking the 
 * simulation time. It supports a configurable tick interval and speed multiplier,
 * and ensures thread-safe scheduling using a ScheduledExecutorService.
 */
public class Timer
{
    private static int threadSleepTimeMilliSeconds = 0;      // The sleep time between each simulated thread update in milliseconds
    private static int speedMultiplier = 0;                  // Speed multiplier affecting simulation speed
    private static long runTime = 0;                         // Total runtime of the simulation in milliseconds
    private static volatile long currentTime = 0;            // Current simulation time in milliseconds
    private volatile static boolean running = false;         // Flag indicating whether the timer is running
    private static int tickTime = 0;                         // Duration of a single tick in milliseconds
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  // Executor to manage scheduled tasks

    // Private constructor to prevent instantiation
    private Timer(){}

    /**
     * Initializes the timer with the given parameters.
     *
     * @param threadSleepTimeMilliSeconds Sleep time between simulated thread updates
     * @param speedMultiplier Multiplier controlling simulation speed
     * @param runTime Total runtime of the simulation in milliseconds
     * @param tickTime Duration of a single tick in milliseconds
     */
    public static void Init(int threadSleepTimeMilliSeconds, int speedMultiplier, long runTime, int tickTime)
    {
        if(!running)
        {
            Timer.threadSleepTimeMilliSeconds = threadSleepTimeMilliSeconds;
            Timer.speedMultiplier = speedMultiplier <= 0 ? 10 : speedMultiplier;
            Timer.runTime = runTime;
            Timer.tickTime = tickTime;
            currentTime = 0;
        }
    }

    /**
     * Returns the current simulation time in milliseconds.
     *
     * @return Current simulation time
     */
    public static long GetTimeInMilliSeconds()
    {
        return currentTime;
    }

    /**
     * Returns the current simulation time formatted as minutes and seconds.
     *
     * @return Time string in "XmYs" format
     */
    public static String GetTimeInMinutesAndSeconds()
    {
        return (currentTime / 1000 / 60) + "m" + (currentTime / 1000 % 60) + "s";
    }

    /**
     * Returns the speed multiplier of the simulation.
     *
     * @return Current speed multiplier
     */
    public static int GetSpeedMultiplier()
    {
        return speedMultiplier;
    }

    /**
     * Returns the total runtime of the simulation in milliseconds.
     *
     * @return Total runtime
     */
    public static long GetRunTime()
    {
        return runTime;
    }

    /**
     * Returns the remaining simulation time in milliseconds.
     *
     * @return Remaining simulation time
     */
    public static long GetRemainingTime()
    {
        return Math.max(0, runTime - currentTime);
    }

    /**
     * Starts the timer, scheduling periodic ticks at intervals determined
     * by the tickTime and speedMultiplier.
     */
    public static void Start()
    {
        if(!running)
        {
            running = true;
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(Timer::Tick, 0, tickTime / speedMultiplier, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the timer and cancels scheduled tasks. Ensures proper shutdown
     * of the scheduler and handles interruptions.
     */
    public static void Stop()
    {
        if (!running)
            return;

        running = false;
        scheduler.shutdown();
        try
        {
            if (!scheduler.awaitTermination(2, TimeUnit.SECONDS))
            {
                scheduler.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            System.out.println(Exceptions.TIMER_STOP_INTERRUPTED);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Advances the simulation time by one tick and stops the timer if the
     * total runtime has been reached.
     */
    private static void Tick()
    {
        currentTime += tickTime;
        if(currentTime >= runTime)
            Stop();
    }

    /**
     * Returns whether the timer is currently running.
     *
     * @return True if running, false otherwise
     */
    public static boolean IsRunning()
    {
        return running;
    }

    /**
     * Causes the calling thread to sleep for the specified duration, adjusted
     * by the simulation speed multiplier.
     *
     * @param sleepTime Sleep time in milliseconds
     */
    public static void WaitFor(long sleepTime)
    {
        try
        {
            Thread.sleep(sleepTime / speedMultiplier);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Blocks the calling thread until the timer has started.
     */
    public static void WaitTillTimerStarts()
    {
        while(!running)
        {
            WaitFor(threadSleepTimeMilliSeconds);
        }
    }

    /**
     * Waits for the configured thread sleep time, useful for simulated thread pauses.
     */
    public static void WaitSimulatedThreadSleepTime()
    {
        WaitFor(threadSleepTimeMilliSeconds);
    }
}