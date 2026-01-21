package com.thermo.interfaces;

import java.util.concurrent.Semaphore;

public interface IRoomPartition
{
    public int GetPartitionID();

    public void SetPartitionID(int partitionID);

    public double GetTemperature();

    public void SetTemperature(double temperature);

    public Semaphore[] GetSemaphore();

    public void SetSemaphore(Semaphore[] mutex);

    public int GetRoomPartitionID();
}
