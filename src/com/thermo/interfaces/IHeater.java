package com.thermo.interfaces;

/**
 * Interface representing a heater actuator in the simulation.
 * 
 * Implementing classes should define the behavior for heating the room 
 * and provide a method to start the heater's thread.
 */
public interface IHeater
{
    /**
     * Starts the heater's operation in a separate thread.
     */
    public void Start();
}