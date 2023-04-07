#!/bin/bash
./mvnw -pl core-demo clean test
./mvnw -pl acl-demo clean test
./auth-demo/test.sh