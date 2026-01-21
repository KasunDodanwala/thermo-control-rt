package com.thermo.interfaces;

/**
 * Interface representing a single temperature reading from a sensor.
 * Encapsulates the sensor ID, measured temperature value, and timestamp of the reading.
 */
public interface ITemperatureReading
{
    /**
     * Returns the ID of the sensor that produced this reading.
     *
     * @return Sensor ID
     */
    public int GetSensorID();

    /**
     * Returns the measured temperature value.
     *
     * @return Temperature in Celsius
     */
    public double GetValue();
    
    /**
     * Returns the timestamp when the reading was recorded.
     *
     * @return Timestamp as a formatted string
     */
    public String GetTimestamp();
}