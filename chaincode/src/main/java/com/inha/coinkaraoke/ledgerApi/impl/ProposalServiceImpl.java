package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.ledgerApi.ProposalService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.util.Optional;

public class ProposalServiceImpl implements ProposalService {

    private final EntityManager entityManager;

    public Proposal createProposal(final Context ctx, String userId, String type, Long timestamp, Stake stake) {

        Proposal proposal = new Proposal(type, userId, timestamp, stake.getKey());
        entityManager.saveEntity(ctx.getStub(), proposal);

        return proposal;
    }

    @Override
    public Optional<Proposal> findProposal(final Context ctx, String proposalId, String type) {

        return entityManager.getById(ctx.getStub(), Key.of(proposalId, type), Proposal.class)
                .map(entity -> (Proposal) entity);
    }

    public void finalizeProposal(final Context ctx, String userId, Long proposalId, String type) {
        throw new ChaincodeException("not implemented");
    }


    private ProposalServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static class LazyHolder {
        public static final ProposalServiceImpl INSTANCE = new ProposalServiceImpl(
                EntityManager.Factory.getInstance()
        );
    }

    public static ProposalServiceImpl getInstance() {
        return ProposalServiceImpl.LazyHolder.INSTANCE;
    }
}
