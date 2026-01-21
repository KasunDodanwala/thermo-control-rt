package com.thermo.interfaces;

/**
 * Interface representing a temperature controller that monitors the room temperature
 * and controls heating and cooling actuators to maintain temperature within specified bounds.
 */
public interface ITemperatureController
{
    /**
     * Starts the temperature control loop in a separate thread.
     * Continuously evaluates the room temperature and adjusts actuators accordingly.
     */
    public void Start();
}