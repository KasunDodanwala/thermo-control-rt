package com.thermo.implementations;

import java.util.concurrent.Semaphore;

public class OutsideTemperatureInfluence implements Runnable
{
    private Thread thread = null;
    private double[][] temperatures = null;
    private double outsideTemperature;
    private double heatTransferCoefficient;
    private Semaphore[][] mutexes;

    public OutsideTemperatureInfluence(double[][] temperatures, double outsideTemperature, double heatTransferCoefficient, Semaphore[][] mutexes)
    {
        if(temperatures == null)
            throw new IllegalArgumentException("Temperature grid must not be null");
        if(heatTransferCoefficient < 0)
            throw new IllegalArgumentException("Heat transfer coefficient must be non-negative");

        this.temperatures = temperatures;
        this.outsideTemperature = outsideTemperature;
        this.heatTransferCoefficient = heatTransferCoefficient;
        this.mutexes = mutexes;
    }

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
                System.err.println("Thread interrupted while applying ambient effect: " + e.getMessage());
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