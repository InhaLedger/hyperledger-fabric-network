#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#

# This is a collection of bash functions used by different scripts

# imports
. scripts/utils.sh

export CORE_PEER_TLS_ENABLED=true
export ORDERER_CA=${PWD}/organizations/ordererOrganizations/coinkaraoke.com/tlsca/tlsca.coinkaraoke.com-cert.pem
export PEER0_ORG1_CA=${PWD}/organizations/peerOrganizations/org1.coinkaraoke.com/tlsca/tlsca.org1.coinkaraoke.com-cert.pem
export PEER0_ORG2_CA=${PWD}/organizations/peerOrganizations/org2.coinkaraoke.com/tlsca/tlsca.org2.coinkaraoke.com-cert.pem
export PEER0_ORG3_CA=${PWD}/organizations/peerOrganizations/org3.coinkaraoke.com/tlsca/tlsca.org3.coinkaraoke.com-cert.pem
export ORDERER_ADMIN_TLS_SIGN_CERT=${PWD}/organizations/ordererOrganizations/coinkaraoke.com/orderers/orderer.coinkaraoke.com/tls/server.crt
export ORDERER_ADMIN_TLS_PRIVATE_KEY=${PWD}/organizations/ordererOrganizations/coinkaraoke.com/orderers/orderer.coinkaraoke.com/tls/server.key

# Set environment variables for the peer org
setGlobals() {
  if [ "$#" -ne 2 ]; then
    errorln "illegal number of parameters. Should pass the both (#org) and #(peer)"
  fi

  local USING_ORG=""
  local USING_PEER=$2
  
  if [ -z "$OVERRIDE_ORG" ]; then
    USING_ORG=$1
  else
    USING_ORG="${OVERRIDE_ORG}"
  fi
  infoln "Using peer${USING_PEER} of organization ${USING_ORG}"
  if [ $USING_ORG -eq 1 ]; then
  
    export CORE_PEER_LOCALMSPID="Org1MSP"
    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG1_CA
    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.coinkaraoke.com/users/Admin@org1.coinkaraoke.com/msp
    if [ "$USING_PEER" = 0 ]; then
    	export CORE_PEER_ADDRESS=localhost:7051
    elif [ "$USING_PEER" = 1 ]; then 
        export CORE_PEER_ADDRESS=localhost:9051
    else
    	errorln "#peer not specified."
    fi
#  elif [ $USING_ORG -eq 2 ]; then
#    export CORE_PEER_LOCALMSPID="Org2MSP"
#    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG2_CA
#    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.coinkaraoke.com/users/Admin@org2.coinkaraoke.com/msp
#    export CORE_PEER_ADDRESS=localhost:9051

#  elif [ $USING_ORG -eq 3 ]; then
#    export CORE_PEER_LOCALMSPID="Org3MSP"
#    export CORE_PEER_TLS_ROOTCERT_FILE=$PEER0_ORG3_CA
#    export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org3.coinkaraoke.com/users/Admin@org3.coinkaraoke.com/msp
#    export CORE_PEER_ADDRESS=localhost:11051
  else
    errorln "ORG Unknown"
  fi

  if [ "$VERBOSE" == "true" ]; then
    env | grep CORE
  fi
}

# Set environment variables for use in the CLI container
setGlobalsCLI() {
  setGlobals $1 $2
  local USING_PEER=$2
  local USING_ORG=""
  if [ -z "$OVERRIDE_ORG" ]; then
    USING_ORG=$1
  else
    USING_ORG="${OVERRIDE_ORG}"
  fi
  
  if [ $USING_ORG -eq 1 ]; then
    if [ $USING_PEER -eq 0 ]; then
    	export CORE_PEER_ADDRESS=localhost:7051 #peer$USING_PEER.org1.coinkaraoke.com:7051
    elif [ $USING_PEER -eq 1 ]; then 
        export CORE_PEER_ADDRESS=localhost:9051 #peer$USING_PEER.org1.coinkaraoke.com:9051
    else
    	errorln "#peer not specified."
    fi

#  elif [ $USING_ORG -eq 2 ]; then
#    export CORE_PEER_ADDRESS=peer0.org2.coinkaraoke.com:9051
#  elif [ $USING_ORG -eq 3 ]; then
#    export CORE_PEER_ADDRESS=peer0.org3.coinkaraoke.com:11051
  else
    errorln "ORG Unknown"
  fi
}

# parsePeerConnectionParameters $@
# Helper function that sets the peer connection parameters for a chaincode
# operation
parsePeerConnectionParameters() {
  PEER_CONN_PARMS=()
  PEERS=""
  while [ "$#" -gt 0 ]; do
    setGlobals $1 $2
    PEER="peer$2.org$1"
    ## Set peer addresses
    if [ -z "$PEERS" ]
    then
	PEERS="$PEER"
    else
	PEERS="$PEERS $PEER"
    fi
    PEER_CONN_PARMS=("${PEER_CONN_PARMS[@]}" --peerAddresses $CORE_PEER_ADDRESS)
    ## Set path to TLS certificate
    CA=PEER0_ORG$1_CA
    TLSINFO=(--tlsRootCertFiles "${!CA}")
    PEER_CONN_PARMS=("${PEER_CONN_PARMS[@]}" "${TLSINFO[@]}")
    # shift by two to get to the next organization
    shift
    shift
  done
}

verifyResult() {
  if [ $1 -ne 0 ]; then
    fatalln "$2"
  fi
}
