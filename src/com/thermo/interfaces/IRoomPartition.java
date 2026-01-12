package com.thermo.interfaces;

public interface IRoomPartition
{
    public int GetRoomPartitionID();
    public double GetTemperature();
    public void UpdateTemperature(double difference);
}
