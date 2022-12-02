package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import org.hyperledger.fabric.contract.Context;

import java.util.Optional;

public interface ProposalService {

    /**
     * @param proposalId unique id for this proposal to be set.
     * @param proposerId subjectCN of the X.509 certificate.
     * @param type proposal type. refer to board type at the application level.
     * @param timestamp proposal timestamp.
     * @param stake required to propose edit.
     * @return {@link Proposal}
     */
    Proposal createProposal(final Context ctx, String proposerId, String type, Long timestamp, Stake stake);

    /**
     * @param proposalId unique id of proposal
     * @param type proposal type
     * @return {@link Optional<Proposal>}
     */
    Optional<Proposal> findProposal(final Context ctx, String proposalId, String type);
}
