package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ICooler;
import java.util.concurrent.Semaphore;

public class Cooler implements ICooler, Runnable
{
    private Thread thread = null;
    private double[][] temperatures = null;
    private boolean enabled = false;
    private final double tempAffect;
    private Semaphore[][] mutexes;

    public Cooler(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
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
                System.err.println(Exceptions.COOLER_INTERRUPT + ": " + e.getMessage());
            }
            finally
            {
                mutexes[i][0].release();
            }
        }
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