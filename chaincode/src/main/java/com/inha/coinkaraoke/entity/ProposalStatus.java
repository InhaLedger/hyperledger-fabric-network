package com.inha.coinkaraoke.entity;

public enum ProposalStatus {
    PROGRESS, // 보상 정산이 진행중인 제안 게시글
    DELETE,   // 삭제된 게시글
    COMPLETE  // 보상 정산이 더이상 진행되지 않는 게시글
}
