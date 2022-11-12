package com.inha.coinkaraoke.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inha.coinkaraoke.entity.Account;
import com.inha.coinkaraoke.entity.TransferHistory;
import com.inha.coinkaraoke.ledgerApi.entityUtils.ObjectMapperHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSerializeTest {

    @Nested
    public class AccountTest {

        @Test
        @DisplayName("account 객체를 Deterministic 하게 직렬화한다.")
        public void serializeAccountTest() throws JsonProcessingException {

            Account user1 = new Account("user1");

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(user1);
            assertThat(json)
                    .isEqualTo("{\"ownerId\":\"user1\",\"availableBalance\":0.0,\"stakedBalance\":0.0,\"stakeList\":{}}");

            byte[] bytes = ObjectMapperHolder.serialize(user1);
            assertThat(bytes).isEqualTo(json.getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("account 객체를 deserialization 한다.")
        public void deserializeAccountTest() {
            //given
            Account originalAccount = new Account("user1");
            originalAccount.receive(23.8d);

            //when
            byte[] bytes = ObjectMapperHolder.serialize(originalAccount);
            Account restoredAccount = ObjectMapperHolder.deserialize(bytes, Account.class);

            //then
            assertThat(restoredAccount)
                    .usingRecursiveComparison()
                    .isEqualTo(originalAccount);
        }
    }

    @Nested
    public class TransferHistoryTest {

        @Test
        @DisplayName("transferHistory 객체를 Deterministic 하게 직렬화한다.")
        public void serializeTransferHistoryTest() throws JsonProcessingException {

            TransferHistory history = new TransferHistory.Builder()
                    .createInstance("senderId", "receiverId", 1242352465L, 13.2d)
                    .get();

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(history);
            assertThat(json)
                    .isEqualTo("{\"senderId\":\"senderId\",\"receiverId\":\"receiverId\",\"amount\":13.2,\"timestamp\":1242352465,\"remarks\":\"\"}");

            byte[] bytes = ObjectMapperHolder.serialize(history);
            assertThat(bytes).isEqualTo(json.getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("transferHistory 객체를 deserialization 한다.")
        public void deserializeTransferHistoryTest() {
            //given
            TransferHistory original = new TransferHistory.Builder()
                    .createInstance("senderId", "receiverId", 1242352465L, 13.2d)
                    .get();

            //when
            byte[] bytes = ObjectMapperHolder.serialize(original);
            TransferHistory restoredHistory = ObjectMapperHolder.deserialize(bytes, TransferHistory.class);

            //then
            assertThat(restoredHistory)
                    .usingRecursiveComparison()
                    .isEqualTo(original);
        }

    }
}
