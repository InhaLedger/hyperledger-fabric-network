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
    private static final String ROLE_ATTRIBUTE = "role";
    private static final String ROLE_ADMIN = "admin";

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

//    /**
//     * Only admin can create new tokens.
//     * @param ctx
//     * @param amounts
//     */
//    @Transaction(intent = TYPE.SUBMIT)
//    public void mint(final Context ctx, double amounts) {
//
//        if (ctx.getClientIdentity().assertAttributeValue(ROLE_ATTRIBUTE, ROLE_ADMIN)) {
//
//            double balance = this.accountService.getBalance(ctx, ROLE_ADMIN);
//            JSONObject toJson = new JSONObject();
//            toJson.put("balance", balance + amounts);
//            ctx.getStub().putState(ROLE_ADMIN, toJson.toString().getBytes(UTF_8));
//
//        } else {
//            logger.warn("[{}]{} illegal access to mint method", ctx.getClientIdentity().getMSPID(),
//                    ctx.getClientIdentity().getId());
//            throw new ChaincodeException("only admin can access to mint new tokens");
//        }
//    }
}
