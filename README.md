# CS347 Project #

## Description ##

This repository contains a simplifed implementation of Primary-Backup Object Replications in Java using a passive backup scheme via a Replica Proxy implementation, as part of the CS347 coursework.

The protocol is presented as a simple buy and sell system, which in this case uses the notion of stocks to demonstrate a proxy based fault tolerant system

## Setup ##

1) Start by changing your directory to the project root directory.

2) Start the gateway using the instructions below.

3) Start up one or more server using the instructions below.

4) Start up a client using the instructions below.

## Starting the gateway ##

There are 2 possible ways to start the gateway.

Option 1: Using the launcher provided.

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: ./rpn start gateway "port"

Note: "port" should be replaced by the port you'd like to run the gateway on. e.g. ./rpn start gateway 43590

Option 2: Using the java command

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: java -cp ./out/production/Gateway:./libraries/netty.jar: rpn.gateway.Gateway "port"

Note: "port" should be replaced by the port you'd like to run the gateway on. e.g. java -cp ./out/production/Gateway:./libraries/netty.jar: rpn.gateway.Gateway 43590

## Starting a Server ##

There are 2 possible ways to start a server.

Option 1: Using the launcher provided.

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: ./rpn start server "gatewayHost" "gatewayPort" "serverPort"

Note: "gatewayHost", "gatewayPort", and "serverPort" should be replaced with the gateway host IP, gateway port, and the port you'd like to run the server on. e.g. ./rpn start server 0.0.0.0 43590 43591

Option 2: Using the java command

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: java -cp ./out/production/Server:./libraries/netty.jar: rpn.server.Server "gatewayHost" "gatewayPort" "serverPort"

Note: "gatewayHost", "gatewayPort", and "serverPort" should be replaced with the gateway host IP, gateway port, and the port you'd like to run the server on. e.g. java -cp ./out/production/Server:./libraries/netty.jar: rpn.server.Server 0.0.0.0 43590 43591

## Starting a Client ##

There are 2 possible ways to start a client. You may start multiple clients.

Option 1: Using the launcher provided.

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: ./rpn start client "gatewayHost" "gatewayPort"

Note: "gatewayHost", and "gatewayPort" should be replaced by the gateway host IP, and gateway port. e.g. ./rpn start client 0.0.0.0 43590

Option 2: Using the java command

1) Open a terminal window and make sure you are in the root directory of the project.

2) Execute the following command: java -cp ./out/production/Client:./libraries/netty.jar: rpn.client.Client "gatewayHost" "gatewayPort"

Note: "gatewayHost", and "gatewayPort" should be replaced by the gateway host IP, and gateway port. e.g. java -cp ./out/production/Client:./libraries/netty.jar: rpn.client.Client 0.0.0.0 43590

## Authors ##
Ishe Gambe
Jasmine Mann 
Naqash Tanzeel
Luke Vincent
Jordan Wyatt
