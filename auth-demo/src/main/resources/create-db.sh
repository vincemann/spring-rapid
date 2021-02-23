#!/bin/bash
homeDir=`cd ~ && pwd`
rapidDir=$homeDir/.rapid
mkdir -p $rapidDir/mysql/data
mkdir -p $rapidDir/mysql/conf.d
sudo docker run --detach --name=lemon-mysql --env="MYSQL_ROOT_PASSWORD=mypassword" --env="MYSQL_DATABASE=lemonDb" --publish 6603:3306 --volume=$rapidDir/mysql/conf.d:/etc/mysql/conf.d --volume=$rapidDir/mysql/data:/var/lib/mysql mysql:8.0

