package com.thermo.implementations;

import com.thermo.interfaces.ISensorInput;

public class SensorInput implements ISensorInput, Runnable
{
    private Thread thread = null;
    private Sensor[] sensors = null;
    private TempReadingBuffer buff = null;
    private final int threadSleepTime;

    public SensorInput(Sensor[] sensors, TempReadingBuffer buff, int threadSleepTime)
    {
        this.sensors = sensors;
        this.buff = buff;
        this.threadSleepTime = threadSleepTime;
    }
    public void Start()
    {
        if(thread != null)
            return;
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

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
                    reading = new SensorReading(i, temp, Timer.GetTimeInMinutesAndSeconds());    
                }
                ///////////////LOG///////////////////////
                Logger.GetINSTANCE().Log(
                    "Timestamp: " + reading.Timestamp +
                    "\nSensorID: " + reading.SensorId +
                    "\nTemp: " + String.format("%.2f", reading.Value) + " C\n"
                );
                buff.PushBack(reading);
            }
            Timer.WaitFor(threadSleepTime);
        }
    }
}
