package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import java.util.concurrent.Semaphore;

/**
 * Central controller responsible for managing thermal actuators (heater and cooler).
 *
 * This class acts as a singleton façade over the Heater and Cooler components,
 * exposing high-level operations to enable or disable heating and cooling based
 * on decisions made by the TemperatureController.
 *
 * Responsibilities:
 * - Owns and initializes Heater and Cooler instances
 * - Maintains the current actuator state (OFF, HEATING, COOLING)
 * - Ensures thread-safe singleton initialization
 * - Logs all actuator state transitions with simulation timestamps
 *
 * The Heater and Cooler operate on shared temperature data protected by
 * per-partition semaphores to avoid race conditions.
 */
public class ActuatorControl
{
    /**
     * Singleton instance of ActuatorControl.
     */
    private static volatile ActuatorControl INSTANCE = null;

    /**
     * Heater actuator responsible for increasing temperatures.
     */
    private Heater heater = null;

    /**
     * Cooler actuator responsible for decreasing temperatures.
     */
    private Cooler cooler = null;

    /**
     * Current actuator state.
     */
    public ActuatorState state = ActuatorState.OFF;

    /**
     * Private constructor to enforce singleton usage.
     *
     * @param temperatures Shared temperature grid for all room partitions
     * @param tempAffect Amount of temperature change applied per tick
     * @param mutexes Binary semaphores protecting temperature access
     */
    private ActuatorControl(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        this.cooler = new Cooler(temperatures, tempAffect, mutexes);
        this.heater = new Heater(temperatures, tempAffect, mutexes);
    }

    /**
     * Initializes the ActuatorControl singleton and starts actuator threads.
     *
     * This method must be called exactly once before any actuator operations
     * are performed.
     *
     * @param temperatures Shared temperature grid for all room partitions
     * @param tempAffect Amount of temperature change applied per tick
     * @param mutexes Binary semaphores protecting temperature access
     *
     * @throws IllegalStateException if the controller is already initialized
     */
    public static void Init(double[][] temperatures, double tempAffect, Semaphore[][] mutexes)
    {
        if(INSTANCE != null)
        {
            throw new IllegalStateException(Exceptions.ACTUATORCONTROL_ALREADY_INITIALIZED);
        }

        synchronized(ActuatorControl.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException(Exceptions.ACTUATORCONTROL_ALREADY_INITIALIZED);
            }

            INSTANCE = new ActuatorControl(temperatures, tempAffect, mutexes);
            INSTANCE.cooler.Start();
            INSTANCE.heater.Start();
        }
    }

    /**
     * Returns the singleton instance of ActuatorControl.
     *
     * @return The initialized ActuatorControl instance
     *
     * @throws IllegalStateException if Init() has not been called
     */
    public static ActuatorControl GetINSTANCE()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException(Exceptions.ACTUATORCONTROL_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    /**
     * Enables the heater and updates the actuator state to HEATING.
     * Logs the state transition with the current simulation timestamp.
     */
    public void TurnHeaterOn()
    {
        state = ActuatorState.HEATING;
        heater.Enable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nHeater Turned On\n"
        );
    }

    /**
     * Disables the heater and sets the actuator state to OFF.
     * Logs the state transition with the current simulation timestamp.
     */
    public void TurnHeaterOff()
    {
        state = ActuatorState.OFF;
        heater.Disable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nHeater Turned Off\n"
        );
    }

    /**
     * Enables the cooler and updates the actuator state to COOLING.
     * Logs the state transition with the current simulation timestamp.
     */
    public void TurnCoolerOn()
    {
        state = ActuatorState.COOLING;
        cooler.Enable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nCooler Turned On\n"
        );
    }

    /**
     * Disables the cooler and sets the actuator state to OFF.
     * Logs the state transition with the current simulation timestamp.
     */
    public void TurnCoolerOff()
    {
        state = ActuatorState.OFF;
        cooler.Disable();
        Logger.GetINSTANCE().Log(
            "Timestamp: " + Timer.GetTimeInMinutesAndSeconds() +
            "\nCooler Turned Off\n"
        );
    }

    /**
     * Returns the current actuator state.
     *
     * @return Current ActuatorState (OFF, HEATING, or COOLING)
     */
    public ActuatorState GetState()
    {
        return state;
    }
}