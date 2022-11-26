#!/bin/bash

function create() {
    ./ca.sh create
    ./createNodes.sh up
    ./channel.sh createChannel -ca -c mychannel
}
function up() {
    ./ca.sh up
    ./createNodes.sh up
}

function makeClear() {
     docker stop dns-server
    ./createNodes.sh down -clear
    ./ca.sh down -clear
}

function down() {
    ./createNodes.sh down
    ./ca.sh down
}

function powerOffDNSServer() {
    local LINE=$(docker ps | grep dns-server | wc -l)
    if [ $LINE -eq 2 ]; then 
        echo "Find dns-server, stopping.."
        docker stop dns-server
    fi
}

mode=$1
if [ "$mode" = "create" ]; then
    create
if [ "$mode" = "up" ]; then
    up  
elif [ "$mode" = "down" ]; then
    powerOffDNSServer
    down
elif [ "$mode" = "clear" ]; then
    powerOffDNSServer
    makeClear
fi

