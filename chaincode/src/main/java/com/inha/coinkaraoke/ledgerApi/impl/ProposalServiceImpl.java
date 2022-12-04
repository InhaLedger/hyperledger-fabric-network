package com.inha.coinkaraoke.ledgerApi.impl;

import com.inha.coinkaraoke.entity.Proposal;
import com.inha.coinkaraoke.entity.Stake;
import com.inha.coinkaraoke.entity.Vote;
import com.inha.coinkaraoke.ledgerApi.AccountService;
import com.inha.coinkaraoke.ledgerApi.ProposalService;
import com.inha.coinkaraoke.ledgerApi.RewardService;
import com.inha.coinkaraoke.ledgerApi.entityUtils.EntityManager;
import com.inha.coinkaraoke.ledgerApi.entityUtils.Key;
import com.inha.coinkaraoke.ledgerApi.entityUtils.ObjectMapperHolder;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.Optional;

@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private final EntityManager entityManager;
    private final RewardService rewardService;
    private final AccountService accountService;


    @Override
    public Proposal createProposal(final Context ctx, String id, String userId, String type, Long timestamp, Stake stake) {

        Proposal proposal = new Proposal(id, type, userId, timestamp, stake.getKey());
        entityManager.saveEntity(ctx.getStub(), proposal);

        return proposal;
    }

    @Override
    public Optional<Proposal> findProposal(final Context ctx, String proposalId, String type) {

        return entityManager.getById(ctx.getStub(), Key.of(proposalId, type), Proposal.class)
                .map(entity -> (Proposal) entity);
    }

    private QueryResultsIterator<KeyValue> fetchProposals(Context ctx, Long timestamp, Integer limit) {

        if (limit == null) limit = 25;

        return ctx.getStub()
                .getQueryResult(String.format("{\n" +
                        "   \"selector\": {\n" +
                        "      \"timeStamp\": {\n" +
                        "         \"$lte\": %d\n" +
                        "      },\n" +
                        "      \"status\": \"PROGRESS\",\n" +
                        "      \"_id\": {\n" +
                        "         \"$regex\": \"(?i).*Proposal*\"\n" +
                        "      }\n" +
                        "   },\n" +
                        "   \"limit\": %d" +
                        "}", timestamp, limit));
    }

    private QueryResultsIterator<KeyValue> fetchVotesByProposal(Context ctx, Key proposalKey) {

        return ctx.getStub()
                .getQueryResult(String.format("{\n" +
                        "   \"selector\": {\n" +
                        "      \"proposalKey.value\": \"%s\",\n" +
                        "      \"_id\": {\n" +
                        "         \"$regex\": \"(?)Vote*\"\n" +
                        "      }\n" +
                        "   }\n" +
                        "}", proposalKey.getValue()));
    }


    public Integer finalizeProposals(final Context ctx, Long timestamp, Double rewardPerProposal, Integer batchSize) {

        log.info("start to finalize.");
        int successCount = 0;

        try (QueryResultsIterator<KeyValue> proposals = this.fetchProposals(ctx, timestamp, batchSize)) {

            for (KeyValue json : proposals) {

                Proposal proposal = ObjectMapperHolder.deserialize(json.getValue(), Proposal.class);

                if (proposal.isUnderProgress() && this.finalizeProposal(ctx, proposal, timestamp, rewardPerProposal)) {
                    log.info("success to finalize proposal[{}]", proposal.getKey().getValue());
                    successCount += 1;
                } else {
                    log.info("cannot finalize proposal[{}]", proposal.getKey().getValue());
                }
            }
        } catch (Exception e) {
            log.error("error occurred when finalizing");
            throw new RuntimeException(e);
        }

        log.info("finish finalizing.");
        return successCount;
    }

    private Boolean finalizeProposal(final Context ctx, Proposal proposal, Long timestamp, Double rewardPerProposal) {

        try (QueryResultsIterator<KeyValue> votes = this.fetchVotesByProposal(ctx, proposal.getKey())) {

            log.info("successfully fetched votes[{}]", votes);

            Double voteShare = rewardService.extractVotersShare(rewardPerProposal);

            if (rewardService.isWin(proposal)) {

                log.info("proposal win by votes");

                Double sharePerVote = voteShare / proposal.getUpVotes();

                votes.forEach(_json -> {

                    Vote vote = ObjectMapperHolder.deserialize(_json.getValue(), Vote.class);

                    if (!vote.isProcessed()) {

                        if (vote.isUpVote()) {
                            accountService.transferFromSystemTo(ctx, vote.getUserId(), timestamp, sharePerVote * vote.getAmounts());
                            vote.markAsRewarded();

                        } else if (vote.isDownVote()) {
                            vote.markAsNotRewarded();
                        }
                        entityManager.updateEntity(ctx.getStub(), vote);
                    }

                });

                Double editorShare = rewardService.extractEditorShare(rewardPerProposal);
                accountService.transferFromSystemTo(ctx, proposal.getUserId(), timestamp, editorShare);
                proposal.markAsFinalized();
                entityManager.updateEntity(ctx.getStub(), proposal);

            } else {

                log.info("proposal lose by votes");

                Double sharePerVote = voteShare / proposal.getDownVotes();

                votes.forEach(_json -> {
                    Vote vote = ObjectMapperHolder.deserialize(_json.getValue(), Vote.class);
                    if (!vote.isProcessed()) {

                        if (vote.isDownVote()) {
                            accountService.transferFromSystemTo(ctx, vote.getUserId(), timestamp, sharePerVote * vote.getAmounts());
                            vote.markAsRewarded();

                        } else if (vote.isUpVote()) {
                            vote.markAsNotRewarded();
                        }
                        entityManager.updateEntity(ctx.getStub(), vote);
                        log.info("vote[{}] is processed", vote.getKey().getValue());
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }


    private ProposalServiceImpl(EntityManager entityManager, RewardService rewardService, AccountService accountService) {
        this.entityManager = entityManager;
        this.rewardService = rewardService;
        this.accountService = accountService;
    }

    private static class LazyHolder {
        public static final ProposalServiceImpl INSTANCE = new ProposalServiceImpl(
                EntityManager.Factory.getInstance(), DefaultRewardStrategy.getInstance(), AccountServiceImpl.getInstance()
        );
    }

    public static ProposalServiceImpl getInstance() {
        return ProposalServiceImpl.LazyHolder.INSTANCE;
    }
}
