package com.thermo.implementations;

public class SensorReading
{
    public int SensorId;
    public double Value;
    public String Timestamp;

    public SensorReading(int SensorId, double Value, String Timestamp)
    {
        this.SensorId = SensorId;
        this.Value = Value;
        this.Timestamp = Timestamp;
    }
}