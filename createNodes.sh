#/bin/bash


. env.sh  #import


function checkPrereqs() {
      ## Check if your have cloned the peer binaries and configuration files.
      peer version > /dev/null 2>&1

      if [[ $? -ne 0 || ! -d "./bin" ]]; then
          errorln "Peer binary and configuration files not found.."
          errorln
          errorln "Follow the instructions in the Fabric docs to install the Fabric Binaries:"
          errorln "https://hyperledger-fabric.readthedocs.io/en/latest/install.html"
          exit 1
      fi
      # use the fabric tools container to see if the samples and binaries match your
      # docker images
      LOCAL_VERSION=$(peer version | sed -ne 's/^ Version: //p')
      DOCKER_IMAGE_VERSION=$(${CONTAINER_CLI} run --rm hyperledger/fabric-tools:latest peer version | sed -ne 's/^ Version: //p')

      infoln "LOCAL_VERSION=$LOCAL_VERSION"
      infoln "DOCKER_IMAGE_VERSION=$DOCKER_IMAGE_VERSION"

      if [ "$LOCAL_VERSION" != "$DOCKER_IMAGE_VERSION" ]; then
          warnln "Local fabric binaries and docker images are out of  sync. This may cause problems."
      fi

      for UNSUPPORTED_VERSION in $NONWORKING_VERSIONS; do
          infoln "$LOCAL_VERSION" | grep -q $UNSUPPORTED_VERSION
          if [ $? -eq 0 ]; then
              fatalln "Local Fabric binary version of $LOCAL_VERSION does not match the versions supported by the test network."
          fi

          infoln "$DOCKER_IMAGE_VERSION" | grep -q $UNSUPPORTED_VERSION
          if [ $? -eq 0 ]; then
              fatalln "Fabric Docker image version of $DOCKER_IMAGE_VERSION does not match the versions supported by the test network."
          fi
      done
 }



# use this as the default docker-compose yaml definition
COMPOSE_FILE_BASE=compose-test-net.yaml
# docker-compose.yaml file if you are using couchdb
COMPOSE_FILE_COUCH=compose-couch.yaml
# certificate authorities compose file
COMPOSE_FILE_CA=compose-ca.yaml

function peerUp() {
    
    checkPrereqs

    if [ ! -d "organizations/peerOrganizations" ]; then
        fatalln "There are not any Organizations! Please register orgs to CAs!"
    fi 

    COMPOSE_FILES="-f compose/${COMPOSE_FILE_BASE} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_BASE} -f compose/${COMPOSE_FILE_COUCH} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_COUCH}"

    DOCKER_SOCK="${DOCKER_SOCK}" ${CONTAINER_CLI_COMPOSE} ${COMPOSE_FILES} up -d 2>&1

    $CONTAINER_CLI ps -a
    if [ $? -ne 0 ]; then
      fatalln "Unable to start network"
    fi
}

function peerDown() {

    COMPOSE_FILES="-f compose/${COMPOSE_FILE_BASE} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_BASE} -f compose/${COMPOSE_FILE_COUCH} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_COUCH}"
  
    DOCKER_SOCK=$DOCKER_SOCK ${CONTAINER_CLI_COMPOSE} ${COMPOSE_FILES} down --volumes --remove-orphans

  if [ "$CLEAR" == "true" ]; then
  
    # Bring down the network, deleting the volumes
    ${CONTAINER_CLI} volume rm docker_orderer.coinkaraoke.com docker_peer0.org1.coinkaraoke.com docker_peer1.org1.coinkaraoke.com

    #Cleanup the chaincode containers
    clearContainers

    #Cleanup images
    removeUnwantedImages
    

    ${CONTAINER_CLI} kill $(${CONTAINER_CLI} ps -q --filter name=ccaas) || true
    
    # remove orderer block and other channel configuration transactions and certs
    ${CONTAINER_CLI} run --rm -v "$(pwd):/data" busybox sh -c 'cd /data && rm -rf system-genesis-block/*.block organizations/peerOrganizations organizations/ordererOrganizations'
   
    # remove channel and script artifacts
    ${CONTAINER_CLI} run --rm -v "$(pwd):/data" busybox sh -c 'cd /data && rm -rf channel-artifacts log.txt *.tar.gz'
  fi

}


# when terminate Peers, default is not to remove fabric ledger artifacts
CLEAR="false"

## Parse mode
if [[ $# -lt 1 ]] ; then
  printHelp
  exit 0
else
  MODE=$1
  shift
fi

# parse flags

while [[ $# -ge 1 ]] ; do
  key="$1"
  case $key in
 -clear )
    CLEAR="true"
    ;;
  * )
    errorln "Unknown flag: $key"
    printHelp
    exit 1
    ;;
  esac
  shift
done

# Are we generating crypto material with this command?
#if [ ! -d "organizations/peerOrganizations" ]; then
#  CRYPTO_MODE="with crypto from '${CRYPTO}'"
#else
#  CRYPTO_MODE=""
#fi

# Determine mode of operation and printing out what we asked for
if [ "$MODE" == "up" ]; then
    infoln "Starting Peers"
    peerUp
elif [ "$MODE" == "down" ]; then
    infoln "Stopping Peers"
    peerDown
fi


