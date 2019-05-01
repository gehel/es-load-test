#!/usr/bin/env bash

i=$(hostname | tr -cd '0-9')

if [[ ${i} -lt 2029 ]]; then
    j=$(expr ${i} - 1008)
else
    j=$(expr ${i} - 1007)
fi
src="elastic${j}.eqiad.wmnet"
sudo iptables -A INPUT -p tcp -s ${src} --dport 9999 -j ACCEPT
nc -l -p 9999 | pigz -c -d | pv > requests.gor
sudo iptables -D INPUT -p tcp -s ${src} --dport 9999 -j ACCEPT
