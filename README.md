# ThermoControlRT

ThermoControlRT is a Java application built and run directly using the Java Development Kit (JDK).  
This project does not use Maven, Gradle, or any external build system.

---

## 1. Requirements

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

## 2. Project Structure
ThermoControlRT/
├── bin/
├── lib/
├── logs/
├── src/
│   └── com/thermo
│           ├── app/App.java
│           ├── implementations
│           └── interfaces
├── config.json
└── README.md

All source code lives in the `src` directory.  
All runtime log files are stored in `logs/`.


## 3. Compile the Application


Run this command from the **project root folder** (`ThermoControlRT`).

### Windows
```powershell
javac -cp "lib/*" -d bin (Get-ChildItem -Recurse src -Filter *.java)
```

### macOS / Linux
```bash
javac -cp "lib/*" -d bin $(find src -name "*.java")
```

This generates:
```bash
bin/com/thermo/app/App.class
bin/com/thermo/implementations/*.class
bin/com/thermo/interfaces/*.class
```

## 4. Run the Application

After compiling, run:

### Windows
```powershell
java -cp "bin;lib/*" com.thermo.app.App
```

### macOS / Linux
```bash
java -cp "bin:lib/*" com.thermo.app.App
```

## 5. One-Command Build and Run

### Windows (PowerShell)
```powershell
javac -cp "lib/*" -d bin (Get-ChildItem -Recurse src -Filter *.java) ; if ($?) { java -cp "bin;lib/*" com.thermo.app.App }
```

### macOS / Linux
```bash
javac -cp "lib/*" -d bin $(find src -name "*.java") && java -cp "bin:lib/*" com.thermo.app.App
```

## 6. Notes

- Logs are stored in the logs/ folder at runtime.
- The working directory for both terminal and VS Code is the project root, so relative paths like logs/abc.txt will work correctly.
- config.json is in the project root to store system configuration.
- If you are running this project in VS Code and encounter errors with the Run button, try disabling or uninstalling the Code Runner extension, as it may interfere with package-based Java projects.
- Always ensure your .vscode/settings.json points to src as the source folder and bin as the output folder to avoid compilation or runtime errors.