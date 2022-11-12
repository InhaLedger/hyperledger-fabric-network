package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.TransferHistory;
import com.inha.coinkaraoke.entity.TransferHistory.Builder;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManagerProvider;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

/**
 * This class is singleton. Do not have any shared state variables.
 */
public class AccountServiceImpl implements AccountService {

    private final EntityManager<TransferHistory> historyManager;  // UTXO based model
    private final EntityManager<Account> accountManager;  // account-based model

    public Account getBalance(final Context ctx, String userId) {

        return accountManager.getById(ctx.getStub(), userId)
                .orElse(new Account(userId));
    }

    /**
     * Note that the order of transactions is important. Account-based model can be failed easily
     * because of the CouchDB's MVCC feature. To avoid version conflict, we need to make the interval
     * short as possible between read and write. Also to prevent double-spend problem as possible,
     * sender account request is processed first, receiver account is next and then total transfer history is recorded.
     * @param senderId subjectDN of the X.509 certificate
     * @param receiverId subjectDN of the X.509 certificate
     * @param timestamp request timestamp
     * @param amount total token amount to be transferred.
     * @exception ChaincodeException occurred when sender doesn't have enough available coins
     */
    public void transfer(final Context ctx, String senderId, String receiverId, Long timestamp, Double amount) {

        //sender
        Account senderAccount = this.getBalance(ctx, senderId);
        senderAccount.transfer(amount);
        accountManager.updateEntity(ctx.getStub(), senderAccount);

        //receiver
        Account receiverAccount = this.getBalance(ctx, receiverId);
        receiverAccount.receive(amount);
        accountManager.updateEntity(ctx.getStub(), receiverAccount);

        //history
        TransferHistory history = new Builder()
                .createInstance(senderId, receiverId, timestamp, amount)
                .get();
        historyManager.saveEntity(ctx.getStub(), history);
    }

    private AccountServiceImpl() {
        this.historyManager = EntityManagerProvider.getInstance(TransferHistory.class);
        this.accountManager = EntityManagerProvider.getInstance(Account.class);
    }

    private static class LazyHolder {
        public static final AccountServiceImpl INSTANCE = new AccountServiceImpl();
    }

    public static AccountServiceImpl getInstance() {
        return LazyHolder.INSTANCE;
    }
}
