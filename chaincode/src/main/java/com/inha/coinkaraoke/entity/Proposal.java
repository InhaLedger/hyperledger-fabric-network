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
@JsonPropertyOrder({"id", "userId", "type", "timeStamp", "status", "upVotes", "downVotes", "editStakeKey"})
public class Proposal extends Entity {

    @Property private String id;
    @Property private String type;
    @Property private String userId;
    @Property private Long timeStamp;  // last modified
    @Property private ProposalStatus status;
    @Property private Key editStakeKey;
    @Property private Double upVotes;
    @Property private Double downVotes;

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
        this.upVotes = 0.0;
        this.downVotes = 0.0;
        this.makeKey();
    }

    /**
     * These properties ({@code upVotes} & {@code downVotes}) are very vulnerable for concurrency, especially couchDB's MVCC read conflicts
     * because they are balance-based model not UTXO model. Extra techniques such as delta increasing when conflicts occur
     * are needed. But for the sake of demonstration simplicity, we leave this as a future challenge. Maybe we can develop
     * an annotation for such balanced-base data so then the couchDB calculates total increase whenever read-conflicts occur.
     * @param type vote type. "up" or "down" only.
     * @param amounts vote amount.
     */
    public void getVotes(String type, Double amounts) {
        if (type.equals("up")) {
            this.upVotes += amounts;
        } else if (type.equals("down")) {
            this.downVotes += amounts;
        }
    }
}
