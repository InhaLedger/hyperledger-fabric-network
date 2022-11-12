package com.inha.coinkaraoke.ledgerApi.entityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.shim.ChaincodeException;

import java.io.IOException;
import java.util.Arrays;

public class ObjectMapperHolder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] serialize(Object entity) {

        try {
            return objectMapper.writeValueAsBytes(entity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ChaincodeException("cannot serialize this entity: ", entity.toString());
        }
    }

    public static <T extends Entity> T deserialize(byte[] data, Class<T> cls) {

        try {
            T entity = objectMapper.readValue(data, cls);
            entity.makeKey();

            return entity;

        } catch (IOException e) {
            e.printStackTrace();
            throw new ChaincodeException("cannot deserialize this bytes: ", Arrays.toString(data));
        }
    }

}
