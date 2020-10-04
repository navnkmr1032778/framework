#!/bin/bash
echo "OOOO"
docker-compose -f /Users/guestuser/Downloads/framework/resources/docker/docker-compose.yaml up --scale chrome=1 >> output.txt