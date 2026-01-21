package com.thermo.implementations;

/**
 * Represents the state of an actuator (heater or cooler).
 * Each state indicates whether the actuator is actively affecting temperature.
 */
public enum ActuatorState
{
    /** Heater is active, increasing temperature. */
    HEATING
    {
        @Override
        public boolean isActive()
        {
            return true;
        }
    },

    /** Cooler is active, decreasing temperature. */
    COOLING
    {
        @Override
        public boolean isActive()
        {
            return true;
        }
    },

    /** Actuator is turned off and inactive. */
    OFF
    {
        @Override
        public boolean isActive()
        {
            return false;
        }
    };

    /**
     * Returns whether the actuator is currently active (heating or cooling).
     *
     * @return True if the actuator is active, false if OFF.
     */
    public abstract boolean isActive();
}