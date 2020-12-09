package example;

import org.snmp4j.smi.OID;
import snmp.SnmpClient;

import java.io.IOException;

public class MainOld {
    public static void main(String[] args) throws IOException {
        SimpleSnmpClient client = new SimpleSnmpClient("127.0.0.1/161");
        String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.4.0"));
        System.out.println(sysDescr);

        SnmpClient t = new SnmpClient();
    }
}
