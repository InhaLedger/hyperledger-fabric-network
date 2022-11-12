package com.inha.coinkaraoke.ledgerApi.entityUtils;

public abstract class Entity implements Serializable {

    protected static final String INDEX_KEY_DELIMITER = ":";

    protected String key;

    static String[] splitKey(String key) {

        return key.split(":");
    }

    abstract protected void makeKey();

    public String getKey(){
        return key;
    }

}
