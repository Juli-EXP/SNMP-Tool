package snmp

fun main(){
    val client = SnmpClient("127.0.0.1")
    //println(client.getSnmp("1.3.6.1.2.1.1.4.0"))
    println(client.getSnmp("1.3.6.1.2.1.1.5.0"))

}