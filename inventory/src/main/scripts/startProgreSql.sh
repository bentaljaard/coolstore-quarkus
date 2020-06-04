#!/bin/bash

#Start an empty non-persistent postgresql container
docker run -ti --rm -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 postgres:13