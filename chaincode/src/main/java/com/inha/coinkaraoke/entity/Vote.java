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

@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"stakeKey", "userId", "proposalKey", "amounts", "processed", "rewarded", "timestamp"})
public class Vote extends Entity {

    @Property private Key stakeKey;
    @Property private Long timestamp;
    @Property private String userId;
    @Property private Double amounts;
    @Property private Boolean processed;
    @Property private Boolean rewarded;
    @Property private Key proposalKey;

    @Override
    protected void makeKey() {
        this.key = Key.of(Arrays.toString(proposalKey.split()), userId);
    }

    public static Vote to(Proposal proposal, Stake stake) {

        return new Vote(stake.getKey(), stake.getTimestamp(), stake.getUserId(),stake.getAmount(), proposal.getKey());
    }

    private Vote(Key stakeKey, Long timestamp, String userId, Double amounts, Key proposalKey) {
        this.stakeKey = stakeKey;
        this.timestamp = timestamp;
        this.userId = userId;
        this.amounts = amounts;
        this.processed = false;
        this.rewarded = false;
        this.proposalKey = proposalKey;
        this.makeKey();
    }
}
