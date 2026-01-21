package com.thermo.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.thermo.implementations.exceptions.Exceptions;

/**
 * Centralized configuration manager for the simulation.
 *
 * This class is responsible for loading, storing, and providing access to all
 * configuration parameters defined in an external JSON file. It follows the
 * singleton pattern to ensure configuration data is loaded exactly once and
 * remains globally consistent throughout the simulation lifecycle.
 *
 * Responsibilities:
 * - Load configuration values from a JSON file at startup
 * - Expose read-only, static accessors for all configuration parameters
 * - Prevent reinitialization or access before initialization
 *
 * Jackson is used for JSON deserialization, with direct field access enabled
 * to avoid boilerplate setters.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class Config
{
    /**
     * Singleton instance of the configuration.
     */
    private static volatile Config INSTANCE;

    /** Duration of a simulation tick in milliseconds. */
    private int tickTimeMilliSeconds;

    /** Total simulation runtime in seconds. */
    private int runTimeSeconds;

    /** Simulation speed multiplier. */
    private int simSpeed;

    /** Outside ambient temperature. */
    private double outsideTemp;

    /** Initial inside temperature. */
    private double insideTemp;

    /** Temperature delta applied per tick by heater/cooler. */
    private double coolHeaterAffectPerTick;

    /** Upper bound for desired room temperature. */
    private double roomTempUpperBound;

    /** Lower bound for desired room temperature. */
    private double roomTempLowerBound;

    /** Number of room partitions in the simulation grid. */
    private int numberOfRoomPartitions;

    /** Capacity of the temperature reading buffer. */
    private int tempReadingBufferCapacity;

    /** Base sleep time for simulated threads in milliseconds. */
    private int threadSleepTimeMilliSeconds;

    /** Heat transfer coefficient for outside temperature influence. */
    private double heatTransferCoefficient;

    /** File path for logging output. */
    private String logFilePath;

    /** Flag indicating whether logs should also be printed to terminal. */
    private boolean terminalLogs;

    /**
     * Private constructor to enforce singleton usage.
     */
    private Config(){}

    // -------- Initialization --------

    /**
     * Initializes the configuration by loading values from a JSON file.
     *
     * This method must be called exactly once before any configuration values
     * are accessed.
     *
     * @param jsonFilePath Path to the JSON configuration file
     *
     * @throws IllegalStateException if the configuration is already initialized
     * @throws RuntimeException if the file cannot be read or parsed
     */
    public static void Init(String jsonFilePath)
    {
        if(INSTANCE != null)
        {
            throw new IllegalStateException(Exceptions.CONFIG_ALREADY_INITIALIZED);
        }

        synchronized(Config.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException(Exceptions.CONFIG_ALREADY_INITIALIZED);
            }

            try
            {
                ObjectMapper mapper = new ObjectMapper();
                byte[] json = Files.readAllBytes(Path.of(jsonFilePath));
                INSTANCE = mapper.readValue(json, Config.class);
            }
            catch(Exception e)
            {
                throw new RuntimeException(
                    Exceptions.FILE_OPEN_FAIL + "(" + jsonFilePath + "): " + e
                );
            }
        }
    }

    /**
     * Returns the singleton Config instance.
     *
     * @return Initialized Config instance
     *
     * @throws IllegalStateException if Init() has not been called
     */
    public static Config GetInstance()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException(Exceptions.CONFIG_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    /**
     * @return Simulation tick duration in milliseconds
     */
    public static int GetTickTimeMilliSeconds()
    {
        return GetInstance().tickTimeMilliSeconds;
    }

    /**
     * @return Total simulation runtime in seconds
     */
    public static int GetRunTimeSeconds()
    {
        return GetInstance().runTimeSeconds;
    }

    /**
     * @return Simulation speed multiplier
     */
    public static int GetSimSpeed()
    {
        return GetInstance().simSpeed;
    }

    /**
     * @return Outside ambient temperature
     */
    public static double GetOutsideTemp()
    {
        return GetInstance().outsideTemp;
    }

    /**
     * @return Initial inside temperature
     */
    public static double GetInsideTemp()
    {
        return GetInstance().insideTemp;
    }

    /**
     * @return Temperature delta applied per tick by heater or cooler
     */
    public static double GetCoolHeaterAffectPerTick()
    {
        return GetInstance().coolHeaterAffectPerTick;
    }

    /**
     * @return Upper bound of acceptable room temperature
     */
    public static double GetRoomTempUpperBound()
    {
        return GetInstance().roomTempUpperBound;
    }

    /**
     * @return Lower bound of acceptable room temperature
     */
    public static double GetRoomTempLowerBound()
    {
        return GetInstance().roomTempLowerBound;
    }

    /**
     * @return Number of room partitions
     */
    public static int GetNumberOfRoomPartitions()
    {
        return GetInstance().numberOfRoomPartitions;
    }

    /**
     * @return Capacity of the temperature reading buffer
     */
    public static int GetTempReadingBufferCapacity()
    {
        return GetInstance().tempReadingBufferCapacity;
    }

    /**
     * @return Base sleep time for simulated threads in milliseconds
     */
    public static int GetThreadSleepTimeMilliSeconds()
    {
        return GetInstance().threadSleepTimeMilliSeconds;    
    }

    /**
     * @return Heat transfer coefficient for ambient temperature influence
     */
    public static double GetHeatTransferCoefficient()
    {
        return GetInstance().heatTransferCoefficient;
    }

    /**
     * @return Log file path
     */
    public static String GetLogFilePath()
    {
        return GetInstance().logFilePath;
    }

    /**
     * @return True if terminal logging is enabled
     */
    public static boolean GetTerminalLogs()
    {
        return GetInstance().terminalLogs;
    }
}