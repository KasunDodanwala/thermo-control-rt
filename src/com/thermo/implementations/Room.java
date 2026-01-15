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

        public int GetPartitionID()
        {
            return partitionID;
        }

        public void SetPartitionID(int partitionID)
        {
            this.partitionID = partitionID;
        }

        @Override
        public double GetTemperature()
        {
            return temperature[0];
        }

        public void SetTemperature(double temperature)
        {
            this.temperature[0] = temperature;
        }

        public Semaphore[] GetSemaphore()
        {
            return mutex;
        }

        public void SetSemaphore(Semaphore[] mutex)
        {
            this.mutex = mutex;
        }

        @Override
        public int GetRoomPartitionID() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'GetRoomPartitionID'");
        }

        @Override
        public void UpdateTemperature(double difference) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'UpdateTemperature'");
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

    public RoomPartition[] GetRoomPartitions()
    {
        return roomPartitions;
    }

    public Semaphore[][] GetMutexes()
    {
        return mutexes;
    }

    public Sensor[] GetSensors()
    {
        return sensors;
    }

    public double[][] GetTemperatures()
    {
        return temperatures;
    }
}
