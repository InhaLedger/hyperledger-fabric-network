package com.inha.coinkaraoke.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeMap;

@Slf4j
@DataType
@Getter @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"ownerId", "availableBalance", "stakedBalance", "stakeList"})
public class Account extends Entity {

    @Property private String ownerId;
    @Property private Double availableBalance;
    @Property private Double stakedBalance;
    @Property private TreeMap<Long, Stake> stakeList;


    @Override
    protected void makeKey() {
        this.key = String.join(INDEX_KEY_DELIMITER, ownerId);
    }

    @JsonIgnore
    public Double getTotalBalance() {
        return availableBalance + stakedBalance;
    }

    public void addStake(Stake stake) {
        Double amount = stake.getAmount();
        if (availableBalance < amount)
            throw new ChaincodeException("not enough available balance to stake.");

        this.availableBalance -= amount;
        this.stakedBalance += amount;
        this.stakeList.put(stake.getCompleteTimestamp(), stake);
    }

    public void transfer(Double amount) {
        if (availableBalance < amount)
            throw new ChaincodeException("not enough available balance to stake.");

        this.availableBalance -= amount;
    }

    public void receive(Double amount) {
        this.availableBalance += amount;
    }

    public Account(String ownerId) {
        this.ownerId = ownerId;
        this.stakeList = new TreeMap<>((Comparator<Long> & Serializable) Long::compareTo);  // serializable 하도록 comparator 설정
        this.availableBalance = 0.0d;
        this.stakedBalance = 0.0d;
        this.makeKey();
    }

}
