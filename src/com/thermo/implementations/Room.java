package com.thermo.implementations;

import com.thermo.interfaces.IRoom;
import com.thermo.interfaces.IRoomPartition;
import java.util.concurrent.Semaphore;

/**
 * Represents a room composed of multiple independent partitions.
 *
 * This class models the physical structure of the room by dividing it into
 * partitions, each with its own temperature state, sensor, and synchronization
 * mechanism. The room acts as the central shared data structure for temperature
 * simulation, providing access to partitions, sensors, mutexes, and raw
 * temperature data.
 *
 * Design characteristics:
 * - Each room partition has an independent temperature cell
 * - Each temperature cell is protected by a binary semaphore
 * - Sensors and actuators operate on shared temperature references
 *
 * Concurrency:
 * - Semaphores ensure safe concurrent access to each partition's temperature
 * - Fine-grained locking (per partition) avoids global contention
 */
public class Room implements IRoom
{
    /**
     * Represents a single partition of the room.
     *
     * Each partition maintains:
     * - A unique partition ID
     * - A shared temperature reference
     * - A semaphore protecting that temperature
     */
    public class RoomPartition implements IRoomPartition
    {
        private int partitionID = 0;
        private double[] temperature = null;
        private Semaphore[] mutex = null;

        /**
         * Constructs a room partition.
         *
         * @param partitionID Unique identifier for the partition
         * @param temperature Shared temperature reference
         * @param mutex Semaphore guarding access to the temperature
         */
        public RoomPartition(int partitionID, double[] temperature, Semaphore[] mutex)
        {
            this.partitionID = partitionID;
            this.temperature = temperature;
            this.mutex = mutex;
        }

        /**
         * Returns the partition ID.
         *
         * @return Partition identifier
         */
        @Override
        public int GetPartitionID()
        {
            return partitionID;
        }

        /**
         * Sets the partition ID.
         *
         * @param partitionID New partition identifier
         */
        @Override
        public void SetPartitionID(int partitionID)
        {
            this.partitionID = partitionID;
        }

        /**
         * Returns the current temperature of the partition.
         *
         * @return Temperature value
         */
        @Override
        public double GetTemperature()
        {
            return temperature[0];
        }

        /**
         * Updates the temperature of the partition.
         *
         * @param temperature New temperature value
         */
        @Override
        public void SetTemperature(double temperature)
        {
            this.temperature[0] = temperature;
        }

        /**
         * Returns the semaphore guarding this partition.
         *
         * @return Semaphore array for synchronization
         */
        @Override
        public Semaphore[] GetSemaphore()
        {
            return mutex;
        }

        /**
         * Sets the semaphore guarding this partition.
         *
         * @param mutex Semaphore array
         */
        @Override
        public void SetSemaphore(Semaphore[] mutex)
        {
            this.mutex = mutex;
        }

        /**
         * Returns the room partition ID.
         *
         * @return Partition identifier
         */
        @Override
        public int GetRoomPartitionID()
        {
            return partitionID;
        }
    }

    /** Array of room partitions. */
    private RoomPartition[] roomPartitions = null;

    /** Semaphore matrix protecting each partition's temperature. */
    private Semaphore[][] mutexes = null;

    /** Sensors associated with each room partition. */
    private Sensor[] sensors = null;

    /** Shared temperature grid for all partitions. */
    private double[][] temperatures = null;

    /**
     * Constructs a room with the specified number of partitions.
     *
     * Each partition is initialized with:
     * - The same starting temperature
     * - Its own semaphore
     * - A dedicated sensor
     *
     * @param numberOfRoomPartitions Number of partitions in the room
     * @param currentTemperature Initial temperature for all partitions
     */
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

    /**
     * Returns all room partitions.
     *
     * @return Array of room partitions
     */
    @Override
    public RoomPartition[] GetRoomPartitions()
    {
        return roomPartitions;
    }

    /**
     * Returns the semaphore matrix guarding partition temperatures.
     *
     * @return Semaphore matrix
     */
    @Override
    public Semaphore[][] GetMutexes()
    {
        return mutexes;
    }

    /**
     * Returns all sensors associated with the room.
     *
     * @return Sensor array
     */
    @Override
    public Sensor[] GetSensors()
    {
        return sensors;
    }

    /**
     * Returns the raw temperature grid.
     *
     * @return 2D temperature array
     */
    @Override
    public double[][] GetTemperatures()
    {
        return temperatures;
    }
}