package com.inha.coinkaraoke.ledgerApi;

import org.hyperledger.fabric.contract.Context;

public interface AccountService {

    Double getBalance(final Context ctx, String userId);

    void transfer(final Context ctx, String from, String to, Long timestamp, Double amount);
}
