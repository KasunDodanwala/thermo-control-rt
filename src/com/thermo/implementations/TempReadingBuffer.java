package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import com.thermo.interfaces.ITempReadingBuffer;
import java.util.concurrent.Semaphore;

public class TempReadingBuffer implements ITempReadingBuffer
{
    private SensorReading[] queue = null;
    private int queueStart = 0;                 // Index of the first reading in the queue
    private int queueEnd = 0;                   // Index of the next available slot for a reading in the queue
    private int size = 0;                       // Current size of the queue
    private Integer nextFreeSlot = 0;           // Index of the next available slot for a reading in the queue
    private final Semaphore semaphore;                

    // <==Attributes==>

    // <==Constructors==>

    /**
     * Constructor to initialize a new TempReadingBuffer with a given queue capacity.
     *
     * @param queueCapacity The maximum capacity of the reading queue on this TempReadingBuffer.
     */
    public TempReadingBuffer(Integer queueCapacity)
    {
        if(queueCapacity == null || queueCapacity <= 0)
            queueCapacity = 64;
        queue = new SensorReading[queueCapacity];
        semaphore = new Semaphore(1); 
    }

    // <==Constructors==>

    // <==Getters==>

    @Override
    public int GetSize()
    {
        return size;
    }

    // <==Getters==>

    // <==Methods==>

    public SensorReading PopFront()
    {
        SensorReading removedReading = null;
        try
        {
            semaphore.acquire();
            if(GetSize() < 1)
            {
                return null; // If the queue is empty, return null
            }
            int readingIndex = queueStart; 
            removedReading = queue[readingIndex]; 
            queue[readingIndex] = null; 
            queueStart = (queueStart + 1) % queue.length; // Move the start index forward in a circular manner
            if(!HasSpace()) // If there is no space left, update the next free slot
                nextFreeSlot = readingIndex;
            size--; // Decrement the size of the queue
            return removedReading;
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_POP_FAILED + ": " + e.toString());
        }
        finally
        {
            semaphore.release();
        }
        return removedReading;
    }

    public SensorReading[] RetrieveReadings()
    {
        SensorReading[] readings = null;
        try
        {
            semaphore.acquire();
            if(GetSize() < 1)
            {
                return null; // If the queue is empty, return null
            }
            int range = size;
            readings = new SensorReading[size];
            for(int i = 0; i < range; i++)
            {    int readingIndex = queueStart; 
                readings[i] = queue[readingIndex]; 
                queue[readingIndex] = null; 
                queueStart = (queueStart + 1) % queue.length; // Move the start index forward in a circular manner
                if(!HasSpace()) // If there is no space left, update the next free slot
                    nextFreeSlot = readingIndex;
                size--; // Decrement the size of the queue
            }
            return readings;
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_POP_FAILED + ": " + e.toString());
        }
        finally
        {
            semaphore.release();
        }
        return readings;

    }

    public void PushBack(SensorReading reading)
    {
        try
        {
            semaphore.acquire();
            if(!HasSpace())
                return; // If the queue is full, do not add the reading
            queue[nextFreeSlot] = reading; // Add the reading to the next available slot in the queue
            size++; // Increment the queue size
            queueEnd = (queueEnd + 1) % queue.length; // Update the end index in a circular manner
            if(size == queue.length) // If the queue is full, set nextFreeSlot to null
                nextFreeSlot = null;
            else
                nextFreeSlot = queueEnd; // Update nextFreeSlot to the end of the queue
        }
        catch(InterruptedException e)
        {
            System.out.println(Exceptions.SENSOR_READING_PUSH_FAILED + ": " + e.toString());
        }
        finally
        {
            semaphore.release();
        }
    }

    @Override
    public boolean HasSpace()
    {
        return size < queue.length;
    }

    @Override
    public boolean IsEmpty()
    {
        return size == 0;
    }

}
