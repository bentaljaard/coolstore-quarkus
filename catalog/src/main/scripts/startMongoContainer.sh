#!/bin/bash

#Start an empty non-persistent mongodb container
docker run -ti --rm -p 27017:27017 mongo:4.0