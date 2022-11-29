package com.inha.coinkaraoke;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.ProposalService;
import com.inha.coinkaraoke.ledgerApi.impl.AccountServiceImpl;
import com.inha.coinkaraoke.ledgerApi.impl.ProposalServiceImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;

@Contract(name = "ProposalContract",
        info = @Info(title = "Proposal contract",
                description = "managing proposal for editing and voting",
                version = "0.0.1"))
public class ProposalContract implements ContractInterface {

    private final ProposalService proposalService;
    private final AccountService accountService;

    public ProposalContract() {
        this.proposalService = ProposalServiceImpl.getInstance();
        this.accountService = AccountServiceImpl.getInstance();
    }

    @Transaction(intent = TYPE.SUBMIT)
    public Proposal createProposal(final Context ctx, String type, Long timestamp) {

        String clientId = ContractUtils.getClientId(ctx);
        Stake stake = accountService.stakeToEdit(ctx, clientId, timestamp);

        return proposalService.createProposal(ctx, clientId, type, timestamp, stake);
    }
}
