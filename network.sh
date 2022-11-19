#!/bin/bash

function up() {
    ./ca.sh up
    ./createNodes.sh up
    ./channel.sh createChannel -ca -c mychannel
}

function makeClear() {
    ./createNodes.sh down -clear
    ./ca.sh down -clear
}

function down() {
    ./createNodes.sh down
    ./ca.sh down
}

mode=$1
if [ "$mode" = "up" ]; then
    up
elif [ "$mode" = "down" ]; then
    down
elif [ "$mode" = "clear" ]; then
    makeClear
fi

