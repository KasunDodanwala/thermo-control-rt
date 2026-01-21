package com.thermo.implementations;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.thermo.implementations.exceptions.Exceptions;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class Config
{
    // -------- Singleton instance --------
    private static volatile Config INSTANCE;

    // -------- Config fields --------
    private int tickTimeMilliSeconds;
    private int runTimeSeconds;
    private int simSpeed;
    private double outsideTemp;
    private double insideTemp;
    private double coolHeaterAffectPerTick;
    private double roomTempUpperBound;
    private double roomTempLowerBound;
    private int numberOfRoomPartitions;
    private int tempReadingBufferCapacity;
    private int threadSleepTimeMilliSeconds;
    private double heatTransferCoefficient;
    private String logFilePath;
    private boolean terminalLogs;

    private Config(){}

    // -------- Initialization --------
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
                throw new RuntimeException(Exceptions.FILE_OPEN_FAIL + "(" + jsonFilePath + "): " + e);
            }
        }
    }

    public static Config GetInstance()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException(Exceptions.CONFIG_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    public static int GetTickTimeMilliSeconds()
    {
        return GetInstance().tickTimeMilliSeconds;
    }

    public static int GetRunTimeSeconds()
    {
        return GetInstance().runTimeSeconds;
    }

    public static int GetSimSpeed()
    {
        return GetInstance().simSpeed;
    }

    public static double GetOutsideTemp()
    {
        return GetInstance().outsideTemp;
    }

    public static double GetInsideTemp()
    {
        return GetInstance().insideTemp;
    }

    public static double GetCoolHeaterAffectPerTick()
    {
        return GetInstance().coolHeaterAffectPerTick;
    }

    public static double GetRoomTempUpperBound()
    {
        return GetInstance().roomTempUpperBound;
    }

    public static double GetRoomTempLowerBound()
    {
        return GetInstance().roomTempLowerBound;
    }

    public static int GetNumberOfRoomPartitions()
    {
        return GetInstance().numberOfRoomPartitions;
    }

    public static int GetTempReadingBufferCapacity()
    {
        return GetInstance().tempReadingBufferCapacity;
    }

    public static int GetThreadSleepTimeMilliSeconds()
    {
        return GetInstance().threadSleepTimeMilliSeconds;    
    }

    public static double GetHeatTransferCoefficient()
    {
        return GetInstance().heatTransferCoefficient;
    }

    public static String GetLogFilePath()
    {
        return GetInstance().logFilePath;
    }

    public static boolean GetTerminalLogs()
    {
        return GetInstance().terminalLogs;
    }
}