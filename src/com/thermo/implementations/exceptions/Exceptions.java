package com.thermo.implementations.exceptions;

public abstract class Exceptions
{
    public static final String TIMER_STOP_INTERRUPTED = "TIMER_STOP_INTERRUPTED";
    public static final String SENSOR_READ_FAILED = "SENSOR_READ_FAILED";
    public static final String SENSOR_READING_POP_FAILED = "SENSOR_READING_POP_FAILED";
    public static final String SENSOR_READING_PUSH_FAILED = "SENSOR_READING_PUSH_FAILED";
    public static final String ACTUATORCONTROL_ALREADY_INITIALIZED = "ACTUATORCONTROL_ALREADY_INITIALIZED";
    public static final String ACTUATORCONTROL_NOT_INITIALIZED = "ActuatorControl not initialized. Call ActuatorControl.Init() first.";
    public static final String CONFIG_ALREADY_INITIALIZED = "CONFIG_ALREADY_INITIALIZED";
    public static final String CONFIG_NOT_INITIALIZED = "Config not initialized. Call Config.Init() first.";
    public static final String HEATER_INTERRUPT = "Thread interrupted while applying heating";
    public static final String COOLER_INTERRUPT = "Thread interrupted while applying cooling";
    public static final String FILE_OPEN_FAIL = "FILE_OPEN_FAIL";
    public static final String LOGGER_ALREADY_INITIALIZED = "LOGGER_ALREADY_INITIALIZED";
    public static final String LOGGER_NOT_INITIALIZED = "Logger not initialized. Call Logger.Init() first.";
    public static final String TEMP_GRID_NULL = "Temperature grid must not be null";
    public static final String HEAT_TRANSFER_COEFFICIENT_NULL = "Heat transfer coefficient must be non-negative";
    public static final String OUTSIDETEMPERATUREINFLUENCE_INTERRUPT = "Thread interrupted while applying ambient effect";
    public static final String INVALID_TEMP_BOUNDS = "Lower bound must not be greater than upper bound";
}

