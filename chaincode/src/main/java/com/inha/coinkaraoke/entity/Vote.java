package com.inha.coinkaraoke.entity;

import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public class Vote extends Entity {

    @Property private Long id;
    @Property private Key stakeKey;
    @Property private Long timestamp;
    @Property private String userId;
    @Property private Boolean processed;
    @Property private Boolean rewarded;
    @Property private Key proposalKey;

    @Override
    protected void makeKey() {

    }


}
