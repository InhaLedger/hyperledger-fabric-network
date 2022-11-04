### 1. CA 서버 구성하기
  - `./ca.sh up -ca` : 일반 유저용 ca 와 orderer용 ca 두 개를 구성함.
  - `./ca.sh down` : 두개의 ca 를 종료 (저장된 데이터는 삭제하지 않음, -clear 옵션을 주면 ca 내부 데이터 모두 삭제)

### 2. Peer 노드 구성하기
  - `./createPeer up` : peer0.org1.coinkaraoke.com peer1.org1.coinkaraoke.com orderer.coinkaraoke.com
  - `./createPeer down` : 모든 peer 종료 (-clear 옵션을 주면 ledger data 모두 삭제)
  
