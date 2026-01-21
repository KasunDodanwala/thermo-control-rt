package com.thermo.implementations;

/**
 * Acts as the top-level orchestrator for the temperature simulation.
 *
 * This class is responsible for:
 * - Printing and validating loaded configuration values
 * - Initializing all core subsystems (Timer, Room, Sensors, Actuators, Logger, etc.)
 * - Wiring dependencies between components
 * - Starting all worker threads in the correct order
 * - Starting and supervising the simulation lifecycle
 * - Printing final simulation results
 *
 * Design notes:
 * - This class does not contain business logic
 * - It strictly coordinates setup, startup, and shutdown
 * - All concurrency and timing behavior is delegated to other components
 */
public class Simulator
{
    /**
     * Starts the simulation.
     *
     * This method performs the following steps in order:
     * 1. Prints loaded configuration values for verification
     * 2. Initializes the global simulation timer
     * 3. Creates the room model and all associated subsystems
     * 4. Starts background threads (sensors, controllers, logger, etc.)
     * 5. Starts the simulation clock
     * 6. Waits for the simulation to complete
     * 7. Prints final temperature values for each room partition
     */
    public void Start()
    {
        // -------- Print loaded configuration --------
        System.out.println("Loaded Settings");
        System.out.println("====================");

        System.out.println("tickTimeMilliSeconds = " + Config.GetTickTimeMilliSeconds());
        System.out.println("runTimeSeconds = " + Config.GetRunTimeSeconds());
        System.out.println("simSpeed = " + Config.GetSimSpeed());
        System.out.println("outsideTemp = " + Config.GetOutsideTemp());
        System.out.println("insideTemp = " + Config.GetInsideTemp());
        System.out.println("coolHeaterAffectPerTick = " + Config.GetCoolHeaterAffectPerTick());
        System.out.println("roomTempUpperBound = " + Config.GetRoomTempUpperBound());
        System.out.println("roomTempLowerBound = " + Config.GetRoomTempLowerBound());
        System.out.println("numberOfRoomPartitions = " + Config.GetNumberOfRoomPartitions());
        System.out.println("tempReadingBufferCapacity = " + Config.GetTempReadingBufferCapacity());
        System.out.println("threadSleepTimeMilliSeconds = " + Config.GetThreadSleepTimeMilliSeconds());
        System.out.println("heatTransferCoefficient = " + Config.GetHeatTransferCoefficient());
        System.out.println("logFilePath = " + Config.GetLogFilePath());
        System.out.println("terminalLogs = " + Config.GetTerminalLogs());

        // -------- Initialize core simulation components --------

        // Initialize global simulation timer
        Timer.Init(
            Config.GetThreadSleepTimeMilliSeconds(),
            Config.GetSimSpeed(),
            Config.GetRunTimeSeconds() * 1000,
            Config.GetTickTimeMilliSeconds()
        );

        // Create room model with partitions and initial temperatures
        Room room = new Room(
            Config.GetNumberOfRoomPartitions(),
            Config.GetOutsideTemp()
        );

        // Create shared temperature reading buffer
        TempReadingBuffer buff = new TempReadingBuffer(
            Config.GetTempReadingBufferCapacity()
        );

        // Initialize sensor input subsystem
        SensorInput sensorInput = new SensorInput(
            room.GetSensors(),
            buff,
            Config.GetThreadSleepTimeMilliSeconds()
        );

        // Initialize actuator control (heater and cooler)
        ActuatorControl.Init(
            room.GetTemperatures(),
            Config.GetCoolHeaterAffectPerTick(),
            room.GetMutexes()
        );

        // Initialize temperature controller
        TemperatureController temperatureController =
            new TemperatureController(
                Config.GetRoomTempUpperBound(),
                Config.GetRoomTempLowerBound(),
                buff
            );

        // Initialize logger subsystem
        Logger.Init(
            Config.GetLogFilePath(),
            Config.GetTerminalLogs()
        );

        // Initialize outside temperature influence model
        OutsideTemperatureInfluence outsideTemperatureInfluence =
            new OutsideTemperatureInfluence(
                room.GetTemperatures(),
                Config.GetOutsideTemp(),
                Config.GetHeatTransferCoefficient(),
                room.GetMutexes()
            );

        // -------- Start background worker threads --------
        sensorInput.Start();
        temperatureController.Start();
        Logger.GetINSTANCE().Start();
        outsideTemperatureInfluence.Start();

        // -------- Start simulation --------
        System.out.println("\nSim Starting");
        System.out.println("\n====================\n");

        Timer.Start();

        // Wait for simulation runtime plus a small grace period
        Timer.WaitFor(Timer.GetRemainingTime() + 5000);

        // -------- Simulation completed --------
        System.out.println("\nSim Ended");
        System.out.println("====================");
        System.out.println("Sim ran for " + Timer.GetTimeInMinutesAndSeconds());

        // Print final temperatures for each room partition
        {
            double[][] temps = room.GetTemperatures();
            for(int i = 0; i < temps.length; i++)
            {
                System.out.println(
                    "Room partition " + i +
                    " Temp: " + String.format("%.2f", temps[i][0]) + " C"
                );
            }
        }
    }
}