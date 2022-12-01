package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public abstract class Entity implements Serializable {

    @JsonIgnore
    protected Key key;

    static String[] splitKey(Key key) {

        return key.split();
    }

    abstract protected void makeKey();

    public Key getKey(){
        return key;
    }

    @JsonIgnore
    public String getSimpleClassName() {
        return this.getClass().getSimpleName();
    }

}
