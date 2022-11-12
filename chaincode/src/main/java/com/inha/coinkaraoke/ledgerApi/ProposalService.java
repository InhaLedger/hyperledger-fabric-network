package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import org.hyperledger.fabric.contract.Context;

public interface ProposalService {

    Proposal createProposal(final Context ctx, String userId, String type, Long timestamp, Stake stake);

}
