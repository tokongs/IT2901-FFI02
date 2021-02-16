#!/bin/bash

docker pull tokongs/ffi02:latest
docker stop broker
docker rm broker
docker run --name=broker --restart=always -p 8000:8000 -p 1883:1883 -d tokongs/ffi02:latest
