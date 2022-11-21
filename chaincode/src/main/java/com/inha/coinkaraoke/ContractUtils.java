package com.inha.coinkaraoke;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

public class ContractUtils {

    /**
     * @return certificate DN (ex, "CN=ydh9516, OU=client")
     */
    public static String getClientId(Context ctx) {

        String dn = ctx.getClientIdentity().getX509Certificate().getSubjectDN().getName();
        String[] subNames = dn.split(",");
        for (String subName : subNames) {
            if (subName.contains("CN=")) {
                return subName.substring(3);
            }
        }

        throw new ChaincodeException("cannot find userId(CN record) in the user certificate DN.");
    }
}
