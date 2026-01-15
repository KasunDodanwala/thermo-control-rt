package com.thermo.implementations;

import java.util.concurrent.Semaphore;

public class Heater implements Runnable
{
    private Thread thread = null;
    private double[][] temperatures = null;
    private boolean enabled = false;
    private final double tempAffect;
    private Semaphore[][] mutexes;
    

    public Heater(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        this.temperatures = temperatures;
        this.tempAffect = tempAffect;
        this.mutexes = mutexes;
    }

    public void Enable()
    {
        enabled = true;
    }

    public void Disable()
    {
        enabled = false;
    }

    private void ApplyHeating()
    {
        for(int i = 0; i < temperatures.length; i++)
        {
            try
            {
                mutexes[i][0].acquire();
                temperatures[i][0] += tempAffect;
            }
            catch(InterruptedException e)
            {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted while applying heating: " + e.getMessage());
            }
            finally
            {
                mutexes[i][0].release();
            }
        }
    }

    public void Start()
    {
        if(thread != null)
            return;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

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
