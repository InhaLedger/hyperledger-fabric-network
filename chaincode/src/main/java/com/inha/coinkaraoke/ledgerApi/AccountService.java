package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.Stake;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

public interface AccountService {

    /**
     * @param userId account owner id, which equal to subjectCN of the X.509 certificate.
     * @return {@link Account}
     */
    Account getAccount(final Context ctx, String userId);

    /**
     * @param proposerId subjectCN of the X.509 certificate.
     * @param timestamp edit proposal timestamp.
     * @exception ChaincodeException when the edit proposer doesn't have enough coins.
     */
    Stake stakeToEdit(final Context ctx, String proposerId, Long timestamp);

    Stake stakeToVote(final Context ctx, String proposerId, Double amounts, Long timestamp);

    /**
     * Note that the order of transactions is important. Account-based model can be failed easily
     * because of the CouchDB's MVCC feature. To avoid version conflict, we need to make the interval
     * short as possible between read and write. Also to prevent double-spend problem as possible,
     * sender account request is processed first, receiver account is next and then total transfer history is recorded.
     * @param senderId subjectCN of the X.509 certificate
     * @param receiverId subjectCN of the X.509 certificate
     * @param timestamp request timestamp
     * @param amount total token amount to be transferred.
     * @exception ChaincodeException occurred when sender doesn't have enough available coins
     */
    void transfer(final Context ctx, String senderId, String receiverId, Long timestamp, Double amount);

    /**
     * Only admin can create new coins.
     * @param amount for minting.
     * @exception ChaincodeException request has not authorized. Only admin can call this function.
     */
    void mint(final Context ctx, String minterId, Double amount);
}
