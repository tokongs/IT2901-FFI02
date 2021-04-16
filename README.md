# IT2901-FFI02

This repository contains the code base for three seperate projects. 
* An MQTT broker 
* An MQTT client library 
* A CLI application for testing puposes

The broker and client library are written in Java and based on [HiveMQ Community Edition](https://github.com/hivemq/hivemq-community-edition) and [HiveMQ MQTT Client](https://github.com/hivemq/hivemq-mqtt-client) respsectively. The CLI application is written in Kotlin and relies heavily on the CLI library [Clikt](https://ajalt.github.io/clikt/).

A [user manual](https://github.com/tokongs/IT2901-FFI02/wiki/User-manual) and [developer guide](https://github.com/tokongs/IT2901-FFI02/wiki/Developer-guide) can be found in the project [wiki](https://github.com/tokongs/IT2901-FFI02/wiki).

## Quick start

The quickest way to get the projects up and running is to run the docker images. For this you'll need to have docker installed. The user manual explains how you would spin up the docker containers. 

The above assumes you don't want to modify the code base. If you want to run and modify the projects locally just clone the repo and use gradle to start the project. JDK 11 or above is a requirement for this to work. You do not need to have gradle installed as gradle is packaged with the source code. 
