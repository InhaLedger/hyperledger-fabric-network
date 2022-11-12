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

@Slf4j
@DataType
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account extends Entity {

    @Property private String ownerId;
    @Property private Double balance;

    @Override
    public byte[] serialize() {

        JSONObject json = new JSONObject();
        json.put("ownerId", this.ownerId);
        json.put("balance", this.balance);

        log.info("complete serializing object: {} \n{}", this.key, json);
        return json.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void makeKey() {
        this.key = String.join(INDEX_KEY_DELIMITER, this.getClass().getName(), ownerId);
    }

    public Double getBalance() {
        return balance;
    }

    public static class Builder {

        private Account instance;

        public Builder() {

            this.instance = new Account();
        }

        public Builder createInstance(String ownerId, Double balance) {

            this.instance.setOwnerId(ownerId);
            this.instance.setBalance(balance);

            return this;
        }

        public Account get() {

            this.instance.makeKey();

            return this.instance;
        }
    }

}
