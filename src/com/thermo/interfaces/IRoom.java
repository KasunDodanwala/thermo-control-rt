package com.thermo.interfaces;

import com.thermo.implementations.Room.RoomPartition;
import com.thermo.implementations.Sensor;
import java.util.concurrent.Semaphore;

public interface IRoom
{
    public RoomPartition[] GetRoomPartitions();

    public Semaphore[][] GetMutexes();

    public Sensor[] GetSensors();

    public double[][] GetTemperatures();
}
