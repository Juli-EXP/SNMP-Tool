package example;

import org.snmp4j.smi.OID;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        SimpleSnmpClient client = new SimpleSnmpClient("udp:127.0.0.1/161");
        String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.4.0"));
        System.out.println(sysDescr);
    }
}
