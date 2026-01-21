package com.thermo.app;
import com.thermo.implementations.Config;
import com.thermo.implementations.Simulator;

public class App
{
    public static void main(String[] args)
    {
        Config.Init("config.json");

        Simulator sim = new Simulator();
        sim.Start();
    }
}
