package com.thermo.implementations;

import com.thermo.interfaces.ITemperatureReading;

public class TemperatureReading implements ITemperatureReading
{
    private int sensorID = 0;
    private double value = 0;
    private String timestamp = null;

    public TemperatureReading(int sensorID, double value, String timestamp)
    {
        this.sensorID = sensorID;
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public int GetSensorID()
    {
        return sensorID;
    }

    @Override
    public double GetValue()
    {
        return value;
    }
    
    @Override
    public String GetTimestamp()
    {
        return timestamp;
    }
}
