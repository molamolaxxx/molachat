#!/bin/bash
if [ -d "target" ]; then
  rm -rf ./target
fi
cd ./app
rm -rf ./node_modules
rm -rf ./platforms
rm -rf ./plugins
echo "clear app success"
cd ../server
mvn clean
echo "clear server success"