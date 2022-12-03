package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Arrays;
import java.util.Objects;

@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"voteType", "stakeKey", "userId", "proposalKey", "amounts", "processed", "rewarded", "timestamp"})
public class Vote extends Entity {

    @Property private Key stakeKey;
    @Property private Long timestamp;
    @Property private String userId;
    @Property private Double amounts;
    @Property private Boolean processed;
    @Property private Boolean rewarded;
    @Property private String voteType;
    @Property private Key proposalKey;

    @Override
    protected void makeKey() {
        this.key = Key.of(Arrays.toString(proposalKey.split()), userId);
    }

    public static Vote to(Proposal proposal, final Stake stake, final String voteType) {

        if (!Objects.equals(voteType, "up") && !Objects.equals(voteType, "down"))
            throw new IllegalArgumentException("vote type must be \"up\" or \"down\".");

        proposal.getVotes(voteType, stake.getAmount());

        return new Vote(stake.getKey(), stake.getTimestamp(), stake.getUserId(), voteType, stake.getAmount(), proposal.getKey());
    }

    private Vote(Key stakeKey, Long timestamp, String userId, String voteType, Double amounts, Key proposalKey) {
        this.stakeKey = stakeKey;
        this.timestamp = timestamp;
        this.userId = userId;
        this.amounts = amounts;
        this.processed = false;
        this.rewarded = false;
        this.proposalKey = proposalKey;
        this.voteType = voteType;
        this.makeKey();
    }
}
