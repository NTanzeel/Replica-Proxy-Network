# CS347 Project #

## Description ##

This repository contains a simplifed implementation of Primary-Backup Object Replications in Java using a passive backup scheme via a Replica Proxy implementation, as part of the CS347 coursework.

The protocol is presented as a simple buy and sell system, which in this case uses the notion of stocks to demonstrate a proxy based fault tolerant system

## Setup ##
...
### Commands ###

Gateway : java -cp ./out/production/gateway:./libraries/netty.jar: rpn.gateway.Gateway

Server  : java -cp ./out/production/server:./libraries/netty.jar: rpn.server.Server

Client  : java -cp ./out/production/client:./libraries/netty.jar: rpn.client.Client

## Authors ##
Ishe Gambe
Jasmine Mann 
Naqash Tanzeel
Luke Vincent
Jordan Wyatt
