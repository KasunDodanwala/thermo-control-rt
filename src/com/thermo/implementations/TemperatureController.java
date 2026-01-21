package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ITemperatureController;

public class TemperatureController implements ITemperatureController, Runnable
{
    private Thread thread = null;
    private double tempUpperBound;
    private double tempLowerBound;
    private TempReadingBuffer buff = null;

    public TemperatureController(double tempUpperBound, double tempLowerBound, TempReadingBuffer buff)
    {
        if(tempLowerBound > tempUpperBound)
            throw new IllegalArgumentException
            (
                Exceptions.INVALID_TEMP_BOUNDS + ": " +
                "lower=" + tempLowerBound + ", upper=" + tempUpperBound
            );
        this.tempLowerBound = tempLowerBound;
        this.tempUpperBound = tempUpperBound;
        this.buff = buff;
    }

    private Double CurrentAverageTemp()
    {
        double tempAcc = 0;
        SensorReading[] tempBuff = buff.RetrieveReadings();
        if(tempBuff == null)
            return null;
        int size = tempBuff.length;
        for(int i = 0; i < size; i++)
        {
            tempAcc += tempBuff[i].Value;
        }
        return tempAcc / size;
    }

    private void EvaluateTemperature(Double temp)
    {
        if(temp == null)
            return;
        double middleTemp = (tempLowerBound + tempUpperBound) / 2;
        if(temp > tempLowerBound && temp < tempUpperBound)
        {
            switch(ActuatorControl.GetINSTANCE().GetState())
            {
                case OFF -> {}
                case HEATING -> { if(temp > middleTemp) ActuatorControl.GetINSTANCE().TurnHeaterOff(); }
                case COOLING -> { if(temp < middleTemp) ActuatorControl.GetINSTANCE().TurnCoolerOff(); }
                default -> throw new AssertionError();
            }
        }
        else if(temp < tempLowerBound)
        {
            ActuatorControl.GetINSTANCE().TurnHeaterOn();
        }
        else
        {
            ActuatorControl.GetINSTANCE().TurnCoolerOn();
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
            EvaluateTemperature(CurrentAverageTemp());
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }

}
