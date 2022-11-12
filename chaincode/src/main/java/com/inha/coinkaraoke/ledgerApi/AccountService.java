package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Account;
import org.hyperledger.fabric.contract.Context;

public interface AccountService {

    Account getBalance(final Context ctx, String userId);

    void transfer(final Context ctx, String from, String to, Long timestamp, Double amount);
}
