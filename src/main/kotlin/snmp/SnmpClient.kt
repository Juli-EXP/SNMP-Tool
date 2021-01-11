package snmp

import net.percederberg.mibble.*
import net.percederberg.mibble.value.ObjectIdentifierValue
import org.snmp4j.*
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.*
import org.snmp4j.smi.VariableBinding
import org.snmp4j.transport.DefaultUdpTransportMapping
import java.lang.NumberFormatException
import java.util.concurrent.TimeoutException



class SnmpClient(
    var ipAddress: String = "127.0.0.1",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {
    //variables/getter/setter-------------------------------------------------------------------------------------------

    //Standard port for SNMP
    var port = 161

    private val mibLoader: MibLoader = MibLoader()

    private var mib: Mib = mibLoader.load("RFC1213-MIB")    //for testing

    //Last used OID; for getNext
    private var lastOid: String = ""

    //Infos about the current state
    val info: String
        get() {
            return "IP: $ipAddress\n" +
                    "Port: $port\n" +
                    "Community: $community\n" +
                    "Version: ${snmpVersion + 1}"
        }


    //Snmp-functions----------------------------------------------------------------------------------------------------

    //Get function
    fun get(oid: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(parseOid(oid))))
            pdu.type = PDU.GET
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            //System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.get(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException) {
            e.printStackTrace()
            "Timeout"
        }

        snmp.close()

        return result
    }

    //Set function
    fun set(oid: String, value: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(parseOid(oid)), OctetString(value)))
            pdu.type = PDU.SET
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            //System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.set(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException) {
            e.printStackTrace()
            "Timeout, the value wasn't set"
        }

        snmp.close()

        return result
    }

    //Loads a mib file
    fun loadMib(name: String) {
        mib = mibLoader.load(name)
    }

    //Checks if value is an oid or a mib symbol and returns an oid
    private fun parseOid(value: String): String {
        val mib = mib.getSymbol(value) ?: return value  //Returns value if mib is null

        val oid = extractOid(mib)
        if (oid != null)
            return oid

        return value
    }

    //Returns the
    private fun extractOid(symbol: MibSymbol): String? {
        if (symbol is MibValueSymbol) {
            val value = symbol.value
            if (value is ObjectIdentifierValue) {
                return "$value.0"
            }
        }
        return null
    }

    //Create target
    private fun getTarget(): CommunityTarget {
        val target = CommunityTarget()
        target.community = OctetString(community)
        target.address = UdpAddress("$ipAddress/$port")
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