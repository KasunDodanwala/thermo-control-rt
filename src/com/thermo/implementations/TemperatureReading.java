package com.thermo.implementations;

import com.thermo.interfaces.ITemperatureReading;

/**
 * Represents a single temperature reading from a sensor.
 *
 * Stores the sensor ID, the measured temperature value, and the timestamp of the reading.
 * Immutable once created.
 */
public class TemperatureReading implements ITemperatureReading
{
    private int sensorID = 0;     // ID of the sensor that produced this reading
    private double value = 0;     // Measured temperature value
    private String timestamp = null; // Timestamp of the reading

    /**
     * Constructs a TemperatureReading with the specified sensor ID, value, and timestamp.
     *
     * @param sensorID ID of the sensor
     * @param value Measured temperature value
     * @param timestamp Timestamp of the reading
     */
    public TemperatureReading(int sensorID, double value, String timestamp)
    {
        this.sensorID = sensorID;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Returns the sensor ID associated with this reading.
     *
     * @return sensor ID
     */
    @Override
    public int GetSensorID()
    {
        return sensorID;
    }

    /**
     * Returns the temperature value of this reading.
     *
     * @return temperature value
     */
    @Override
    public double GetValue()
    {
        return value;
    }
    
    /**
     * Returns the timestamp of this reading.
     *
     * @return timestamp string
     */
    @Override
    public String GetTimestamp()
    {
        return timestamp;
    }
}