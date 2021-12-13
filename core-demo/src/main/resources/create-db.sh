#!/bin/bash
homeDir=`cd ~ && pwd`
rapidDir=$homeDir/.rapid/core-db
mkdir -p "$rapidDir"
mkdir -p $rapidDir/mysql/data
mkdir -p $rapidDir/mysql/conf.d
sudo docker run --detach --name=rapid-core-mysql --env="MYSQL_ROOT_PASSWORD=mypassword" --env="MYSQL_DATABASE=coreDemoDb" --publish 6603:3306 --volume=$rapidDir/mysql/conf.d:/etc/mysql/conf.d --volume=$rapidDir/mysql/data:/var/lib/mysql mysql:8.0

