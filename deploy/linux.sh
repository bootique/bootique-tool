#!/usr/bin/env bash
# Licensed to ObjectStyle LLC under one or more
# contributor license agreements. See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ObjectStyle LLC licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
curl -OL https://github.com/oracle/graal/releases/download/vm-19.1.1/graalvm-ce-linux-amd64-19.1.1.tar.gz
tar zxf graalvm-ce-linux-amd64-19.1.1.tar.gz
sudo mv graalvm-ce-19.1.1 /usr/lib/jvm/
export JAVA_HOME=/usr/lib/jvm/graalvm-ce-19.1.1
export PATH=${JAVA_HOME}/bin:$PATH
${JAVA_HOME}/bin/gu install native-image
java -version

mvn install -Pnative-image

NAME=$(basename $(find . -type f -name 'bq-*.jar'))
VERSION=$(echo "${NAME%.*}" | cut -d'-' -f 2)

mkdir bq-deb
cd bq-deb
mv ../target/bq .

PACK_NAME=$(ls)
chmod +x ${PACK_NAME}
mkdir packageroot
mkdir packageroot/DEBIAN
touch packageroot/DEBIAN/control

echo "Package: $PACK_NAME
Version: $VERSION
Architecture: amd64
Maintainer: Bootique
Description: Bq tool
" > packageroot/DEBIAN/control

cat packageroot/DEBIAN/control

mkdir -p packageroot/usr/bin
cp ${PACK_NAME} packageroot/usr/bin/
dpkg-deb -b packageroot ${PACK_NAME}-${VERSION}.deb

sudo dpkg -i ./${PACK_NAME}-${VERSION}.deb
sudo apt-get install -f

DEB_PACK=$(find . -type f -name 'bq-*.deb')
echo ${DEB_PACK}
cp ${DEB_PACK} ../target/

cd ../target

# convert to rpm
sudo apt-get update
sudo apt-get install rpm
sudo apt-get install ruby ruby-dev rubygems build-essential
gem install --no-ri --no-rdoc fpm

fpm -t rpm -s deb ${PACK_NAME}-${VERSION}.deb

pwd
ls



