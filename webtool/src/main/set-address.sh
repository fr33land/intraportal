#!/bin/bash
# this script will change IP with basic input on OpenSUSE using networkmanager

# Default variables
#$1 = SERVER_IF
#$2 = SERVER_IP_SUBNET
#$3 = SERVER_GATEWAY
#$4 = SERVER_DNS1
#$5 = SERVER_DNS2 (not used but reserved)

nmcli con mod $1 ipv4.method manual ipv4.address $2 ipv4.gateway $3 ipv4.dns $4
nmcli con down id $1
nmcli con up id $1

# Restart Nginx to make sure that network stack will be updated.
systemctl restart nginx