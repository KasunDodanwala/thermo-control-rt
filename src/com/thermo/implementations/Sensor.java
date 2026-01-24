package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ISensor;
import java.util.concurrent.Semaphore;

/**
 * Represents a temperature sensor bound to a single room partition.
 *
 * This class provides read-only access to a shared temperature value.
 * The temperature is protected by a semaphore to ensure thread-safe
 * access when multiple concurrent components (controllers, actuators,
 * environment effects) operate on the same data.
 *
 * Concurrency:
 * - Uses a binary semaphore to guarantee mutual exclusion
 * - Ensures consistent temperature reads under concurrent updates
 */
public class Sensor implements ISensor
{
    /** Shared temperature reference for the associated room partition. */
    private double[] temperature = null;

    /** Semaphore guarding access to the temperature value. */
    private Semaphore[] mutex = null;

    /**
     * Constructs a sensor bound to a temperature source.
     *
     * @param temperature Shared temperature reference
     * @param mutex Semaphore protecting the temperature
     */
    public Sensor(double[] temperature, Semaphore[] mutex)
    {
        this.temperature = temperature;
        this.mutex = mutex;
    }

    /**
     * 
     * Always release() even if acquire() failed.
     * 
     * Reads the current temperature value in a thread-safe manner.
     *
     * The method acquires the semaphore before accessing the shared
     * temperature value to prevent race conditions.
     *
     * @return The current temperature, or null if interrupted
     */
    @Override
    public Double ReadTemperature()
    {
        Double temp = null;
        try
        {
            mutex[0].acquire();
            temp = temperature[0];
        }
        catch (InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READ_FAILED + ": " + e.toString());
            Thread.currentThread().interrupt();
        }
        finally
        {
            mutex[0].release();
        }
        return temp;
    }
}