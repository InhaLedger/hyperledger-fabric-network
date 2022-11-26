### 1. CA 서버 구성하기
  - `./ca.sh create` : `org1-ca`와 `orderer-ca` 두 개를 구성 후 node와 admin 을 위한 각종 crypto materials 를 생성.
  - `./ca.sh up` : `org1-ca` 와 `orderer-ca` 두 개를 구성함. (기존에 생성한 cryto materials를 그대로 이용)
  - `./ca.sh down` : 두개의 ca 를 종료 (저장된 데이터는 삭제하지 않음, -clear 옵션을 주면 ca 내부 데이터 모두 삭제)

### 2. Peer 노드 구성하기
  - `./createNodes.sh up` : `peer0.org1.coinkaraoke.com` `peer1.org1.coinkaraoke.com` `orderer.coinkaraoke.com`
  - `./createNodes.sh down` : 모든 peer 종료 (-clear 옵션을 주면 ledger data 모두 삭제)

### 3. Channel 구성하기
  - `./channel.sh  createChannel -ca -c {CHANNEL_NAME} [...options]`

### 4. chaincode 배포하기
  - `./channel.sh deployCC -ccn chaincode -ccp ./chaincode -ccl java [...options]`

### [optional] 5. dns proxy server 구동
  - `./dnsServer.sh`
  - 각 노드 컨테이너들은 `peer0.org1.coinkaraoke.com`처럼 도메인 형식의 이릉을 갖고 있음. 로컬에서 해당 도메인 이름으로 라우팅을 하고 싶으면 실행. 실행되면 자동으로 컨테이너 실별해서 라우팅 해줌.
  - 단 비정상 종료 시, 외부로의 라우팅이 정상적으로 수행되지 않는 문제가 있음. -> 다시 켰다가 docker stop dns-server 로 종료시켜주면 됨.
