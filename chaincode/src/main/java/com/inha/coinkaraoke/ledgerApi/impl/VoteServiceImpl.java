package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.Vote;
import com.inha.coinkaraoke.ledgerApi.VoteService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import org.hyperledger.fabric.contract.Context;

public class VoteServiceImpl implements VoteService {

    private final EntityManager entityManager;


    @Override
    public Vote createAndSave(final Context ctx, Proposal proposal, Stake stake, String voteType) {

        Vote vote = Vote.to(proposal, stake, voteType);
        entityManager.saveEntity(ctx.getStub(), vote);

        return vote;
    }



    private static class LazyHolder {
        public static final VoteServiceImpl INSTANCE = new VoteServiceImpl(
                EntityManager.Factory.getInstance()
        );
    }

    private VoteServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public static VoteServiceImpl getInstance() {
        return VoteServiceImpl.LazyHolder.INSTANCE;
    }
}
