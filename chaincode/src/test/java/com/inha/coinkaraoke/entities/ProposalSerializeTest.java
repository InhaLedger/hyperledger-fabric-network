package com.inha.coinkaraoke.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.Vote;
import com.inha.coinkaraoke.ledgerApi.entityUtils.ObjectMapperHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class ProposalSerializeTest {

    @Nested
    public class ProposalTest {
        @Test
        @DisplayName("proposal 를 deterministic 하게 직렬화 한다.")
        public void serializeProposalTest() throws JsonProcessingException {
            Stake stake = Stake.forEdit("user1", 12394812L);
            Proposal proposal = new Proposal("123", "board1", "user1", 12394812L, stake.getKey());

            String json = new ObjectMapper().writeValueAsString(proposal);
            assertThat(json).isEqualTo("{\"id\":\"123\",\"userId\":\"user1\",\"type\":\"board1\",\"timeStamp\":12394812,\"status\":\"PROGRESS\",\"upVotes\":0.0,\"downVotes\":0.0,\"editStakeKey\":{\"value\":\"user1:12394812\"}}");

            byte[] bytes = ObjectMapperHolder.serialize(proposal);
            assertThat(bytes).isEqualTo(json.getBytes(StandardCharsets.UTF_8));

            Proposal restoredEntity = ObjectMapperHolder.deserialize(bytes, Proposal.class);
            assertThat(restoredEntity)
                    .usingRecursiveComparison()
                    .isEqualTo(proposal);
        }
    }

    @Nested
    public class VoteTest {

        @Test
        public void serializeVoteTest() throws JsonProcessingException {

            Stake stake = Stake.forEdit("user1", 12394812L);
            Proposal proposal = new Proposal("123", "board1", "user1", 12394812L, stake.getKey());

            Stake forVote = Stake.forVote("user2", 2.1, 12399912L);
            Vote vote = Vote.to(proposal, forVote, "up");


            String json = new ObjectMapper().writeValueAsString(vote);
            assertThat(json).isEqualTo("{\"voteType\":\"up\",\"stakeKey\":{\"value\":\"user2:12399912\"},\"userId\":\"user2\",\"proposalKey\":{\"value\":\"123:board1\"},\"amounts\":2.1,\"processed\":false,\"rewarded\":false,\"timestamp\":12399912}");
            System.out.println(json);

            byte[] bytes = ObjectMapperHolder.serialize(vote);
            assertThat(bytes).isEqualTo(json.getBytes(StandardCharsets.UTF_8));

            Vote restoredEntity = ObjectMapperHolder.deserialize(bytes, Vote.class);
            assertThat(restoredEntity)
                    .usingRecursiveComparison()
                    .isEqualTo(vote);
        }

        @Test
        public void deserializeTest() {
            String json = "{\n" +
                    "  \"amounts\": 2.3,\n" +
                    "  \"proposalKey\": {\n" +
                    "    \"value\": \"52f6c60e-0864-437e-8379-83639a0caa43:packboard\"\n" +
                    "  },\n" +
                    "  \"processed\": false,\n" +
                    "  \"rewarded\": false,\n" +
                    "  \"stakeKey\": {\n" +
                    "    \"value\": \"47:1670150679912\"\n" +
                    "  },\n" +
                    "  \"timestamp\": 1670150679912,\n" +
                    "  \"userId\": \"47\",\n" +
                    "  \"voteType\": \"down\"\n" +
                    "}";
            Vote vote = ObjectMapperHolder.deserialize(json.getBytes(), Vote.class);

            assertThat(vote.getVoteType()).isEqualTo("down");
            assertThat(vote.isProcessed()).isEqualTo(false);
            assertThat(vote.getRewarded()).isEqualTo(false);
            assertThat(vote.getAmounts()).isEqualTo(2.3);
            assertThat(vote.getTimestamp()).isEqualTo(1670150679912L);
            assertThat(vote.getUserId()).isEqualTo("47");
            assertThat(vote.getProposalKey().getValue()).isEqualTo("52f6c60e-0864-437e-8379-83639a0caa43:packboard");
            assertThat(vote.getStakeKey().getValue()).isEqualTo("47:1670150679912");
        }

    }


}
