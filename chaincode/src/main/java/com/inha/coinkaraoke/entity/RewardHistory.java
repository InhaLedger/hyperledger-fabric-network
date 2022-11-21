package com.inha.coinkaraoke.entity;

import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RewardHistory extends Entity {

    @Property private Long id;
    @Property private Long proposalId;
    @Property private String userId;
    @Property private Boolean isEditor;
    @Property private Double voteRewards;
    @Property private Double editRewards;
    @Property private Long proposalFinalizedTimestamp;
    @Property private Long proposalFinalizedPeriod;

    @Override
    protected void makeKey() {

    }
}
