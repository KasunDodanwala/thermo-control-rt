package com.thermo.implementations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Logger implements Runnable
{
    private Thread thread = null;
    private static volatile Logger INSTANCE = null;

    private final Semaphore binarySemaphore = new Semaphore(1);
    private final ArrayList<String> logBuffer = new ArrayList<>();

    private BufferedWriter writer;
    public boolean terminalLogs = false;

    private Logger(String filePath, boolean terminalLogs)
    {
        try
        {
            this.writer = new BufferedWriter(new FileWriter(filePath, true));
            this.terminalLogs = terminalLogs;
        }
        catch(IOException e)
        {
            throw new RuntimeException("Failed to open log file");
        }
    }

    public static void Init(String filePath, boolean terminalLogs)
    {
        if(INSTANCE != null)
        {
            throw new IllegalStateException("Logger already initialized");
        }

        synchronized(Logger.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException("Logger already initialized");
            }

            INSTANCE = new Logger(filePath, terminalLogs);
        }
    }

    public static Logger GetINSTANCE()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException("Logger not initialized. Call Logger.Init() first.");
        }
        return INSTANCE;
    }

    public void Log(String message)
    {
        try
        {
            binarySemaphore.acquire();
            logBuffer.add(message);
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        finally
        {
            binarySemaphore.release();
        }
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
            try
            {
                binarySemaphore.acquire();

                if(!logBuffer.isEmpty())
                {
                    for(String entry : logBuffer)
                    {
                        writer.write(entry);
                        writer.newLine();

                        if(terminalLogs)
                            System.out.println(entry);
                    }

                    writer.flush();
                    logBuffer.clear();
                }
            }
            catch(IOException | InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            finally
            {
                binarySemaphore.release();
            }
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }
}
