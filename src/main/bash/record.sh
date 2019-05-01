#!/usr/bin/env bash

ssh cumin1001.eqiad.wmnet \
        "sudo cumin --force 'A:elastic-eqiad' 'rm /home/gehel/requests.gor && timeout 1800 /home/gehel/gor --input-raw 0.0.0.0:9200 --http-disallow-url /_bulk --http-allow-method GET --http-allow-method POST --http-allow-method HEAD --output-file /home/gehel/requests.gor --output-file-append'"
