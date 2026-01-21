package com.thermo.interfaces;

public interface ITemperatureReading
{
    public int GetSensorID();

    public double GetValue();
    
    public String GetTimestamp();
}
