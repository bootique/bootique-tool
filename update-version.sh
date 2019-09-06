#!/usr/bin/env bash

cd target

NAME=$(basename $(find . -type f -name 'bq-*.jar'))
VERSION=$(echo "${NAME%.*}" | cut -d'-' -f 2)

cd ../

sed -i "s/template_version/$VERSION/g" deploy-deb-config.json
sed -i "s/template_tag/$VERSION/g" deploy-deb-config.json

sed -i "s/template_version/$VERSION/g" deploy-rpm-config.json
sed -i "s/template_tag/$VERSION/g" deploy-rpm-config.json