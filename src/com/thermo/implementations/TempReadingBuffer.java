package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ITempReadingBuffer;
import java.util.concurrent.Semaphore;

/**
 * Thread-safe circular buffer for storing sensor temperature readings.
 *
 * Supports adding readings to the back of the queue, retrieving all readings, 
 * and popping the oldest reading. Uses a semaphore to prevent concurrent access issues.
 */
public class TempReadingBuffer implements ITempReadingBuffer
{
    private SensorReading[] queue = null;           // Array holding the readings
    private int queueStart = 0;                     // Index of the first reading in the queue
    private int queueEnd = 0;                       // Index of the next slot to insert a reading
    private int size = 0;                           // Current number of readings in the queue
    private Integer nextFreeSlot = 0;               // Index of the next available slot for insertion
    private final Semaphore semaphore;              // Semaphore to protect concurrent access

    /**
     * Constructs a temperature reading buffer with the specified capacity.
     * Defaults to 64 if the given capacity is null or invalid.
     *
     * @param queueCapacity Maximum number of readings the buffer can hold
     */
    public TempReadingBuffer(Integer queueCapacity)
    {
        if(queueCapacity == null || queueCapacity <= 0)
            queueCapacity = 64;
        queue = new SensorReading[queueCapacity];
        semaphore = new Semaphore(1); 
    }

    /**
     * Returns the current number of readings stored in the buffer.
     *
     * @return number of readings
     */
    @Override
    public int GetSize()
    {
        return size;
    }

    /**
     * Removes and returns the oldest reading from the buffer.
     *
     * @return oldest SensorReading, or null if the buffer is empty
     */
    public SensorReading PopFront()
    {
        SensorReading removedReading = null;
        boolean acquired = false;
        try
        {
            semaphore.acquire();
            acquired = true;
            if(GetSize() < 1)
            {
                return null; // Buffer empty
            }
            int readingIndex = queueStart; 
            removedReading = queue[readingIndex]; 
            queue[readingIndex] = null; 
            queueStart = (queueStart + 1) % queue.length; // Circular increment
            if(!HasSpace()) // If buffer was full before, reset next free slot
                nextFreeSlot = readingIndex;
            size--;
            return removedReading;
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_POP_FAILED + ": " + e.toString());
            Thread.currentThread().interrupt();
        }
        finally
        {
            if (acquired)
            semaphore.release();
        }
        return removedReading;
    }

    /**
     * Retrieves and removes all current readings from the buffer.
     *
     * @return array of SensorReadings, or null if buffer is empty
     */
    public SensorReading[] RetrieveReadings()
    {
        SensorReading[] readings = null;
        boolean acquired = false;
        try
        {
            semaphore.acquire();
            acquired = true;
            if(GetSize() < 1)
            {
                return null; // Buffer empty
            }
            int range = size;
            readings = new SensorReading[size];
            for(int i = 0; i < range; i++)
            {    
                int readingIndex = queueStart; 
                readings[i] = queue[readingIndex]; 
                queue[readingIndex] = null; 
                queueStart = (queueStart + 1) % queue.length;
                if(!HasSpace())
                    nextFreeSlot = readingIndex;
                size--;
            }
            return readings;
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_POP_FAILED + ": " + e.toString());
            Thread.currentThread().interrupt();
        }
        finally
        {
            if(acquired)
                semaphore.release();
        }
        return readings;
    }

    /**
     * Adds a new reading to the back of the buffer if space is available.
     *
     * @param reading the SensorReading to add
     */
    public void PushBack(SensorReading reading)
    {
        if(reading == null)
            return;
        boolean acquired = false;
        try
        {
            semaphore.acquire();
            acquired = true;
            if(!HasSpace())
                return; // Buffer full, cannot add
            queue[nextFreeSlot] = reading;
            size++;
            queueEnd = (queueEnd + 1) % queue.length;
            if(size == queue.length)
                nextFreeSlot = null; // Buffer now full
            else
                nextFreeSlot = queueEnd;
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_PUSH_FAILED + ": " + e.toString());
            Thread.currentThread().interrupt();
        }
        finally
        {
            if(acquired)
                semaphore.release();
        }
    }

    /**
     * Checks if the buffer has available space for new readings.
     *
     * @return true if there is space, false if full
     */
    @Override
    public boolean HasSpace()
    {
        return size < queue.length;
    }

    /**
     * Checks if the buffer is empty.
     *
     * @return true if empty, false otherwise
     */
    @Override
    public boolean IsEmpty()
    {
        return size == 0;
    }
}