package com.thermo.interfaces;

/**
 * Interface representing the simulator that orchestrates the thermal system simulation.
 * It is responsible for initializing all components and starting the simulation.
 */
public interface ISimulator
{
    /**
     * Starts the simulation, including all sensors, actuators, controllers,
     * ambient influence, and logging.
     */
    public void Start();
}