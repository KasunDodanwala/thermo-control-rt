package com.thermo.interfaces;

/**
 * Interface representing a temperature sensor within a room.
 * 
 * Provides a method to read the current temperature in a thread-safe manner.
 */
public interface ISensor
{
    /**
     * Reads the current temperature from the sensor.
     *
     * @return Temperature as a Double. Returns null if reading fails or is unavailable.
     */
    public Double ReadTemperature();
}