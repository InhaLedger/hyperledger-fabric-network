#!/bin/bash


#ROOTDIR=$(cd "$(dirname "$0")" && pwd)
#export PATH=${ROOTDIR}/bin:${PWD}/bin:$PATH
    #export FABRIC_CFG_PATH=${PWD}/configtx
    #export VERBOSE=false

# push to the required directory & set a trap to go back if needed
#pushd ${ROOTDIR} > /dev/null
#trap "popd > /dev/null" EXIT

#. scripts/utils.sh  #import

#: ${CONTAINER_CLI:="docker"}
#: ${CONTAINER_CLI_COMPOSE:="${CONTAINER_CLI}-compose"}
#infoln "Using ${CONTAINER_CLI} and ${CONTAINER_CLI_COMPOSE}"


. env.sh #import

function checkPrereqs() {

  ## check for fabric-ca
  if [ "$CRYPTO" == "Certificate Authorities" ]; then

    fabric-ca-client version > /dev/null 2>&1
    if [[ $? -ne 0 ]]; then
      errorln "fabric-ca-client binary not found.."
      errorln
      errorln "Follow the instructions in the Fabric docs to install the Fabric Binaries:"
      errorln "https://hyperledger-fabric.readthedocs.io/en/latest/install.html"
      exit 1
    fi
    CA_LOCAL_VERSION=$(fabric-ca-client version | sed -ne 's/ Version: //p')
    CA_DOCKER_IMAGE_VERSION=$(docker run --rm hyperledger/fabric-ca:latest fabric-ca-client version | sed -ne 's/ Version: //p' | head -1)
    infoln "CA_LOCAL_VERSION=$CA_LOCAL_VERSION"
    infoln "CA_DOCKER_IMAGE_VERSION=$CA_DOCKER_IMAGE_VERSION"

    if [ "$CA_LOCAL_VERSION" != "$CA_DOCKER_IMAGE_VERSION" ]; then
      warnln "Local fabric-ca binaries and docker images are out of sync. This may cause problems."
    fi
  fi
}



function caDown() {

  COMPOSE_CA_FILES="-f compose/${COMPOSE_FILE_CA} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_CA}"

  DOCKER_SOCK=$DOCKER_SOCK ${CONTAINER_CLI_COMPOSE} ${COMPOSE_CA_FILES} down --volumes --remove-orphans

   ## remove fabric ca artifacts
  if [ "$CLEAR" == "true" ]; then
  
    ${CONTAINER_CLI} run --rm -v "$(pwd):/data" busybox sh -c 'cd /data && rm -rf organizations/fabric-ca/org1/msp organizations/fabric-ca/org1/tls-cert.pem organizations/fabric-ca/org1/ca-cert.pem organizations/fabric-ca/org1/IssuerPublicKey organizations/fabric-ca/org1/IssuerRevocationPublicKey organizations/fabric-ca/org1/fabric-ca-server.db'
    ${CONTAINER_CLI} run --rm -v "$(pwd):/data" busybox sh -c 'cd /data && rm -rf organizations/fabric-ca/ordererOrg/msp organizations/fabric-ca/ordererOrg/tls-cert.pem organizations/fabric-ca/ordererOrg/ca-cert.pem organizations/fabric-ca/ordererOrg/IssuerPublicKey organizations/fabric-ca/ordererOrg/IssuerRevocationPublicKey organizations/fabric-ca/ordererOrg/fabric-ca-server.db'
   
  fi

}

function caUp() {

  checkPrereqs

  COMPOSE_CA_FILES="-f compose/${COMPOSE_FILE_CA} -f compose/${CONTAINER_CLI}/${CONTAINER_CLI}-${COMPOSE_FILE_CA}"
  DOCKER_SOCK=$DOCKER_SOCK ${CONTAINER_CLI_COMPOSE} ${COMPOSE_CA_FILES} up -d 2>&1
}


# when terminate CAs, default is not to remove fabric ca artifacts
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
  -h )
    printHelp $MODE
    exit 0
    ;;
 -ca )
    CRYPTO="Certificate Authorities"
    ;;
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
  infoln "Starting CAs using ${CRYPTO}"
  caUp
elif [ "$MODE" == "down" ]; then
  infoln "Stopping ca"
  caDown
fi
