package snmp

fun main(){
    val client = SnmpClient()
    println(client.getSnmp("1.3.6.1.2.1.1.4.0"))

}