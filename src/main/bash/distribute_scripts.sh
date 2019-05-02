#!/usr/bin/env bash

for i in {17..52}; do
    if [[ ${i} == 21 ]]; then
        continue
    fi
    dest="elastic10${i}.eqiad.wmnet"
    echo "distributing scripts to ${dest}"
    scp ~/Downloads/goreplay/gor ${dest}:~
    scp target/es-load-test-1.0.0-SNAPSHOT-shaded.jar ${dest}:~
    scp src/main/bash/send.sh ${dest}:~
done

for i in {25..54}; do
    dest="elastic20${i}.codfw.wmnet"
    echo "distributing scripts to ${dest}"
    scp ~/Downloads/goreplay/gor ${dest}:~
    scp src/main/python/rewrite-morelike.py ${dest}:~
    scp src/main/bash/receive.sh ${dest}:~
    scp src/main/bash/replay.sh ${dest}:~
done
