package com.inha.coinkaraoke;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.impl.AccountServiceImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(name = "AccountContract",
        info = @Info(title = "Account contract",
                description = "managing clients' accounts",
                version = "0.0.1"))
public class AccountContract implements ContractInterface {

    private static final Logger logger = LoggerFactory.getLogger(AccountContract.class);

    private final AccountService accountService;

    public AccountContract() {
        this.accountService= AccountServiceImpl.getInstance();
    }

    // this is for test
    protected AccountContract(AccountService accountService) {
        this.accountService = accountService;
    }


    @Transaction(intent = TYPE.EVALUATE)
    public Account getAccount(final Context ctx) {

        String clientId = ContractUtils.getClientId(ctx);
        logger.info("[Account Contract] call getBalance : {}", clientId);

        return this.accountService.getAccount(ctx, clientId);
    }

    @Transaction
    public Stake createStakeToEdit(final Context ctx, Long timestamp) {

        String clientId = ContractUtils.getClientId(ctx);
        logger.info("[Account Contract] call createStakeToEdit : {}", clientId);

        return accountService.stakeToEdit(ctx, clientId, timestamp);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void transfer(final Context ctx, String receiverId, Long timestamp, Double amount) {

        String senderId = ContractUtils.getClientId(ctx);
        logger.info("[Account Contract] call transfer : {} to {} ({}) coins", senderId, receiverId,
                amount);

        this.accountService.transfer(ctx, senderId, receiverId, timestamp, amount);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public void mint(final Context ctx, Double amount) throws IllegalAccessException {

        String clientId = ContractUtils.getClientId(ctx);
        logger.info("[Account Contract] call mint : {} try to mint ({}) coins", clientId, amount);

        this.accountService.mint(ctx, clientId, amount);
    }
}
