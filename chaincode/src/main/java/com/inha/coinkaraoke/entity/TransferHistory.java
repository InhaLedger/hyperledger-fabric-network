package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
@Slf4j
@Getter @Setter(AccessLevel.PRIVATE)
@JsonPropertyOrder({"senderId", "receiverId", "amount", "timestamp", "remarks"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferHistory extends Entity {

    @Property private double amount;
    @Property private Long timestamp;
    @Property private String receiverId;
    @Property private String senderId;
    @Property private String remarks; // 기타

    @Override
    protected void makeKey() {
        this.key = String.join(INDEX_KEY_DELIMITER, this.timestamp.toString(), this.senderId, this.receiverId);
    }

    public static class Builder {

        private final TransferHistory instance;

        public Builder() {

            this.instance = new TransferHistory();
        }

        public Builder createInstance(String senderId, String receiverId, Double amount, Long timestamp, String remark) {

            this.instance.setAmount(amount);
            this.instance.setRemarks(remark);
            this.instance.setSenderId(senderId);
            this.instance.setReceiverId(receiverId);
            this.instance.setTimestamp(timestamp);

            return this;
        }

        public Builder createInstance(String senderId, String receiverId, Long timestamp, Double amount) {

            this.instance.setAmount(amount);
            this.instance.setRemarks("");
            this.instance.setSenderId(senderId);
            this.instance.setReceiverId(receiverId);
            this.instance.setTimestamp(timestamp);

            return this;
        }

        public TransferHistory get() {

            this.instance.makeKey();

            return this.instance;
        }
    }
}
