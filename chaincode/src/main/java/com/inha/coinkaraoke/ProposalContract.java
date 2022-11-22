package com.inha.coinkaraoke;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.ledgerApi.ProposalService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.ObjectMapperHolder;
import com.inha.coinkaraoke.ledgerApi.impl.ProposalServiceImpl;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Info;

@Contract(name = "ProposalContract",
        info = @Info(title = "Proposal contract",
                description = "managing proposal for editing and voting",
                version = "0.0.1"))
public class ProposalContract implements ContractInterface {

    private final ProposalService proposalService;

    public ProposalContract() {
        this.proposalService = ProposalServiceImpl.getInstance();
    }

    public Proposal createProposal(final Context ctx, String type, Long timestamp) {

        byte[] rawStake = ctx.getStub()
                .invokeChaincodeWithStringArgs(AccountContract.class.getSimpleName(), "createStakeToEdit", timestamp.toString())
                .getPayload();
        Stake stake = ObjectMapperHolder.deserialize(rawStake, Stake.class);

        String clientId = ContractUtils.getClientId(ctx);

        return proposalService.createProposal(ctx, clientId, type, timestamp, stake);
    }
}
