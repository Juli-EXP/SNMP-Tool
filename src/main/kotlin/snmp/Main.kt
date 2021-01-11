package snmp

import org.snmp4j.mp.SnmpConstants

fun main() {
    val client = SnmpClient()
    ui(client)
}

fun ui(client: SnmpClient) {
    var input: String
    var option: List<String>
    var quit = false

    println("Type 'help' to get a list of all commands")
    println("IMPORTANT!")
    println("MIB symbols are case sensitive")

    while (!quit) {
        input = readLine().toString()
        option = input.split(" ")

        when (option[0]) {
            "help" -> help()
            "info" -> println(client.info)
            "quit" -> quit = true
            "get" -> {
                if (checkArgs(option, 2)) {
                    println(client.get(option[1]))
                }
            }
            "set" -> {
                if (checkArgs(option, 3)) {
                    println(client.set(option[1], option[2]))
                }
            }
            "scan" -> {
                if (checkArgs(option, 2)) {
                    println("Scanning network, this might take a while")
                    client.scanNetwork(option[1]).forEach { (key, value) -> println("$key: $value") }
                }
            }
            "ip" -> {
                if (checkArgs(option, 2)) {
                    client.ipAddress = option[1]
                    println("IP address changed to: ${option[1]}")
                }
            }
            "port" -> {
                if (checkArgs(option, 2)) {
                    client.port = option[1].toInt()
                    println("Port changed to: ${option[1]}")
                }
            }
            "version" -> {
                if (checkArgs(option, 2)) {
                    when (option[1].toInt()) {
                        1 -> client.snmpVersion = SnmpConstants.version1
                        2 -> client.snmpVersion = SnmpConstants.version2c
                        3 -> client.snmpVersion = SnmpConstants.version3
                    }
                    println("Version changed to: ${option[1]}")
                }
            }
            "community" -> {
                if (checkArgs(option, 2)) {
                    client.community = option[1]
                    println("Community changed to: ${option[1]}")
                }
            }
            "load" -> {
                if (checkArgs(option, 2)) {
                    println("Trying to load MIB: ${option[1]}")
                    client.loadMib(option[1])
                }
            }

            else -> println("Command not found: ${option[0]}")
        }
    }
}


fun checkArgs(option: List<Any>, min: Int): Boolean {
    if (option.size >= min) {
        return true
    } else
        println("Error: not enough arguments")
    return false
}

fun help() {
    val format = "%-25s%s%n"
    System.out.printf(format, "help", "-> Prints the manual")
    System.out.printf(format, "info", "-> Prints informations about the client")
    System.out.printf(format, "quit", "-> Quits the program")
    System.out.printf(format, "get [oid/string]", "-> Performs the get operation")
    System.out.printf(format, "set [oid/string] [value]", "-> Performs the set operation")
    System.out.printf(format, "scan [network]", "-> Scans the whole network.")
    System.out.printf(format, "", "'-> Only type in the ip and not the subnet. Only works with /24 networks")
    System.out.printf(format, "ip [new ip]", "-> Changes the IP address")
    System.out.printf(format, "port [new port]", "-> Changes the port")
    System.out.printf(format, "version [version number]", "-> Change the SNMP version")
    System.out.printf(format, "community [comm. name]", "-> Change the community string")
    System.out.printf(format, "load [MIB name]", "-> Load a new MIB file (might not work)")
}

