#!/bin/bash
./mvnw -pl core-demo clean test
./mvnw -pl acl-demo clean test
cd ./auth-demo/
./test.sh
cd ..