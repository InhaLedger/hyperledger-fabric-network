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

import java.util.concurrent.TimeUnit;

@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"stakeId", "userId", "amount", "timestamp", "completeTimestamp"})
public class Stake extends Entity {

    private static final Double EDIT_STAKE_AMOUNT = 10.0;
    private static final Double VOTE_STAKE_AMOUNT = 1.0;
    private static final Long DEFAULT_STAKE_PERIOD = TimeUnit.DAYS.toMillis(7);

    @Property private String userId;
    @Property private Double amount;
    @Property private Long timestamp;
    @Property private Long completeTimestamp;

    @Override
    protected void makeKey() {
        this.key = Key.of(userId, timestamp.toString());
    }

    public static Stake forEdit(String userId, Long timestamp) {
        Stake instance = new Stake();
        instance.userId = userId;
        instance.amount = EDIT_STAKE_AMOUNT;
        instance.timestamp = timestamp;
        instance.completeTimestamp = timestamp + DEFAULT_STAKE_PERIOD;
        instance.makeKey();

        return instance;
    }

    public static Stake forVote(String userId, Long timestamp) {
        Stake instance = new Stake();
        instance.userId = userId;
        instance.amount = VOTE_STAKE_AMOUNT;
        instance.timestamp = timestamp;
        instance.completeTimestamp = timestamp + DEFAULT_STAKE_PERIOD;
        instance.makeKey();

        return instance;
    }
}