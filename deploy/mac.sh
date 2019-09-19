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
curl -OL https://github.com/oracle/graal/releases/download/vm-19.1.1/graalvm-ce-darwin-amd64-19.1.1.tar.gz
tar zxf graalvm-ce-darwin-amd64-19.1.1.tar.gz
sudo mv graalvm-ce-19.1.1 /Library/Java/JavaVirtualMachines
/usr/libexec/java_home -v 1.8
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-19.1.1/Contents/Home
export PATH=${JAVA_HOME}/bin:$PATH
${JAVA_HOME}/bin/gu install native-image

mvn package -Pnative-image,assembly
