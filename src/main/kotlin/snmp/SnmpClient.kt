package snmp

import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.*
import org.snmp4j.transport.DefaultUdpTransportMapping
import tornadofx.resizeCall
import java.lang.NumberFormatException
import java.util.concurrent.TimeoutException

class SnmpClient(
    ipAddress: String = "127.0.0.1/161",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {

    //variables/getter/setter-------------------------------------------------------------------------------------------

    //Adds the port to the end of the ip address
    var ipAddress = ipAddress
        set(value) {
            field = "$value/161"
        }

    fun getInfo() {
        println("IP: $ipAddress")
        println("Community: $community")
        println("Version: ${snmpVersion + 1}")
    }

    //Snmp-functions----------------------------------------------------------------------------------------------------

    //Get function
    fun getSnmp(oid: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()


        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(oid)))
            pdu.type = PDU.GET
        } catch (e: NumberFormatException) {
            System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.get(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException){
            e.printStackTrace()
            "Timeout"
        }

        snmp.close()

        return result
    }


    //Set function
    fun setSnmp(oid: String, value: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(oid), OctetString(value)))
            pdu.type = PDU.SET
        } catch (e: NumberFormatException) {
            System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.set(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException){
            e.printStackTrace()
            "Timeout, the value wasn't set"
        }

        snmp.close()

        return result
    }


    //Create target
    private fun getTarget(): CommunityTarget {
        val target = CommunityTarget()
        target.community = OctetString(community)
        target.address = UdpAddress(ipAddress)
        target.version = snmpVersion
        target.retries = 2
        target.timeout = 1500
        return target
    }

    //Execute snmp function
    @Throws(TimeoutException::class)
    private fun executeEvent(event: ResponseEvent?): String {

        return if (event != null && event.response != null) {
            val pduResponse = event.response

            //returns the result of the following block if event != null
            if (event.response.errorStatusText === "Success") {
                pduResponse.variableBindings.firstElement().toString()
            } else {
                System.err.println(event.response.errorStatusText)
                pduResponse.errorStatusText
            }

        } else {
            //returns timeout if event == null
            throw TimeoutException("The request timed out")
        }
    }
}