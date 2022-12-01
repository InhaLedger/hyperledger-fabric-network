package com.inha.coinkaraoke.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Entity;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import com.inha.coinkaraoke.ledgerApi.entityUtils.ObjectMapperHolder;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMapperHolderTest {

    @Test
    public void serializeTest() throws IOException {

        TestEntity testEntity = new TestEntity("myName", 12, "Korea", "man");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(testEntity);
        assertThat(json).isEqualTo("{\"age\":12,\"gender\":\"man\",\"address\":\"Korea\",\"name\":\"myName\"}");

        byte[] bytes = ObjectMapperHolder.serialize(testEntity);
        assertThat(bytes).isEqualTo(json.getBytes(StandardCharsets.UTF_8));
    }

    @Getter
    @JsonPropertyOrder({"age", "gender", "address", "name"})
    static class TestEntity extends Entity implements Serializable {

        private final String name;
        private final Integer age;
        private final String address;
        private final String gender;

        public TestEntity(String name, Integer age, String address, String gender) {
            this.name = name;
            this.age = age;
            this.address = address;
            this.gender = gender;
        }

        public String toString() {
            return String.join("-", name, age.toString(), address, gender);
        }

        @Override
        protected void makeKey() {
            this.key = Key.of(this.name);
        }
    }

}
