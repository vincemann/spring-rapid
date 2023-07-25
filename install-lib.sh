#!/bin/bash
./mvnw -pl \!core-demo,\!auth-demo,\!acl-demo clean install -DskipTests
