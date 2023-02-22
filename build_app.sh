#!/bin/bash
if [ ! -d "target" ]; then
  mkdir target
fi
cd ./app
echo "add platform"
cordova platform add android@7.1.2
echo "add plugins"
cordova plugin add cordova-plugin-android-permissions
cordova plugin add cordova-plugin-clipboard
echo "start build"
cp ../ext/GradleBuilder.js ./platforms/android/cordova/lib/builders/GradleBuilder.js
cp ../ext/gradle_ali1 ./platforms/android/build.gradle
cp ../ext/gradle_ali2 ./platforms/android/app/build.gradle
cp ../ext/gradle_ali3 ./platforms/android/CordovaLib/build.gradle
cp ../ext/AndroidManifest.xml ./platforms/android/app/src/main/AndroidManifest.xml
cordova build android
mv ./platforms/android/app/build/outputs/apk/debug/app-debug.apk ../target/molachat.apk