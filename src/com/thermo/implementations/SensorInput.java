package com.thermo.implementations;

import com.thermo.interfaces.ISensorInput;

/**
 * Collects temperature readings from all sensors and forwards them to the
 * shared temperature reading buffer.
 *
 * This class runs as a dedicated thread in an event-driven style:
 * - Periodically polls each sensor
 * - Wraps raw readings into SensorReading objects with timestamps
 * - Logs readings via the Logger subsystem
 * - Pushes readings into a non-blocking shared buffer for the
 *   TemperatureController to consume
 *
 * Concurrency:
 * - Runs on its own daemon thread
 * - Relies on sensors for thread-safe temperature access
 * - Uses Timer utilities for synchronized simulation timing
 */
public class SensorInput implements ISensorInput, Runnable
{
    /** Worker thread responsible for sensor polling. */
    private Thread thread = null;

    /** Array of sensors associated with room partitions. */
    private Sensor[] sensors = null;

    /** Shared buffer used to pass readings to the controller. */
    private TempReadingBuffer buff = null;

    /** Sleep duration between consecutive sensor polling cycles. */
    private final int threadSleepTime;

    /**
     * Constructs a SensorInput component.
     *
     * @param sensors Array of sensors to be polled
     * @param buff Shared temperature reading buffer
     * @param threadSleepTime Delay between polling cycles (milliseconds)
     */
    public SensorInput(Sensor[] sensors, TempReadingBuffer buff, int threadSleepTime)
    {
        this.sensors = sensors;
        this.buff = buff;
        this.threadSleepTime = threadSleepTime;
    }

    /**
     * Starts the sensor input thread.
     *
     * Ensures the thread is started only once and runs as a daemon
     * so it terminates automatically when the JVM exits.
     */
    @Override
    public void Start()
    {
        if(thread != null)
            return;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Main execution loop of the SensorInput thread.
     *
     * The thread waits for the global simulation timer to start, then:
     * - Iterates over all sensors
     * - Reads temperature values
     * - Logs each reading with timestamp and sensor ID
     * - Pushes readings into the shared buffer
     * - Sleeps for a configured duration before the next cycle
     */
    @Override
    public void run()
    {
        Timer.WaitTillTimerStarts();
        while(Timer.IsRunning())
        {
            for(int i = 0; i < sensors.length; i++)
            {
                Double temp = sensors[i].ReadTemperature();
                SensorReading reading = null;

                if(temp != null)
                {
                    reading = new SensorReading
                    (
                        i,
                        temp,
                        Timer.GetTimeInMinutesAndSeconds()
                    );
                }

                Logger.GetINSTANCE().Log(
                    "Timestamp: " + (reading.Timestamp == null ? "null" : reading.Timestamp)  +
                    "\nSensorID: " + reading.SensorId +
                    "\nTemp: " + String.format("%.2f", reading.Value) + " C\n"
                );

                buff.PushBack(reading);
            }

            Timer.WaitFor(threadSleepTime);
        }
    }
}