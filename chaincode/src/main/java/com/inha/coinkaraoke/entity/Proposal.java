package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"id", "userId", "type", "timeStamp", "status", "editStakeKey"})
public class Proposal extends Entity {

    @Property private String id;
    @Property private String type;
    @Property private String userId;
    @Property private Long timeStamp;  // last modified
    @Property private ProposalStatus status;
    @Property private Key editStakeKey;

    @JsonIgnore
    public Boolean isUnderProgress() {
        return this.status == ProposalStatus.PROGRESS;
    }

    @Override
    protected void makeKey() {
        this.key = Key.of(id, type);
    }

    public Proposal(String id, String type, String userId, Long timeStamp, Key editStakeKey) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.editStakeKey = editStakeKey;
        this.status = ProposalStatus.PROGRESS;
        this.makeKey();
    }
}
