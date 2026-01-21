package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.IOutsideTemperatureInfluence;
import java.util.concurrent.Semaphore;

public class OutsideTemperatureInfluence implements IOutsideTemperatureInfluence, Runnable
{
    private Thread thread = null;
    private double[][] temperatures = null;
    private double outsideTemperature;
    private double heatTransferCoefficient;
    private Semaphore[][] mutexes;

    public OutsideTemperatureInfluence(double[][] temperatures, double outsideTemperature, double heatTransferCoefficient, Semaphore[][] mutexes)
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

    @Override
    public void Start()
    {
        if(thread != null)
            return;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

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
                System.err.println(Exceptions.OUTSIDETEMPERATUREINFLUENCE_INTERRUPT + ": " + e.getMessage());
            }
            finally
            {
                mutexes[i][0].release();
            }
        }
    }

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