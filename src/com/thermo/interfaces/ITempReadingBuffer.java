package com.thermo.interfaces;

/**
 * Interface representing a buffer for storing temperature readings.
 * Provides methods to query the buffer's current state such as size, space availability, and emptiness.
 */
public interface ITempReadingBuffer
{
    /**
     * Returns the current number of readings stored in the buffer.
     *
     * @return Number of readings
     */
    public int GetSize();

    /**
     * Checks whether the buffer has space for additional readings.
     *
     * @return True if there is space, false if the buffer is full
     */
    public boolean HasSpace();

    /**
     * Checks whether the buffer is empty.
     *
     * @return True if the buffer contains no readings, false otherwise
     */
    public boolean IsEmpty();
}