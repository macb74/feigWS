#!/bin/sh

export JAVA_HOME=/opt/jdk1.8.0_144/

MVN=/opt/maven/bin/mvn
$MVN package

DATE=`date +%Y%m%d`
DEP=`$MVN dependency:list > dependency.list`
PACKAGE=`cat dependency.list | sed -e '/Building/!d' | sed -r 's/.*Building (.*)$/\1/g' | sed -e 's/ /-/g'`
APP=`cat dependency.list | sed -e '/Building/!d' | sed -r 's/.*Building (.*) .*/\1/g' | sed -e 's/ /-/g'`
OBIDISC4J=`cat dependency.list | sed -e '/OBIDISC4J/!d' | sed -r 's/.*OBIDISC4J:jar:(.*):.*$/\1/g'`

echo "Package:   ${PACKAGE}-${DATE}" > version.info
echo "OBIDISC4J: ${OBIDISC4J}" >> version.info


mkdir ${PACKAGE}-${DATE}
cp target/${PACKAGE}.jar ${PACKAGE}-${DATE}/${APP}.jar
cp application.properties ${PACKAGE}-${DATE}
cp version.info ${PACKAGE}-${DATE}
cp lib/ID*.zip ${PACKAGE}-${DATE}

zip ${PACKAGE}-${DATE}.zip ${PACKAGE}-${DATE}/*

rm dependency.list
rm -r ${PACKAGE}-${DATE}