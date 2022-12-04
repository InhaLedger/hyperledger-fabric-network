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
    Proposal createProposal(final Context ctx, String proposalId, String proposerId, String type, Long timestamp, Stake stake);

    /**
     * @param proposalId unique id of proposal
     * @param type proposal type
     * @return {@link Optional<Proposal>}
     */
    Optional<Proposal> findProposal(final Context ctx, String proposalId, String type);

    /**
     * Calculate rewards for proposal editors and its voters.
     * @param timestamp exact time when requests to finalize {@link Proposal}s.
     * @param rewardPerProposal rewards to be allocated per a proposal. This reward is divided into two share which
     *                          one is for editor another is for voters
     * @param batchSize The number of proposals to be finalized at one transaction. This must not be too big.
     * @return The number of proposals which are successfully finalized.
     */
    Integer finalizeProposals(final Context ctx, Long timestamp, Double rewardPerProposal, Integer batchSize);
}
