package com.inha.coinkaraoke.entity;

import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import java.nio.charset.StandardCharsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType
@Slf4j
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountHistory extends Entity {

    @Property private double amount;
    @Property private Long timestamp;
    @Property private String receiverId;
    @Property private String senderId;
    @Property private String remarks; // 기타

    @Override
    protected void makeKey() {
        this.key = String.join(INDEX_KEY_DELIMITER, this.getClass().getName(), this.timestamp.toString());
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public byte[] serialize() {

        JSONObject json = new JSONObject();
        json.put("senderId", senderId);
        json.put("receiverId", receiverId);
        json.put("amount", amount);
        json.put("timestamp", timestamp);
        json.put("remarks", remarks);

        log.info("complete serializing object: {} \n{}", this.key, json);
        return json.toString().getBytes(StandardCharsets.UTF_8);
    }



    public static class Builder {

        private AccountHistory instance;

        public Builder() {

            this.instance = new AccountHistory();
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

        public AccountHistory get() {

            this.instance.makeKey();

            return this.instance;
        }
    }
}
