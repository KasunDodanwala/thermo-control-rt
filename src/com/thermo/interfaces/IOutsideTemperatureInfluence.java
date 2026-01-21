package com.thermo.interfaces;

/**
 * Interface representing the influence of outside temperature on the room.
 * 
 * Implementing classes should simulate the effect of the outside temperature 
 * on the internal room temperatures and provide a method to start the simulation.
 */
public interface IOutsideTemperatureInfluence
{
    /**
     * Starts the outside temperature influence simulation in a separate thread.
     */
    public void Start();
}