package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.IHeater;
import java.util.concurrent.Semaphore;

/**
 * Heater actuator responsible for increasing room temperatures.
 *
 * This class models a heating actuator that runs in its own thread and
 * periodically applies a heating effect to the shared temperature grid
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
 * - Stops applying heating once the global Timer stops
 */
public class Heater implements IHeater, Runnable
{
    /** Dedicated thread executing the heating loop. */
    private Thread thread = null;

    /** Shared temperature grid affected by the heater. */
    private double[][] temperatures = null;

    /** Indicates whether the heater is currently active. */
    private boolean enabled = false;

    /** Temperature delta applied per tick when heating. */
    private final double tempAffect;

    /** Per-cell semaphores guarding access to the temperature grid. */
    private Semaphore[][] mutexes;

    /**
     * Constructs a heater actuator.
     *
     * @param temperatures Shared temperature grid
     * @param tempAffect Temperature decrease applied per tick
     * @param mutexes Semaphore matrix protecting each temperature cell
     */
    public Heater(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        this.temperatures = temperatures;
        this.tempAffect = tempAffect;
        this.mutexes = mutexes;
    }

    /**
     * Enables the heater, allowing it to apply heating on each tick.
     */
    public void Enable()
    {
        enabled = true;
    }

    /**
     * Disables the heater, preventing further temperature changes.
     */
    public void Disable()
    {
        enabled = false;
    }

    /**
     * Applies heating to all room partitions.
     *
     * Each temperature update is guarded by a semaphore to ensure
     * thread-safe access to the shared temperature grid.
     */
    private void ApplyHeating()
    {
        for(int i = 0; i < temperatures.length; i++)
        {
            boolean acquired = false;
            try
            {
                mutexes[i][0].acquire();
                acquired = true;
                temperatures[i][0] += tempAffect;
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.err.println(
                    Exceptions.HEATER_INTERRUPT + ": " + e.getMessage()
                );
            }
            finally
            {
                 if(acquired) mutexes[i][0].release();
            }
        }
    }

    /**
     * Starts the heater thread.
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
     * Main execution loop for the heater.
     *
     * The thread waits for the simulation timer to start, then periodically
     * applies heating when enabled, synchronized to the simulation tick rate.
     */
    @Override
    public void run()
    {
        Timer.WaitTillTimerStarts();
        while(Timer.IsRunning())
        {
            if(enabled)
                ApplyHeating();
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }
}
