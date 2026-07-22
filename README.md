# ThermoControlRT

ThermoControlRT is a real-time, multithreaded room-temperature simulation and control system written in core Java (JDK 17+), with no external build tools (no Maven/Gradle).

A `Room` is divided into independent partitions. Each partition has its own simulated temperature cell, a sensor, and a semaphore guarding concurrent access. Background threads simulate ambient heat transfer from outside, poll sensors, feed readings into a shared buffer, and drive a heater/cooler based on configurable upper/lower temperature bounds — all coordinated by a virtual `Timer` that can run faster than real time via a speed multiplier.

---

## 1. Architecture

The simulation is composed of independent, mostly-daemon-threaded components wired together in `Simulator.Start()`:

| Component | Role |
|---|---|
| `Config` | Loads and validates `config.json` (via Jackson) into static, simulation-wide settings. |
| `Timer` | Static virtual clock. Ticks on a `ScheduledExecutorService` at `tickTime / simSpeed` real intervals; all other threads synchronize against it (`WaitTillTimerStarts`, `IsRunning`, `WaitFor`). |
| `Room` / `RoomPartition` | Holds the shared `temperatures[][]` grid and the `mutexes[][]` semaphore matrix (one binary semaphore per partition), plus one `Sensor` per partition. |
| `Sensor` | Reads a single partition's temperature under its semaphore. |
| `SensorInput` | Background thread that polls all sensors each cycle, logs each reading, and pushes them into the shared `TempReadingBuffer`. |
| `TempReadingBuffer` | Thread-safe circular buffer (semaphore-protected) decoupling sensor production from controller consumption. |
| `TemperatureController` | Background thread that drains the buffer, averages readings across partitions, and turns the heater/cooler on or off via `ActuatorControl` once the average crosses `roomTempLowerBound`/`roomTempUpperBound`. |
| `ActuatorControl` | Thread-safe singleton façade owning the `Heater` and `Cooler`, tracking the current `ActuatorState` (OFF/HEATING/COOLING), and logging state transitions. |
| `Heater` / `Cooler` | Each runs its own background thread; while enabled, nudges every partition's temperature up/down by `coolHeaterAffectPerTick` per tick. |
| `OutsideTemperatureInfluence` | Background thread that drifts each partition's temperature toward `outsideTemp` each tick, scaled by `heatTransferCoefficient` (simple linear heat-transfer model). |
| `Logger` | Thread-safe, buffered, asynchronous singleton logger. Producers append to an in-memory list under a semaphore; a dedicated thread periodically flushes it to `logFilePath` and optionally echoes to the console. |

### Data flow per tick

1. `OutsideTemperatureInfluence` and `Heater`/`Cooler` (if enabled) each adjust the shared temperature grid, guarded by per-partition semaphores.
2. `SensorInput` reads each partition's temperature and pushes a timestamped `SensorReading` into `TempReadingBuffer`, logging it.
3. `TemperatureController` drains the buffer, computes the average temperature, and enables/disables the heater or cooler accordingly, logging any state change.
4. `Logger`'s background thread periodically flushes all queued log entries to disk (and the console, if enabled).

All of the above run concurrently as daemon threads and are paced by the shared `Timer`, so the whole simulation stops cleanly once `Timer` reaches `runTimeSeconds` (accelerated by `simSpeed`).

---

## 2. Configuration (`config.json`)

| Key | Meaning |
|---|---|
| `tickTimeMilliSeconds` | Simulated duration of one tick, in ms. |
| `runTimeSeconds` | Total simulated runtime before the simulation stops. |
| `simSpeed` | Multiplier that compresses real wall-clock time relative to simulated time (higher = faster). |
| `outsideTemp` | Ambient outside temperature (°C) that partitions drift toward. |
| `insideTemp` | Initial temperature (°C) for all room partitions. |
| `coolHeaterAffectPerTick` | Temperature change (°C) applied per tick while the heater/cooler is enabled. |
| `roomTempUpperBound` / `roomTempLowerBound` | Target temperature band the controller tries to keep the room average within. |
| `numberOfRoomPartitions` | Number of independent room partitions to simulate. |
| `tempReadingBufferCapacity` | Capacity of the circular buffer between sensors and the controller. |
| `threadSleepTimeMilliSeconds` | Real-time sleep interval (before `simSpeed` scaling) used by sensor polling and startup waits. |
| `heatTransferCoefficient` | Rate coefficient controlling how fast partitions drift toward `outsideTemp`. |
| `logFilePath` | Path to the log file (relative to the project root). |
| `terminalLogs` | If `true`, log entries are also printed to the console. |

---

## 3. Requirements

You must have **Java JDK 17 or later** installed.

Verify installation:

### Windows (Command Prompt or PowerShell)
```powershell
java -version
javac -version
```
### macOS / Linux (Terminal)
```bash
java -version
javac -version
```

## 4. Project Structure
```
ThermoControlRT/
├── bin/                            # Compiled class files and runtime dependencies
│   ├── com/thermo/app/App.class
│   ├── com/thermo/implementations/*.class
│   ├── com/thermo/implementations/exceptions/Exceptions.class
│   └── com/thermo/interfaces/*.class
├── lib/                            # External libraries (Jackson JSON library)
│   ├── jackson-databind-2.15.2.jar
│   ├── jackson-annotations-2.20.jar
│   └── jackson-core-2.20.1.jar
├── logs/                           # Runtime logs
│   └── log.txt
├── src/                            # Source code
│   └── com/thermo
│       ├── app/App.java
│       ├── implementations
│       │   ├── *.java
│       │   └── exceptions/Exceptions.java
│       └── interfaces
│           └── *.java
├── config.json                     # Simulation configuration file
├── README.md                       # Project documentation
├── .gitignore                      # Git ignore rules
└── .vscode/settings.json           # VS Code project settings
```

Notes:
- `src/` contains all Java source code organized by package (`app`, `implementations`, `implementations.exceptions`, `interfaces`).
- `bin/` contains compiled `.class` files; the required Jackson `.jar`s live in `lib/`.
- `logs/` is used for runtime logs generated by the simulator.
- `.git/` and `.vscode/` store project metadata and editor configuration, not part of runtime.

## 5. Compile the Application

Run this command from the **project root folder** (`ThermoControlRT`).

### Windows
```powershell
javac -cp "lib/*" -d bin ((Get-ChildItem -Recurse src -Filter *.java).FullName)
```

### macOS / Linux
```bash
javac -cp "lib/*" -d bin $(find src -name "*.java")
```

This generates:
```
bin/com/thermo/app/App.class
bin/com/thermo/implementations/*.class
bin/com/thermo/interfaces/*.class
```

## 6. Run the Application

After compiling, run:

### Windows
```powershell
java -cp "bin;lib/*" com.thermo.app.App
```

### macOS / Linux
```bash
java -cp "bin:lib/*" com.thermo.app.App
```

## 7. One-Command Build and Run

### Windows (PowerShell)
```powershell
javac -cp "lib/*" -d bin -sourcepath src (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName }) ; if ($?) { java -cp "bin;lib/*" com.thermo.app.App }
```

### macOS / Linux
```bash
javac -cp "lib/*" -d bin $(find src -name "*.java") && java -cp "bin:lib/*" com.thermo.app.App
```

## 8. Notes

- Logs are stored in the `logs/` folder at runtime, at the path set by `logFilePath` in `config.json`.
- The working directory for both terminal and VS Code is the project root, so relative paths like `logs/log.txt` resolve correctly.
- `config.json` lives in the project root and is loaded once at startup by `Config.Init`.
- If you are running this project in VS Code and encounter errors with the Run button, try disabling or uninstalling the Code Runner extension, as it may interfere with package-based Java projects.
- Always ensure `.vscode/settings.json` points to `src` as the source folder and `bin` as the output folder to avoid compilation or runtime errors.
