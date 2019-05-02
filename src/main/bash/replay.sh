#!/usr/bin/env bash

/home/gehel/gor --input-file /home/gehel/requests.gor'|'$1'%' --output-http http://search.svc.codfw.wmnet:9200 --stats --output-http-stats --output-http-workers 40
