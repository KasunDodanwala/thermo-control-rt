package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ITemperatureController;

/**
 * Responsible for monitoring room temperatures and controlling actuators (heater and cooler)
 * to maintain the temperature within specified upper and lower bounds.
 *
 * Design notes:
 * - Continuously retrieves sensor readings from the shared TempReadingBuffer.
 * - Calculates the current average temperature across all room partitions.
 * - Evaluates whether to turn the heater or cooler on/off based on the average temperature.
 * - Runs as a background thread and interacts with the ActuatorControl singleton.
 */
public class TemperatureController implements ITemperatureController, Runnable
{
    private Thread thread = null;             // Background thread running this controller
    private double tempUpperBound;            // Maximum allowable temperature
    private double tempLowerBound;            // Minimum allowable temperature
    private TempReadingBuffer buff = null;    // Shared buffer containing recent sensor readings

    /**
     * Constructs a TemperatureController with specified bounds and reading buffer.
     *
     * @param tempUpperBound Maximum allowable temperature
     * @param tempLowerBound Minimum allowable temperature
     * @param buff Shared buffer containing sensor readings
     * @throws IllegalArgumentException If lower bound > upper bound
     */
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

    /**
     * Computes the current average temperature from the TempReadingBuffer.
     *
     * @return The average temperature across all readings, or null if buffer is empty
     */
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

    /**
     * Evaluates the given temperature and adjusts actuator states as needed.
     *
     * - If temperature is below lower bound: turns heater on
     * - If temperature is above upper bound: turns cooler on
     * - If temperature is within bounds: may turn off active actuator if near middle
     *
     * @param temp The temperature to evaluate
     */
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

    /**
     * Starts the controller thread if not already running.
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
     * Controller thread logic.
     * Waits until the simulation timer starts, then continuously evaluates temperature
     * and adjusts actuators at each tick interval.
     */
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