#!/usr/bin/env bash

i=$(hostname | tr -cd '0-9')

if [[ ${i} -lt 1021 ]]; then
    j=$(expr ${i} + 1008)
else
    j=$(expr ${i} + 1007)
fi
if [[ ${j} -gt 2054 ]]; then
    exit
fi

dest="elastic${j}.codfw.wmnet"

echo "sending filtered requests to ${dest}"
cat requests.gor | java -jar es-load-test-*-shaded.jar | pv | pigz -c | nc -w 3 -q 0 ${dest} 9999
