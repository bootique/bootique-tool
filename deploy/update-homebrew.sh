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

CHECKSUM=$(echo "$(shasum -a 256 bootique-tool/target/${NAME})" | awk '{print $1;}')
echo "New checksum: ${CHECKSUM}"

mkdir temp-homebrew && cd temp-homebrew || exit
git clone https://github.com/bootique-tools/homebrew-repo
cd homebrew-repo || exit
git remote add origin-deploy https://${GITHUB_TOKEN}@github.com/bootique-tools/homebrew-repo.git
cd Formula || exit

echo "====== Existing homebrew recipe: ======"
cat bq.rb

PREV_NAME=$(grep -o 'file_path=.*$' bq.rb | cut -c11-)
PREV_NAME=${PREV_NAME%?}

PREV_CHECKSUM_FROM_FILE=$(grep -o 'sha256.*$' bq.rb | cut -c9-)
PREV_CHECKSUM=${PREV_CHECKSUM_FROM_FILE%?}

echo "Prev name: ${PREV_NAME}"
echo "Prev checksum: ${PREV_CHECKSUM}"

sed -i '.bak' "s/$PREV_NAME/$NAME/g" bq.rb
sed -i '.bak' "s/$PREV_CHECKSUM/$CHECKSUM/g" bq.rb

echo "====== Updated homebrew recipe: ======"
cat bq.rb

git add .
git commit -m "Update formula version"
git push origin-deploy master