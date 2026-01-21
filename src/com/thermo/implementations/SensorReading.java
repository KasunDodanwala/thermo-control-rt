package com.thermo.implementations;

/**
 * Represents a single sensor reading containing the sensor ID, 
 * the measured temperature value, and the timestamp when the reading 
 * was recorded.
 */
public class SensorReading
{
    public int SensorId;       // Unique ID of the sensor
    public double Value;       // Temperature value recorded by the sensor
    public String Timestamp;   // Time at which the reading was taken

    /**
     * Constructs a SensorReading with the given sensor ID, temperature value, and timestamp.
     *
     * @param SensorId Unique ID of the sensor
     * @param Value Temperature value recorded
     * @param Timestamp Timestamp of the reading
     */
    public SensorReading(int SensorId, double Value, String Timestamp)
    {
        this.SensorId = SensorId;
        this.Value = Value;
        this.Timestamp = Timestamp;
    }
}