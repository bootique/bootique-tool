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
NAME=$(basename $(find . -type f -name 'bq-*.zip'))
echo ${NAME}
mkdir temp-homebrew

CHECKSUM=$(echo "$(shasum -a 256 target/${NAME})" | awk '{print $1;}')
echo ${CHECKSUM}

cd temp-homebrew
git clone https://github.com/bootique-tools/homebrew-repo
cd homebrew-repo
git remote add origin-deploy https://${GITHUB_TOKEN}@github.com/bootique-tools/homebrew-repo.git
cd Formula

cat bq.rb

PREV_NAME=$(grep -o 'file_path=.*$' bq.rb | cut -c11-)
PREV_NAME=${PREV_NAME%?}

PREV_CHECKSUM_FROM_FILE=$(grep -o 'sha256.*$' bq.rb | cut -c9-)
PREV_CHECKSUM=${PREV_CHECKSUM_FROM_FILE%?}

echo ${PREV_NAME}
echo ${PREV_CHECKSUM}

sed -i '.bak' "s/$PREV_NAME/$NAME/g" bq.rb

sed -i '.bak' "s/$PREV_CHECKSUM/$CHECKSUM/g" bq.rb

cat bq.rb

git add .
git commit -m "Update formula version"
git push origin-deploy master