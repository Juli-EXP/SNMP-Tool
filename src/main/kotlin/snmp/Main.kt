package snmp

import javax.net.ssl.SNIHostName


fun main() {
    val client = SnmpClient()

    //test(client)
    ui(client)
}

fun test(client: SnmpClient) {
    println(client.getSnmp("1.3.6.1.2.1.1.4.0"))
    println(client.setSnmp("1.3.6.1.2.1.1.4.0", "lampi"))
    println(client.getSnmp("1.3.6.1.2.1.1.4.0"))
    println(client.setSnmp("1.3.6.1.2.1.1.4.0", "Julian Lamprecht"))

    println()

    println("6 Infos about ${client.ipAddress}:")
    printAll(client)
}

fun ui(client: SnmpClient) {
    var option: Int
    var exit = false


    while (!exit) {
        println("-----------------------------------------")
        println("(1) Set ip address (default is localhost)")
        println("(2) Set community (default is public")
        println("(3) Get 6 Informations about the PC")
        println("(4) Get")
        println("(5) Set")
        println("(6) Exit")
        println("-----------------------------------------")

        option = readLine()!!.toInt()

        when (option) {
            1 -> {
                println("Enter an ip address:")
                client.ipAddress = readLine().toString()
            }

            2 -> {
                println("Enter a community:")
                client.community = readLine().toString()
            }

            3 -> printAll(client)

            4 -> {
                println("Enter an OID")
                val oid = readLine().toString()

                println(client.getSnmp(oid))
            }

            5 -> {
                println("Enter an OID")
                val oid = readLine().toString()
                println("Enter a value")
                val value = readLine().toString()

                println(client.setSnmp(oid, value))
            }

            6 -> exit = true
        }

    }
}

fun printAll(client: SnmpClient) {
    println(client.getSnmp("1.3.6.1.2.1.1.1.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.2.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.3.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.4.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.5.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.6.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.7.0"))
}

