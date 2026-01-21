package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
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
            throw new IllegalStateException(Exceptions.ACTUATORCONTROL_ALREADY_INITIALIZED);
        }

        synchronized(ActuatorControl.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException(Exceptions.ACTUATORCONTROL_ALREADY_INITIALIZED);
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
            throw new IllegalStateException(Exceptions.ACTUATORCONTROL_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    public void TurnHeaterOn()
    {
        state = ActuatorState.HEATING;
        heater.Enable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nHeater Turned On\n"
        );
    }

    public void TurnHeaterOff()
    {
        state = ActuatorState.OFF;
        heater.Disable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nHeater Turned Off\n"
        );
    }

    public void TurnCoolerOn()
    {
        state = ActuatorState.COOLING;
        cooler.Enable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nCooler Turned On\n"
        );
    }

    public void TurnCoolerOff()
    {
        state = ActuatorState.OFF;
        cooler.Disable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nCooler Turned Off\n"
        );
    }

    public ActuatorState GetState()
    {
        return state;
    }
}
