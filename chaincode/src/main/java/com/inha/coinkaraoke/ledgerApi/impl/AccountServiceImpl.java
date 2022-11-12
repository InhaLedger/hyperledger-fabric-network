package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.AccountHistory;
import com.inha.coinkaraoke.entity.AccountHistory.Builder;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManagerProvider;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

/**
 * This class is singleton. Do not have any shared state variables.
 */
public class AccountServiceImpl implements AccountService {

    private final EntityManager<AccountHistory> historyManager;  // UTXO based model
    private final EntityManager<Account> accountManager;  // account-based model

    public Double getBalance(final Context ctx, String userId) {

        return accountManager.getById(ctx.getStub(), userId)
                .map(Account::getBalance)
                .orElse(0.0d);
    }

    /**
     * Note that the order of transactions is important. Account-based model can be failed easily
     * because of the CouchDB's MVCC feature. To avoid version conflict, we need to make the interval
     * short as possible between read and write. Also to prevent double-spend problem as possible,
     * sender account request is processed first, receiver account is next and then total transfer history is recorded.
     * @param ctx
     * @param senderId subjectDN of the X.509 certificate
     * @param receiverId subjectDN of the X.509 certificate
     * @param timestamp request timestamp
     * @param amount total token amount to be transferred.
     */
    public void transfer(final Context ctx, String senderId, String receiverId, Long timestamp, Double amount) {

        Double senderBalance = this.getBalance(ctx, senderId);
        if (senderBalance < amount) {
            throw new ChaincodeException("not enough account balance!");
        }

        //sender
        Account senderAccount = new Account.Builder()
                .createInstance(senderId, senderBalance - amount)
                .get();
        accountManager.updateEntity(ctx.getStub(), senderAccount);

        //receiver
        Double newBalance = this.getBalance(ctx, receiverId) + amount;
        Account receiverAccount = new Account.Builder()
                .createInstance(receiverId, newBalance)
                .get();
        accountManager.updateEntity(ctx.getStub(), receiverAccount);

        //history
        AccountHistory history = new Builder()
                .createInstance(senderId, receiverId, timestamp, amount)
                .get();
        historyManager.saveEntity(ctx.getStub(), history);
    }

    private AccountServiceImpl() {
        this.historyManager = EntityManagerProvider.getInstance(AccountHistory.class);
        this.accountManager = EntityManagerProvider.getInstance(Account.class);
    }

    private static class LazyHolder {
        public static final AccountServiceImpl INSTANCE = new AccountServiceImpl();
    }

    public static AccountServiceImpl getInstance() {
        return LazyHolder.INSTANCE;
    }
}
