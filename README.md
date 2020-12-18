# SNMP-Tool
This is a simple SNMP tool for the school subject Systems and Networks.  
The tool uses Java, Kotlin and the [SNMP4j](https://www.snmp4j.org/) library.

## Requirements
- Java version 8 or higher
- Gradle
- Intellij is recommended

## Setup
The easiest way to work with this project is to use Intellij.  
When you open Intellij, gradle should initiate the project automatically. If it does not start, or if you use another
IDE without gradle support, you have to run `gradle build` manually.  
You also have to run `gradle build` or `gradle jar` to create a jar that is located in `/build/libs`.

##Usage
Enter the number displayed in the console to execute the specified command.

## Current goals
- ~~Implement SNMP-Get~~
- ~~Implement SNMP-Set~~
- Implement SNMP-Walk
- Using different community strings

## Optional goals
- Implment MIB
- Receive Traps or Informs
- Scan a whole network
- Simple user interface

## Working features
- SNMP-Get
- SNMP-Set

## Next steps
- Proper error messages
- Simple GUI
- SNMP-Walk
- Network scan
