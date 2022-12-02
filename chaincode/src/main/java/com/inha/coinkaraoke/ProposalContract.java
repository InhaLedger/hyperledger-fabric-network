package com.inha.coinkaraoke;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.Vote;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.ProposalService;
import com.inha.coinkaraoke.ledgerApi.VoteService;
import com.inha.coinkaraoke.ledgerApi.impl.AccountServiceImpl;
import com.inha.coinkaraoke.ledgerApi.impl.ProposalServiceImpl;
import com.inha.coinkaraoke.ledgerApi.impl.VoteServiceImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeException;

@Contract(name = "ProposalContract",
        info = @Info(title = "Proposal contract",
                description = "managing proposal for editing and voting",
                version = "0.0.1"))
public class ProposalContract implements ContractInterface {

    private final ProposalService proposalService;
    private final AccountService accountService;
    private final VoteService voteService;

    public ProposalContract() {
        this.proposalService = ProposalServiceImpl.getInstance();
        this.accountService = AccountServiceImpl.getInstance();
        this.voteService = VoteServiceImpl.getInstance();
    }

    @Transaction(intent = TYPE.SUBMIT)
    public Proposal createProposal(final Context ctx, String type, Long timestamp) {

        String clientId = ContractUtils.getClientId(ctx);
        Stake stake = accountService.stakeToEdit(ctx, clientId, timestamp);

        return proposalService.createProposal(ctx, clientId, type, timestamp, stake);
    }

    @Transaction(intent = TYPE.EVALUATE)
    public Proposal getProposal(final Context ctx, String proposalId, String type) {

        return proposalService.findProposal(ctx, proposalId, type)
                .orElse(null);
    }

    @Transaction(intent = TYPE.SUBMIT)
    public Vote vote(final Context ctx, String proposalId, String type, Double amounts, Long timestamp) {

        Proposal proposal = proposalService.findProposal(ctx, proposalId, type)
                .orElseThrow(() -> new ChaincodeException("not found such proposal."));

        if (!proposal.isUnderProgress()) {
            throw new ChaincodeException("Cannot vote to this proposal. This proposal has been closed");
        }

        String clientId = ContractUtils.getClientId(ctx);
        Stake stake = accountService.stakeToVote(ctx, clientId, amounts, timestamp);

        return voteService.createAndSave(ctx, proposal, stake);
    }
}
