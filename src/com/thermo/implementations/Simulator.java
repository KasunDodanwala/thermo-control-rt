package com.thermo.implementations;

public class Simulator
{
    public void Start()
    {
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


        Timer.Start();

        Timer.WaitFor(Timer.GetRemainingTime() + 5000);
    }
}
