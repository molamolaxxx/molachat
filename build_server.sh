#!/bin/bash
if [ ! -d "target" ]; then
  mkdir target
fi
cd ./server
mvn package;

cp ./target/molachat-0.0.1-SNAPSHOT.jar ../target/molachat.jar;
cp ../ext/server_start.sh ../target/server_start.sh;
cp ../ext/server_shutdown.sh ../target/server_shutdown.sh;