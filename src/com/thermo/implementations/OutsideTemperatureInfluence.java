package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.IOutsideTemperatureInfluence;
import java.util.concurrent.Semaphore;

/**
 * Models the influence of the external (ambient) temperature on the internal room temperature.
 *
 * This class simulates heat exchange between the environment and each room partition
 * using a simple linear heat transfer model. The effect is applied periodically
 * in synchronization with the simulation timer.
 *
 * Design characteristics:
 * - Runs in its own background thread
 * - Applies temperature drift toward the outside temperature
 * - Uses semaphores to ensure safe concurrent access to shared temperature data
 *
 * Concurrency:
 * - Each temperature cell is protected by a corresponding semaphore
 * - Ensures mutual exclusion when reading and updating temperatures
 */
public class OutsideTemperatureInfluence implements IOutsideTemperatureInfluence, Runnable
{
    /** Dedicated background thread for applying ambient temperature effects. */
    private Thread thread = null;

    /** Shared temperature grid representing room partitions. */
    private double[][] temperatures = null;

    /** External ambient temperature. */
    private double outsideTemperature;

    /** Coefficient controlling the rate of heat transfer. */
    private double heatTransferCoefficient;

    /** Mutex matrix protecting access to the temperature grid. */
    private Semaphore[][] mutexes;

    /**
     * Constructs a new OutsideTemperatureInfluence instance.
     *
     * @param temperatures Shared temperature grid
     * @param outsideTemperature Ambient temperature outside the room
     * @param heatTransferCoefficient Heat transfer coefficient (must be non-negative)
     * @param mutexes Semaphore matrix guarding the temperature grid
     */
    public OutsideTemperatureInfluence(
        double[][] temperatures,
        double outsideTemperature,
        double heatTransferCoefficient,
        Semaphore[][] mutexes
    )
    {
        if(temperatures == null)
            throw new IllegalArgumentException(Exceptions.TEMP_GRID_NULL);
        if(heatTransferCoefficient < 0)
            throw new IllegalArgumentException(Exceptions.HEAT_TRANSFER_COEFFICIENT_NULL);

        this.temperatures = temperatures;
        this.outsideTemperature = outsideTemperature;
        this.heatTransferCoefficient = heatTransferCoefficient;
        this.mutexes = mutexes;
    }

    /**
     * Starts the ambient temperature influence thread.
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
     * Applies the ambient temperature effect to each room partition.
     *
     * The temperature of each partition is incrementally adjusted toward
     * the outside temperature based on the heat transfer coefficient.
     */
    private void ApplyAmbientEffect()
    {
        for(int i = 0; i < temperatures.length; i++)
        {
            try
            {
                mutexes[i][0].acquire();

                double current = temperatures[i][0];
                double delta = heatTransferCoefficient * (outsideTemperature - current);
                temperatures[i][0] += delta;
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.err.println(
                    Exceptions.OUTSIDETEMPERATUREINFLUENCE_INTERRUPT + ": " + e.getMessage()
                );
            }
            finally
            {
                mutexes[i][0].release();
            }
        }
    }

    /**
     * Main execution loop for ambient temperature simulation.
     *
     * The thread waits for the simulation timer to start and then
     * periodically applies the ambient temperature effect at each tick.
     */
    @Override
    public void run()
    {
        Timer.WaitTillTimerStarts();

        while(Timer.IsRunning())
        {
            ApplyAmbientEffect();
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }
}