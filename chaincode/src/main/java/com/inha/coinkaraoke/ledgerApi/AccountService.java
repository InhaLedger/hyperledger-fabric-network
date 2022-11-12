package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.Stake;
import org.hyperledger.fabric.contract.Context;

public interface AccountService {

    Account getAccount(final Context ctx, String userId);

    Stake stakeToEdit(final Context ctx, String userId, Long timestamp);

    void transfer(final Context ctx, String from, String to, Long timestamp, Double amount);
}
