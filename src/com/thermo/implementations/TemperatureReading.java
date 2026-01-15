package com.thermo.implementations;

public class TemperatureReading
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

    public int GetSensorID()
    {
        return sensorID;
    }

    public double GetValue()
    {
        return value;
    }
    
    public String GetTimestamp()
    {
        return timestamp;
    }
}
