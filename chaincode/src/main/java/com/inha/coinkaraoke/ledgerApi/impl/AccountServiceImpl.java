package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.TransferHistory;
import com.inha.coinkaraoke.entity.TransferHistory.Builder;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.util.Objects;

/**
 * This class is singleton. Do not have any shared state variables.
 */
public class AccountServiceImpl implements AccountService {

    private final EntityManager entityManager;

    @Override
    public Account getAccount(final Context ctx, String userId) {

        return (Account) entityManager.getById(ctx.getStub(), Key.of(userId), Account.class)
                .orElse(new Account(userId));
    }

    @Override
    public Stake stakeToEdit(Context ctx, String proposerId, Long timestamp) {

        Stake stake = Stake.forEdit(proposerId, timestamp);
        Account account = getAccount(ctx, proposerId);
        account.addStake(stake);
        entityManager.saveEntity(ctx.getStub(), account);

        return stake;
    }

    @Override
    public Stake stakeToVote(final Context ctx, String proposerId, Double amounts, Long timestamp) {

        Stake stake = Stake.forVote(proposerId, amounts, timestamp);
        Account account = getAccount(ctx, proposerId);
        account.addStake(stake);
        entityManager.saveEntity(ctx.getStub(), account);

        return stake;
    }

    @Override
    public void transfer(final Context ctx, String senderId, String receiverId, Long timestamp, Double amount) {

        //sender
        Account senderAccount = this.getAccount(ctx, senderId);
        senderAccount.transfer(amount);
        entityManager.updateEntity(ctx.getStub(), senderAccount);

        //receiver
        Account receiverAccount = this.getAccount(ctx, receiverId);
        receiverAccount.receive(amount);
        entityManager.updateEntity(ctx.getStub(), receiverAccount);

        //history
        TransferHistory history = new Builder()
                .createInstance(senderId, receiverId, timestamp, amount)
                .get();
        entityManager.saveEntity(ctx.getStub(), history);
    }

    @Override
    public void mint(final Context ctx, String minterId, Double amount) {

        if (Objects.equals(minterId, "admin")) {

            Account adminAccount = this.getAccount(ctx, minterId);
            adminAccount.receive(amount);
            entityManager.updateEntity(ctx.getStub(), adminAccount);
        } else {
            throw new ChaincodeException("only admin can access to mint new tokens");
        }
    }

    private AccountServiceImpl(EntityManager entityManager) {

        this.entityManager = entityManager;
    }

    private static class LazyHolder {
        public static final AccountServiceImpl INSTANCE = new AccountServiceImpl(
                EntityManager.Factory.getInstance()
        );
    }

    public static AccountServiceImpl getInstance() {
        return LazyHolder.INSTANCE;
    }
}
