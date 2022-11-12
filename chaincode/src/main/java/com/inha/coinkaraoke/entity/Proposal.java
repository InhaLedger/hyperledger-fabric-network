package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.concurrent.atomic.AtomicLong;

@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"userId", "type", "timeStamp", "status", "editStakeKey"})
public class Proposal extends Entity {

    private static AtomicLong ID_GENERATOR = new AtomicLong();

    @Property private Long id;
    @Property private String type;
    @Property private String userId;
    @Property private Long timeStamp;  // last modified
    @Property private ProposalStatus status;
    @Property private String editStakeKey;

    @Override
    protected void makeKey() {
        this.key = String.join(INDEX_KEY_DELIMITER, id.toString(), userId, type);
    }

    public Proposal(String type, String userId, Long timeStamp, String editStakeKey) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.type = type;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.editStakeKey = editStakeKey;
        this.status = ProposalStatus.PROGRESS;
    }
}
