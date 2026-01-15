package com.thermo.implementations;

public enum ActuatorState
{
    HEATING
    {
        @Override
        public boolean isActive()
        {
            return true;
        }
    },
    COOLING
    {
        @Override
        public boolean isActive()
        {
            return true;
        }
    },
    OFF
    {
        @Override
        public boolean isActive()
        {
            return false;
        }
    };

    public abstract boolean isActive();
}
