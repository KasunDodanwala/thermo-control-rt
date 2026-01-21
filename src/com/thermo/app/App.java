package com.thermo.app;

import com.thermo.implementations.Config;
import com.thermo.implementations.Simulator;

/**
 * Entry point for the Thermo simulation application.
 * 
 * Loads configuration from "config.json", initializes the system, and starts the simulation.
 */
public class App
{
    public static void main(String[] args)
    {
        // Load simulation configuration from JSON file
        Config.Init("config.json");

        // Create and start the simulator
        Simulator sim = new Simulator();
        sim.Start();
    }
}