#!/bin/bash
homeDir=`cd ~ && pwd`
rapidDir=$homeDir/.rapid/acl-db
mkdir -p "$rapidDir"
mkdir -p $rapidDir/mysql/data
mkdir -p $rapidDir/mysql/conf.d
sudo docker run --detach --name=rapid-acl-mysql --env="MYSQL_ROOT_PASSWORD=mypassword" --env="MYSQL_DATABASE=aclDemoDb" --publish 6605:3306 --volume=$rapidDir/mysql/conf.d:/etc/mysql/conf.d --volume=$rapidDir/mysql/data:/var/lib/mysql mysql:8.0

