#!/bin/bash

docker run -it --network="host" --rm -p 3000:3000 grafana/grafana


#8022


#{__name__=~"application:ft_.*_invocations_total"}