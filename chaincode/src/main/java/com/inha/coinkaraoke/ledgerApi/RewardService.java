package com.inha.coinkaraoke.ledgerApi;

import com.inha.coinkaraoke.entity.Proposal;

public interface RewardService {

    Boolean isWin(final Proposal proposal);

    Double extractEditorShare(Double rewardPerProposal);

    Double extractVotersShare(Double rewardPerProposal);
}
