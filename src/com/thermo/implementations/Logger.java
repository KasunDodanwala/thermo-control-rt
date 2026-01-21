package com.thermo.implementations;

import com.thermo.implementations.exceptions.Exceptions;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Centralized asynchronous logging facility for the simulation.
 *
 * This class implements a thread-safe, buffered logger that decouples
 * log producers from I/O operations. Log messages are accumulated in
 * memory and periodically flushed to a file (and optionally to the
 * terminal) by a dedicated background thread.
 *
 * Design characteristics:
 * - Singleton to guarantee a single log sink
 * - Producer–consumer pattern with a mutex-protected buffer
 * - Runs independently and synchronizes with the simulation Timer
 *
 * Concurrency:
 * - A binary semaphore protects the log buffer against concurrent access
 * - The logging thread safely drains and flushes buffered messages
 */
public class Logger implements Runnable
{
    /** Dedicated background thread responsible for flushing logs. */
    private Thread thread = null;

    /** Singleton instance of the Logger. */
    private static volatile Logger INSTANCE = null;

    /** Mutex guarding access to the log buffer. */
    private final Semaphore mutex = new Semaphore(1);

    /** In-memory buffer holding pending log messages. */
    private final ArrayList<String> logBuffer = new ArrayList<>();

    /** File writer used to persist logs to disk. */
    private BufferedWriter writer;

    /** Flag indicating whether logs should also be printed to the terminal. */
    public boolean terminalLogs = false;

    /**
     * Private constructor to enforce singleton usage.
     *
     * @param filePath Path to the log file
     * @param terminalLogs Whether logs should also be echoed to stdout
     */
    private Logger(String filePath, boolean terminalLogs)
    {
        try
        {
            this.writer = new BufferedWriter(new FileWriter(filePath, false));
            this.terminalLogs = terminalLogs;
        }
        catch(IOException e)
        {
            throw new RuntimeException(
                Exceptions.FILE_OPEN_FAIL + "(" + filePath + "): " + e
            );
        }
    }

    /**
     * Initializes the Logger singleton.
     *
     * @param filePath Path to the log file
     * @param terminalLogs Whether logs should be printed to the terminal
     */
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

    /**
     * Returns the initialized Logger instance.
     *
     * @return Singleton Logger instance
     */
    public static Logger GetINSTANCE()
    {
        if(INSTANCE == null)
        {
            throw new IllegalStateException(Exceptions.LOGGER_NOT_INITIALIZED);
        }
        return INSTANCE;
    }

    /**
     * Queues a log message for asynchronous writing.
     *
     * @param message Log entry to be recorded
     */
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

    /**
     * Starts the logger background thread.
     *
     * This method is idempotent and will not start the thread more than once.
     */
    public void Start()
    {
        if(thread != null)
            return;

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Main execution loop for the logger.
     *
     * The thread waits for the simulation timer to start, then periodically
     * flushes buffered log messages to disk and optionally to the terminal.
     */
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