package com.thermo.implementations;

import com.thermo.interfaces.IRoom;
import com.thermo.interfaces.IRoomPartition;
import java.util.concurrent.Semaphore;

public class Room implements  IRoom
{
    public class RoomPartition implements  IRoomPartition
    {
        private int partitionID = 0;
        private double[] temperature = null;
        private Semaphore[] mutex = null;

        public RoomPartition(int partitionID, double[] temperature, Semaphore[] mutex)
        {
            this.partitionID = partitionID;
            this.temperature = temperature;
            this.mutex = mutex;
        }

        @Override
        public int GetPartitionID()
        {
            return partitionID;
        }

        @Override
        public void SetPartitionID(int partitionID)
        {
            this.partitionID = partitionID;
        }

        @Override
        public double GetTemperature()
        {
            return temperature[0];
        }

        @Override
        public void SetTemperature(double temperature)
        {
            this.temperature[0] = temperature;
        }

        @Override
        public Semaphore[] GetSemaphore()
        {
            return mutex;
        }

        @Override
        public void SetSemaphore(Semaphore[] mutex)
        {
            this.mutex = mutex;
        }

        @Override
        public int GetRoomPartitionID()
        {
            return partitionID;
        }
        
    }

    private RoomPartition[] roomPartitions = null;
    private Semaphore[][] mutexes = null;
    private Sensor[] sensors = null;
    private double[][] temperatures = null;

    public Room(int numberOfRoomPartitions, double currentTemperature)
    {
        roomPartitions = new RoomPartition[numberOfRoomPartitions];
        mutexes = new Semaphore[numberOfRoomPartitions][1];
        sensors = new Sensor[numberOfRoomPartitions];
        temperatures = new double[numberOfRoomPartitions][1];

        for(int i = 0; i < numberOfRoomPartitions; i++)
        {
            mutexes[i][0] = new Semaphore(1, true);
            temperatures[i][0] = currentTemperature;
            roomPartitions[i] = new RoomPartition(i, temperatures[i], mutexes[i]);
            sensors[i] = new Sensor(temperatures[i], mutexes[i]);
        }
    }

    @Override
    public RoomPartition[] GetRoomPartitions()
    {
        return roomPartitions;
    }

    @Override
    public Semaphore[][] GetMutexes()
    {
        return mutexes;
    }

    @Override
    public Sensor[] GetSensors()
    {
        return sensors;
    }

    @Override
    public double[][] GetTemperatures()
    {
        return temperatures;
    }
}
