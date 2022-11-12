package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
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
        this.key = String.join(INDEX_KEY_DELIMITER, userId, timestamp.toString());
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

/* *
 * 1. 게시글 작성 스마트 컨트랙트 작성하기 -> stake 관련 로직도 함께 작성
 * 2. 투표 스마트 컨트랙트 작성하기
 * 3. 보상 컨트랙트 작성하기. (mint, 송금)
 */