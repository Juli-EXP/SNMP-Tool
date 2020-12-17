package snmp

import org.snmp4j.CommunityTarget
import org.snmp4j.PDU
import org.snmp4j.Snmp
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.smi.*
import org.snmp4j.transport.DefaultUdpTransportMapping
import org.snmp4j.util.DefaultPDUFactory
import org.snmp4j.util.TreeEvent
import org.snmp4j.util.TreeUtils
import java.lang.NumberFormatException
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList
import org.snmp4j.smi.VariableBinding


class SnmpClient(
    var ipAddress: String = "127.0.0.1",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {
    //variables/getter/setter-------------------------------------------------------------------------------------------

    //Standard port for SNMP
    var port = 161

    //Last used OID; for getNext
    private var lastOid: String = ""

    //Simple function to print all infos about the class
    fun getInfo() {
        println("IP: $ipAddress")
        println("Port: $port")
        println("Community: $community")
        println("Version: ${snmpVersion + 1}")
    }

    //Snmp-functions----------------------------------------------------------------------------------------------------

    //Get function
    fun get(oid: String): String {
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
            pdu.add(VariableBinding(OID(oid), OctetString(value)))
            pdu.type = PDU.SET
        } catch (e: NumberFormatException) {
            System.err.println("OID is not valid")
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

    //Walk function, doesn't work at the moment
    fun walk(startOid: String): ArrayList<String>? {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val results = ArrayList<String>()

        val oid: OID
        try {
            oid = OID(startOid)
        } catch (e: NumberFormatException) {
            System.err.println("OID is not valid")
            return null
        }

        val treeUtils = TreeUtils(snmp, DefaultPDUFactory())
        val events = treeUtils.getSubtree(target, oid)

        if (events == null || events.size == 0) {
            System.err.println("No results were found")
            return null
        }

        for (event: TreeEvent in events) {
            print("le")

            val varBindings = event.variableBindings
            if (varBindings == null || varBindings.isEmpty()) {
                continue
            }

            for (varBinding: VariableBinding? in varBindings) {
                if (varBinding == null) {
                    continue
                }
                println(
                    varBinding.oid.toString() +
                            " : " +
                            varBinding.variable.syntaxString +
                            " : " +
                            varBinding.variable
                )
            }
        }

        return results
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