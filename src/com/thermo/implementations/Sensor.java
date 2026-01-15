package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ISensor;
import java.util.concurrent.Semaphore;

public class Sensor implements ISensor
{
    private double[] temperature = null;
    private Semaphore[] mutex = null;

    public Sensor(double[] temperature, Semaphore[] mutex)
    {
        this.temperature = temperature;
        this.mutex = mutex;
    }

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
        }
        finally
        {
            mutex[0].release();
        }
        return temp;
    }
}
