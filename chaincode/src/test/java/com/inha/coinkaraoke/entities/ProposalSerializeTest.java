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
    public class proposalTest {
        @Test
        @DisplayName("proposal 를 deterministic 하게 직렬화 한다.")
        public void serializeProposalTest() throws JsonProcessingException {
            Stake stake = Stake.forEdit("user1", 12394812L);
            Proposal proposal = new Proposal("123", "board1", "user1", 12394812L, stake.getKey());

            String json = new ObjectMapper().writeValueAsString(proposal);
            assertThat(json).isEqualTo("{\"id\":\"123\",\"userId\":\"user1\",\"type\":\"board1\",\"timeStamp\":12394812,\"status\":\"PROGRESS\",\"editStakeKey\":{\"value\":\"user1:12394812\"}}");

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
    }


}
