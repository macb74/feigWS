#!/bin/sh

export JAVA_HOME=/opt/java
MVN=/opt/maven/bin/mvn

#$MVN deploy:deploy-file -Dfile=./lib/ID_ISC.SDK.Java-V4.8.1/sub-prj/lib/fedm-java/build/dist/OBIDISC4J.jar -DgroupId=de.feig -DartifactId=OBIDISC4J -Dversion=4.8.1 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

$MVN deploy:deploy-file -Dfile=./lib/ID_ISC.SDK.Java-V5.6.0/sub-prj/lib/fedm-java/build/dist/OBIDISC4J.jar -DgroupId=de.feig -DartifactId=OBIDISC4J -Dversion=5.6.0 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

$MVN deploy:deploy-file -Dfile=./lib/ID_ISC.SDK.Raspi-V5.6.0/sub-prj/lib/fedm-java/build/dist/OBIDISC4J.jar -DgroupId=de.feig -DartifactId=OBIDISC4J -Dversion=rpi5.6.0 -Dpackaging=jar -Durl=file:./maven-repository/ -DrepositoryId=maven-repository -DupdateReleaseInfo=true

