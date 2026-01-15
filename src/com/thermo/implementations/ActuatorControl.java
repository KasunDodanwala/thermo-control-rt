package com.thermo.implementations;

import java.util.concurrent.Semaphore;

public class ActuatorControl
{
    private static volatile  ActuatorControl INSTANCE = null;
    private Heater heater = null;
    private Cooler cooler = null;
    public  ActuatorState state = ActuatorState.OFF;

    private ActuatorControl(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        this.cooler = new Cooler(temperatures, tempAffect, mutexes);
        this.heater = new Heater(temperatures, tempAffect, mutexes);
    }

    public static void Init(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        if(INSTANCE != null)
        {
            throw new IllegalStateException("ActuatorControl already initialized");
        }

        synchronized(ActuatorControl.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException("ActuatorControl already initialized");
            }

            INSTANCE = new ActuatorControl(temperatures, tempAffect, mutexes);
            INSTANCE.cooler.Start();
            INSTANCE.heater.Start();
        }
    }

    public static ActuatorControl GetINSTANCE()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException("ActuatorControl not initialized. Call ActuatorControl.Init() first.");
        }
        return INSTANCE;
    }

    public void TurnHeaterOn()
    {
        state = ActuatorState.HEATING;
        heater.Enable();
        ///////////////LOG///////////////////////
        Logger.GetINSTANCE().Log(Timer.GetTimeInMinutesAndSeconds() + ": " + "Heater Turned On");
    }

    public void TurnHeaterOff()
    {
        state = ActuatorState.OFF;
        heater.Disable();
        ///////////////LOG///////////////////////
        Logger.GetINSTANCE().Log(Timer.GetTimeInMinutesAndSeconds() + ": " + "Heater Turned Off");
    }

    public void TurnCoolerOn()
    {
        state = ActuatorState.COOLING;
        cooler.Enable();
        ///////////////LOG///////////////////////
        Logger.GetINSTANCE().Log(Timer.GetTimeInMinutesAndSeconds() + ": " + "Cooler Turned On");
    }

    public void TurnCoolerOff()
    {
        state = ActuatorState.OFF;
        cooler.Disable();
        ///////////////LOG///////////////////////
        Logger.GetINSTANCE().Log(Timer.GetTimeInMinutesAndSeconds() + ": " + "Cooler Turned Off");
    }

    public ActuatorState GetState()
    {
        return state;
    }
}
