package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.ledgerApi.RewardService;

public class DefaultRewardStrategy implements RewardService {

    private final static Double EDITOR_SHARE_RATE = 0.7;
    private final static Double VOTERS_SHARE_RATE = 1 - EDITOR_SHARE_RATE;


    public Boolean isWin(final Proposal proposal) {
        return proposal.getUpVotes() > proposal.getDownVotes();
    }

    public Double extractEditorShare(Double rewardPerProposal) {
        return rewardPerProposal * EDITOR_SHARE_RATE;
    }

    public Double extractVotersShare(Double rewardPerProposal) {
        return rewardPerProposal * VOTERS_SHARE_RATE;
    }



    private DefaultRewardStrategy() {}

    private static class LazyHolder {
        public static final DefaultRewardStrategy INSTANCE = new DefaultRewardStrategy();
    }

    public static DefaultRewardStrategy getInstance() {
        return DefaultRewardStrategy.LazyHolder.INSTANCE;
    }
}
