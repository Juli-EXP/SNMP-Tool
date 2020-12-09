package snmp

import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.*
import org.snmp4j.transport.DefaultUdpTransportMapping

class SnmpClient(
    ipAddress: String = "127.0.0.1",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {
    init {

    }

    //variables/getter/setter-------------------------------------------------------------------------------------------

    //Adds the port to the end of the ip address
    private var ipAddress = "$ipAddress/161"
        set(value) {
            field = "$value/161"
        }


    //Snmp-functions----------------------------------------------------------------------------------------------------

    fun getSnmp(oid: String): String {
        var result = ""

        val transport = DefaultUdpTransportMapping()
        transport.listen()

        val target = CommunityTarget()
        target.community = OctetString(community)
        target.address = UdpAddress(ipAddress)
        target.version = snmpVersion
        target.retries = 2
        target.timeout = 1500

        val pdu = PDU()
        pdu.add(VariableBinding(OID(oid)))
        pdu.type = PDU.GET

        val snmp = Snmp(transport)

        val response = snmp.get(pdu, target)

        if (response != null) {
            if (response.response.errorStatusText === "Success") {
                val pduResponse = response.response
                result = pduResponse.variableBindings.firstElement().toString()
            }
        } else {
            System.err.println("An error occured")
        }

        snmp.close()

        return result
    }
}