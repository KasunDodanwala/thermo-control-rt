package com.thermo.interfaces;

import java.util.concurrent.Semaphore;

/**
 * Interface representing a single partition within a room.
 * 
 * Provides methods to access and modify the partition's ID, temperature,
 * and its associated semaphores for thread-safe operations.
 */
public interface IRoomPartition
{
    /**
     * Returns the partition's ID.
     *
     * @return Partition ID as an integer.
     */
    public int GetPartitionID();

    /**
     * Sets the partition's ID.
     *
     * @param partitionID The new partition ID.
     */
    public void SetPartitionID(int partitionID);

    /**
     * Returns the current temperature of the partition.
     *
     * @return Temperature as a double.
     */
    public double GetTemperature();

    /**
     * Sets the temperature of the partition.
     *
     * @param temperature The new temperature value.
     */
    public void SetTemperature(double temperature);

    /**
     * Returns the semaphores associated with this partition for synchronization.
     *
     * @return Array of Semaphore objects.
     */
    public Semaphore[] GetSemaphore();

    /**
     * Sets the semaphores associated with this partition for synchronization.
     *
     * @param mutex Array of Semaphore objects.
     */
    public void SetSemaphore(Semaphore[] mutex);

    /**
     * Returns the room partition ID (alias for GetPartitionID).
     *
     * @return Partition ID as an integer.
     */
    public int GetRoomPartitionID();
}