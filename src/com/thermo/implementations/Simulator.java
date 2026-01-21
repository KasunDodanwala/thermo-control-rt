package com.thermo.implementations;

public class Simulator
{
    public void Start()
    {

        System.out.println("Loaded Settings");
        System.out.println("====================");

        System.out.println("tickTimeMilliSeconds = " + Config.GetTickTimeMilliSeconds());
        System.out.println("runTimeSeconds = " + Config.GetRunTimeSeconds());
        System.out.println("simSpeed = " + Config.GetSimSpeed());
        System.out.println("outsideTemp = " + Config.GetOutsideTemp());
        System.out.println("insideTemp = " + Config.GetInsideTemp());
        System.out.println("coolHeaterAffectPerTick = " + Config.GetCoolHeaterAffectPerTick());
        System.out.println("roomTempUpperBound = " + Config.GetRoomTempUpperBound());
        System.out.println("roomTempLowerBound = " + Config.GetRoomTempLowerBound());
        System.out.println("numberOfRoomPartitions = " + Config.GetNumberOfRoomPartitions());
        System.out.println("tempReadingBufferCapacity = " + Config.GetTempReadingBufferCapacity());
        System.out.println("threadSleepTimeMilliSeconds = " + Config.GetThreadSleepTimeMilliSeconds());
        System.out.println("heatTransferCoefficient = " + Config.GetHeatTransferCoefficient());
        System.out.println("logFilePath = " + Config.GetLogFilePath());
        System.out.println("terminalLogs = " + Config.GetTerminalLogs());

        Timer.Init(Config.GetThreadSleepTimeMilliSeconds(), Config.GetSimSpeed(), Config.GetRunTimeSeconds() * 1000, Config.GetTickTimeMilliSeconds());
        Room room = new Room(Config.GetNumberOfRoomPartitions(), Config.GetOutsideTemp());
        TempReadingBuffer buff = new TempReadingBuffer(Config.GetTempReadingBufferCapacity());
        SensorInput sensorInput = new SensorInput(room.GetSensors(), buff, Config.GetThreadSleepTimeMilliSeconds());
        ActuatorControl.Init(room.GetTemperatures(), Config.GetCoolHeaterAffectPerTick(), room.GetMutexes());
        TemperatureController temperatureController = new TemperatureController(Config.GetRoomTempUpperBound(), Config.GetRoomTempLowerBound(), buff);
        Logger.Init(Config.GetLogFilePath(), Config.GetTerminalLogs());
        OutsideTemperatureInfluence outsideTemperatureInfluence = new OutsideTemperatureInfluence(room.GetTemperatures(), Config.GetOutsideTemp(), Config.GetHeatTransferCoefficient(), room.GetMutexes());

        sensorInput.Start();
        temperatureController.Start();
        Logger.GetINSTANCE().Start();
        outsideTemperatureInfluence.Start();

        System.out.println("\nSim Starting");
        System.out.println("\n====================\n");

        Timer.Start();

        Timer.WaitFor(Timer.GetRemainingTime() + 5000);

        System.out.println("\nSim Ended");
        System.out.println("====================");
        System.out.println("Sim ran for " + Timer.GetTimeInMinutesAndSeconds());
        {
            double[][] temps = room.GetTemperatures();
            for(int i = 0; i < temps.length; i++)
            {
                System.out.println("Room partition " + i + " Temp: " + String.format("%.2f", temps[i][0]) + " C");
            }
        }
    }
}
