package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.Vote;
import org.hyperledger.fabric.contract.Context;

public interface VoteService {

    /**
     *
     * @param proposal which is voted to.
     * @param stake required for voting.
     * @return {@link Vote}
     */
    Vote createAndSave(final Context ctx, Proposal proposal, Stake stake, String voteType);
}
