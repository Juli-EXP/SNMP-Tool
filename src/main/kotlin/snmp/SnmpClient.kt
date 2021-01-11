package snmp

import net.percederberg.mibble.*
import net.percederberg.mibble.value.ObjectIdentifierValue
import org.snmp4j.*
import org.snmp4j.smi.*
import org.snmp4j.event.ResponseEvent
import org.snmp4j.mp.SnmpConstants
import org.snmp4j.transport.DefaultUdpTransportMapping
import java.io.FileNotFoundException
import java.lang.NumberFormatException
import java.util.concurrent.TimeoutException


/**
 * A simple SNMP client. This class contains functions to get and set SNMP informations
 * of a set ip address. It also contains basic support for MIB.
 *
 * @author Julian Lamprecht
 */
class SnmpClient(
    var ipAddress: String = "127.0.0.1",
    var snmpVersion: Int = SnmpConstants.version1,
    var community: String = "public"
) {
    //variables/getter/setter-------------------------------------------------------------------------------------------

    /**
     * The port for SNMP operations. 161 is the standard for SNMP.
     */
    var port = 161

    /**
     * A MIB loader to load different MIB files.
     */
    private val mibLoader: MibLoader = MibLoader()

    /**
     * The MIB symbols and the corresponding OIDs.
     */
    private val mibTable: HashMap<String, String> = HashMap()

    /**
     * Stores basic informations for the SNMP client such as the IP, port, community and
     * the SNMP version.
     */
    val info: String
        get() {

            return "IP: $ipAddress\n" +
                    "Port: $port\n" +
                    "Community: $community\n" +
                    "Version: ${when(snmpVersion){
                        SnmpConstants.version1 -> 1
                        SnmpConstants.version2c -> 2
                        SnmpConstants.version3 -> 3
                        else -> 0
                    }}"
        }

    //Loads a few MIB files
    init {
        fillMibTable(mibLoader.load("RFC1213-MIB"))
        fillMibTable(mibLoader.load("HOST-RESOURCES-MIB"))
    }

    //Snmp-functions----------------------------------------------------------------------------------------------------

    /**
     * Executes the SNMP get function.
     *
     * @param oid   The oid, either in dotted numerical format or as a MIB string.
     *
     * @return      The result of the operation or an error message.
     */
    fun get(oid: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(parseInput(oid))))
            pdu.type = PDU.GET
        } catch (e: NumberFormatException) {
            //e.printStackTrace()
            //System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.get(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException) {
            //e.printStackTrace()
            "Timeout"
        }

        snmp.close()

        return parseResult(result)
    }

    /**
     * Executes the SNMP set function.
     *
     * @param oid   The oid, either in dotted numerical format or as a MIB string.
     * @param value The value, you want to set.
     *
     * @return      The result of the operation or an error message.
     */
    fun set(oid: String, value: String): String {
        val snmp = Snmp(DefaultUdpTransportMapping())
        snmp.listen()

        val target = getTarget()

        val pdu = PDU()
        try {
            pdu.add(VariableBinding(OID(parseInput(oid)), OctetString(value)))
            pdu.type = PDU.SET
        } catch (e: NumberFormatException) {
            //e.printStackTrace()
            //System.err.println("OID is not valid")
            return "Invalid OID"
        }

        val event = snmp.set(pdu, target)

        val result = try {
            executeEvent(event)
        } catch (e: TimeoutException) {
            //e.printStackTrace()
            "Timeout, the value wasn't set"
        }

        snmp.close()

        return parseResult(result)
    }

    /**
     * Loads a new MIB file.
     *
     * @param name  The name of the MIB file
     */
    fun loadMib(name: String) {
        try{
            val mib = mibLoader.load(name)
            fillMibTable(mib)
        }catch (e: FileNotFoundException){
            println(e.message)
        }
    }

    /**
     * DO NOT USE!!!
     * Unloads a MIB file.
     *
     * @param name  The name of the MIB file
     */
    private fun unloadMib(name: String) {
        mibLoader.unload(name)
    }

    /**
     * Checks if the value is an oid in dotted numerical format, or if it
     * is a MIB symbol string. If it is neither, the unchanged input is returned.
     *
     * @param value     The input you want to parse
     * @return          The oid or the unchanged input
     */
    private fun parseInput(value: String): String {
        /*
        val mib = mibs[0].getSymbol(value) ?: return value  //Returns value if mib is null

        val oid = extractOid(mib)
        if (oid != null) {
            return oid
        }

        return value
         */
        return if (mibTable.containsKey(value)) {
             mibTable[value]!!
        } else {
             value
        }
    }

    /**
     * Removes everything from the string, except the actual result.
     * If it is an error message, it will be returned unchanged.
     *
     * @param value     The input you want to parse
     * @return          The result or the unchanged input
     */
    private fun parseResult(value: String): String {
        if (!value.contains("=")) {
            return value
        }

        return value.split(" ")[2]  //Returns th eactual result of the SNMP operation
    }

    /**
     * Stores all the MIB symbols and the corresponding OIDs.
     *
     * @param mib   The MIB of File
     */
    private fun fillMibTable(mib: Mib) {
        for (symbol in mib.allSymbols) {
            val oid = extractOid(symbol as MibSymbol)
            if (oid != null) {
                mibTable[symbol.name] = oid
            }
        }
    }

    /**
     * Returns the OID af a given MIB symbol.
     *
     * @param symbol    The MIB symbol
     * @return          The OID of the symbol or null
     */
    private fun extractOid(symbol: MibSymbol): String? {
        if (symbol is MibValueSymbol) {
            val value = symbol.value
            if (value is ObjectIdentifierValue) {
                return "$value.0"
            }
        }

        return null
    }

    /**
     * Creates a target.
     */
    private fun getTarget(): CommunityTarget {
        val target = CommunityTarget()
        target.community = OctetString(community)
        target.address = UdpAddress("$ipAddress/$port")
        target.version = snmpVersion
        target.retries = 2
        target.timeout = 1500
        return target
    }

    /**
     * Executes the SNMP function.
     *
     * @param event     The event of the SNMP operation
     * @return          The result of the SNMP operation
     */
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