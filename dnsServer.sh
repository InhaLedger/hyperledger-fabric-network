#!/bin/bash
 docker run -d --rm --hostname dns.mageddo --name dns-server -p 5380:5380 \
  -v /opt/dns-proxy-server/conf:/app/conf \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /etc/resolv.conf:/etc/resolv.conf \
  -e MG_REGISTER_CONTAINER_NAMES=1 \
  defreitas/dns-proxy-server

