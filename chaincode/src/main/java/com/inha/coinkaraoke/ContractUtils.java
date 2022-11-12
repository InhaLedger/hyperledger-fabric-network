package com.inha.coinkaraoke;


import org.hyperledger.fabric.contract.Context;

public class ContractUtils {

    public static String getClientId(Context ctx) {

        return ctx.getClientIdentity().getX509Certificate().getSubjectDN().getName();
    }
}
