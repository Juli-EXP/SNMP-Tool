# SNMP-Tool
This is a simple SNMP tool for the school subject Systems and Networks.  
The tool uses Kotlin, the [SNMP4j](https://www.snmp4j.org/) library and [Mibble](https://github.com/cederberg/mibble).

## Requirements
- Java version 8 or higher
- Gradle
- Intellij is recommended

## Setup
The easiest way to work with this project is to use Intellij.  
When you open Intellij, gradle should initiate the project automatically. If it does not start, or if you use another
IDE without gradle support, you have to install gradle and run `gradle build` manually.  
You also have to run `gradle build` to create a jar that is located in `/build/libs`.

## Usage
The program is very simple to use. Type in help, and you get a list of all working commands

## Current goals
- ~~Implement SNMP-Get~~
- ~~Implement SNMP-Set~~
- ~~Using different community strings~~

## Optional goals
- ~~Implment MIB~~
- Receive Traps or Informs
- ~~Scan a whole network~~
- Simple GUI
  
## Working features
- SNMP-Get
- SNMP-Set
- Different community strings
- Simple MIB functionality
- Scanning a network

## Next steps
- Simple GUI
- Receiving Traps
