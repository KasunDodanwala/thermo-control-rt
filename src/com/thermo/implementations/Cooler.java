package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ICooler;
import java.util.concurrent.Semaphore;

/**
 * Cooler actuator responsible for reducing room temperatures.
 *
 * This class models a cooling actuator that runs in its own thread and
 * periodically applies a cooling effect to the shared temperature grid
 * when enabled. It operates independently of the temperature controller,
 * which only toggles its enabled state.
 *
 * Concurrency:
 * - Each temperature cell update is protected by a binary semaphore
 *   to prevent race conditions with other actuators or influences.
 *
 * Lifecycle:
 * - Created and started by ActuatorControl
 * - Remains idle until enabled
 * - Stops applying cooling once the global Timer stops
 */
public class Cooler implements ICooler, Runnable
{
    /** Dedicated thread executing the cooling loop. */
    private Thread thread = null;

    /** Shared temperature grid affected by the cooler. */
    private double[][] temperatures = null;

    /** Indicates whether the cooler is currently active. */
    private boolean enabled = false;

    /** Temperature delta applied per tick when cooling. */
    private final double tempAffect;

    /** Per-cell semaphores guarding access to the temperature grid. */
    private Semaphore[][] mutexes;

    /**
     * Constructs a Cooler actuator.
     *
     * @param temperatures Shared temperature grid
     * @param tempAffect Temperature decrease applied per tick
     * @param mutexes Semaphore matrix protecting each temperature cell
     */
    public Cooler(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        this.temperatures = temperatures;
        this.tempAffect = tempAffect;
        this.mutexes = mutexes;
    }

    /**
     * Enables the cooler, allowing it to apply cooling on each tick.
     */
    public void Enable()
    {
        enabled = true;
    }

    /**
     * Disables the cooler, preventing further temperature changes.
     */
    public void Disable()
    {
        enabled = false;
    }

    /**
     * Applies cooling to all room partitions.
     *
     * Each temperature update is guarded by a semaphore to ensure
     * thread-safe access to the shared temperature grid.
     */
    private void ApplyCooling()
    {
        for(int i = 0; i < temperatures.length; i++)
        {
            try
            {
                mutexes[i][0].acquire();
                temperatures[i][0] -= tempAffect;
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.err.println(
                    Exceptions.COOLER_INTERRUPT + ": " + e.getMessage()
                );
            }
            finally
            {
                mutexes[i][0].release();
            }
        }
    }

    /**
     * Starts the cooler thread.
     *
     * This method is idempotent and will not start the thread more than once.
     */
    @Override
    public void Start()
    {
        if(thread != null)
            return;

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Main execution loop for the cooler.
     *
     * The thread waits for the simulation timer to start, then periodically
     * applies cooling when enabled, synchronized to the simulation tick rate.
     */
    @Override
    public void run()
    {
        Timer.WaitTillTimerStarts();
        while(Timer.IsRunning())
        {
            if(enabled)
                ApplyCooling();
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }
}