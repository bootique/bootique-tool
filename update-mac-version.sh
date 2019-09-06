#!/usr/bin/env bash

cd target

NAME=$(basename $(find . -type f -name 'bq-*.jar'))
VERSION=$(echo "${NAME%.*}" | cut -d'-' -f 2)

cd ../

sed -i '.bak' "s/template_version/$VERSION/g" deploy-mac-config.json
sed -i '.bak' "s/template_tag/$VERSION/g" deploy-mac-config.json