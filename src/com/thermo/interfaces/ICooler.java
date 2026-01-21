package com.thermo.interfaces;

/**
 * Interface representing a cooler actuator in the simulation.
 * 
 * Implementing classes should define the behavior for cooling the room 
 * and provide a method to start the cooler's thread.
 */
public interface ICooler
{
    /**
     * Starts the cooler's operation in a separate thread.
     */
    public void Start();
}