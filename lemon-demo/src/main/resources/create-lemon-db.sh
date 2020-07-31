#!/bin/bash
homeDir=`cd ~ && pwd`
rapidDir=$homeDir/.rapid
mkdir -p $rapidDir/mysql/data
mkdir -p $rapidDir/mysql/conf.d
cp db/init-database.sql $rapidDir/
sudo docker run --detach --name=lemon-mysql --env="MYSQL_ROOT_PASSWORD=mypassword" --env="MYSQL_DATABASE=lemonDb" --publish 3306:3306 --volume=$rapidDir/mysql/conf.d:/etc/mysql/conf.d --volume=$rapidDir/mysql/data:/var/lib/mysql --volume=$rapidDir:/docker-entrypoint-initdb.d mysql:8.0

