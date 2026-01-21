package com.thermo.interfaces;

/**
 * Interface representing the sensor input system that reads temperatures
 * from all sensors in the room and records them into a buffer.
 */
public interface ISensorInput
{
    /**
     * Starts the sensor input thread to periodically read sensor temperatures
     * and push them to the temperature reading buffer.
     */
    public void Start();
}