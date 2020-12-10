package snmp

import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.Target
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.*
import org.snmp4j.transport.DefaultUdpTransportMapping

class SnmpClient(
    ipAddress: String = "127.0.0.1",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {

    //variables/getter/setter-------------------------------------------------------------------------------------------

    //Adds the port to the end of the ip address
    var ipAddress = "$ipAddress/161"
        set(value) {
            field = "$value/161"
        }


    //Snmp-functions----------------------------------------------------------------------------------------------------

    fun getSnmp(oid: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        pdu.add(VariableBinding(OID(oid)))
        pdu.type = PDU.GET

        val event = snmp.get(pdu, target)

        val result = executeEvent(event)

        snmp.close()

        return result
    }

    fun setSnmp(oid: String, value: String) :String{
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        pdu.add(VariableBinding(OID(oid), OctetString(value)))
        pdu.type = PDU.SET

        val event = snmp.set(pdu, target)

        val result = executeEvent(event)

        snmp.close()

        return result
    }

    private fun getTarget(): CommunityTarget {
        val target = CommunityTarget()
        target.community = OctetString(community)
        target.address = UdpAddress(ipAddress)
        target.version = snmpVersion
        target.retries = 2
        target.timeout = 1500
        return target
    }

    private fun executeEvent(event: ResponseEvent?): String {
        var result = ""

        if (event != null) {
            if (event.response.errorStatusText === "Success") {
                val pduResponse = event.response
                result = pduResponse.variableBindings.firstElement().toString()
            }
        } else {
            System.err.println("An error occured")
        }

        return result
    }
}