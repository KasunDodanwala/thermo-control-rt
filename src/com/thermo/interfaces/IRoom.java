package com.thermo.interfaces;

import com.thermo.implementations.Room.RoomPartition;
import com.thermo.implementations.Sensor;
import java.util.concurrent.Semaphore;

/**
 * Interface representing a room divided into partitions.
 * 
 * Provides access to the room partitions, sensors, temperature values, 
 * and synchronization primitives (mutexes) for thread-safe temperature updates.
 */
public interface IRoom
{
    /**
     * Returns an array of RoomPartition objects representing the room's partitions.
     *
     * @return Array of RoomPartition objects.
     */
    public RoomPartition[] GetRoomPartitions();

    /**
     * Returns a 2D array of Semaphores used for thread-safe access to each partition's temperature.
     *
     * @return 2D array of Semaphores.
     */
    public Semaphore[][] GetMutexes();

    /**
     * Returns an array of Sensor objects associated with each room partition.
     *
     * @return Array of Sensor objects.
     */
    public Sensor[] GetSensors();

    /**
     * Returns the 2D array of temperature values for all room partitions.
     *
     * @return 2D array of temperatures.
     */
    public double[][] GetTemperatures();
}