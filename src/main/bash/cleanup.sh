#!/usr/bin/env bash

for i in {17..52}; do
    if [[ ${i} == 21 ]]; then
        continue
    fi
    dest="elastic10${i}.eqiad.wmnet"
    echo "cleaning up ${dest}"
    ssh ${dest} rm gor send.sh requests.gor
done

for i in {25..54}; do
    dest="elastic20${i}.codfw.wmnet"
    echo "cleaning up ${dest}"
    ssh ${dest} rm gor receive.sh requests.gor rewrite-morelike.py
done
