package com.thermo.implementations.exceptions;

/**
 * Contains standardized exception and error messages used throughout the Thermo simulation system.
 * All fields are static constants to allow easy reuse and consistency across different classes.
 */
public abstract class Exceptions
{
    /** Timer stop was interrupted unexpectedly. */
    public static final String TIMER_STOP_INTERRUPTED = "TIMER_STOP_INTERRUPTED";

    /** Reading temperature from a sensor failed. */
    public static final String SENSOR_READ_FAILED = "SENSOR_READ_FAILED";

    /** Popping a temperature reading from the buffer failed. */
    public static final String SENSOR_READING_POP_FAILED = "SENSOR_READING_POP_FAILED";

    /** Pushing a temperature reading to the buffer failed. */
    public static final String SENSOR_READING_PUSH_FAILED = "SENSOR_READING_PUSH_FAILED";

    /** ActuatorControl singleton was already initialized. */
    public static final String ACTUATORCONTROL_ALREADY_INITIALIZED = "ACTUATORCONTROL_ALREADY_INITIALIZED";

    /** ActuatorControl singleton is not yet initialized. */
    public static final String ACTUATORCONTROL_NOT_INITIALIZED = "ActuatorControl not initialized. Call ActuatorControl.Init() first.";

    /** Config singleton was already initialized. */
    public static final String CONFIG_ALREADY_INITIALIZED = "CONFIG_ALREADY_INITIALIZED";

    /** Config singleton is not yet initialized. */
    public static final String CONFIG_NOT_INITIALIZED = "Config not initialized. Call Config.Init() first.";

    /** Heater thread was interrupted during heating application. */
    public static final String HEATER_INTERRUPT = "Thread interrupted while applying heating";

    /** Cooler thread was interrupted during cooling application. */
    public static final String COOLER_INTERRUPT = "Thread interrupted while applying cooling";

    /** Failed to open a file (e.g., config or log file). */
    public static final String FILE_OPEN_FAIL = "FILE_OPEN_FAIL";

    /** Logger singleton was already initialized. */
    public static final String LOGGER_ALREADY_INITIALIZED = "LOGGER_ALREADY_INITIALIZED";

    /** Logger singleton is not yet initialized. */
    public static final String LOGGER_NOT_INITIALIZED = "Logger not initialized. Call Logger.Init() first.";

    /** Temperature grid provided is null. */
    public static final String TEMP_GRID_NULL = "Temperature grid must not be null";

    /** Heat transfer coefficient must be non-negative. */
    public static final String HEAT_TRANSFER_COEFFICIENT_NULL = "Heat transfer coefficient must be non-negative";

    /** Outside temperature influence thread was interrupted while applying ambient effect. */
    public static final String OUTSIDETEMPERATUREINFLUENCE_INTERRUPT = "Thread interrupted while applying ambient effect";

    /** Temperature bounds are invalid: lower bound greater than upper bound. */
    public static final String INVALID_TEMP_BOUNDS = "Lower bound must not be greater than upper bound";
}