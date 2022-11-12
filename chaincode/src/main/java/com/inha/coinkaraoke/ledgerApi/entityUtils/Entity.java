package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    protected static final String INDEX_KEY_DELIMITER = ":";

    @JsonIgnore
    protected String key;

    static String[] splitKey(String key) {

        return key.split(":");
    }

    abstract protected void makeKey();

    public String getKey(){
        return key;
    }

}
