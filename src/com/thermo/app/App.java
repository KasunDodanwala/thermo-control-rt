package com.thermo.app;
import com.thermo.implementations.Config;
import com.thermo.implementations.Simulator;

public class App
{
    public static void main(String[] args)
    {
        Config.Init("config.json"); 

        // System.out.println("tickTimeMilliSeconds = " + Config.GetTickTimeMilliSeconds());
        // System.out.println("runTimeSeconds = " + Config.GetRunTimeSeconds());
        // System.out.println("simSpeed = " + Config.GetSimSpeed());
        // System.out.println("outsideTemp = " + Config.GetOutsideTemp());
        // System.out.println("insideTemp = " + Config.GetInsideTemp());
        // System.out.println("coolHeaterAffectPerTick = " + Config.GetCoolHeaterAffectPerTick());
        // System.out.println("roomTempUpperBound = " + Config.GetRoomTempUpperBound());
        // System.out.println("roomTempLowerBound = " + Config.GetRoomTempLowerBound());
        // System.out.println("numberOfRoomPartitions = " + Config.GetNumberOfRoomPartitions());
        // System.out.println("tempReadingBufferCapacity = " + Config.GetTempReadingBufferCapacity());
        // System.out.println("threadSleepTimeMilliSeconds = " + Config.GetThreadSleepTimeMilliSeconds());
        // System.out.println("heatTransferCoefficient = " + Config.GetHeatTransferCoefficient());
        // System.out.println("logFilePath = " + Config.GetLogFilePath());
        // System.out.println("terminalLogs = " + Config.GetTerminalLogs());

        Simulator sim = new Simulator();
        sim.Start();
    }
}
