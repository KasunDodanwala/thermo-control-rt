package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Logger implements Runnable
{
    private Thread thread = null;
    private static volatile Logger INSTANCE = null;

    private final Semaphore mutex = new Semaphore(1);
    private final ArrayList<String> logBuffer = new ArrayList<>();

    private BufferedWriter writer;
    public boolean terminalLogs = false;

    private Logger(String filePath, boolean terminalLogs)
    {
        try
        {
            this.writer = new BufferedWriter(new FileWriter(filePath, false));
            this.terminalLogs = terminalLogs;
        }
        catch(IOException e)
        {
            throw new RuntimeException(Exceptions.FILE_OPEN_FAIL + "(" + filePath + "): " + e);
        }
    }

    public static void Init(String filePath, boolean terminalLogs)
    {
        if(INSTANCE != null)
        {
            throw new IllegalStateException(Exceptions.LOGGER_ALREADY_INITIALIZED);
        }

        synchronized(Logger.class)
        {
            if(INSTANCE != null)
            {
                throw new IllegalStateException(Exceptions.LOGGER_ALREADY_INITIALIZED);
            }

            INSTANCE = new Logger(filePath, terminalLogs);
        }
    }

    public static Logger GetINSTANCE()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException(Exceptions.LOGGER_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    public void Log(String message)
    {
        try
        {
            mutex.acquire();
            logBuffer.add(message);
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        finally
        {
            mutex.release();
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
                mutex.acquire();

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
                mutex.release();
            }
            Timer.WaitFor(Config.GetTickTimeMilliSeconds());
        }
    }
}
