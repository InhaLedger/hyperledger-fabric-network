package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Key {

    protected static final String INDEX_KEY_DELIMITER = ":";

    @JsonIgnore
    private final String value;

    public static Key of(String... values) {
        return new Key(String.join(INDEX_KEY_DELIMITER, values));
    }
    private Key(String value) {
        this.value = value;
    }

    public String[] split() {
        return value.split(INDEX_KEY_DELIMITER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key key = (Key) o;
        return value.equals(key.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
