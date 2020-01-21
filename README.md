<!--
  Licensed to ObjectStyle LLC under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ObjectStyle LLC licenses
  this file to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

[![Build Status](https://travis-ci.org/bootique/bootique-tool.svg)](https://travis-ci.org/bootique/bootique-tool)

# `bq` : Bootique CLI tool

`bq` is an interactive tool to create and manage Bootique projects.

## Documentation

https://bootique.io/docs/latest/bootique-tool-docs/

## How to Build

Follow documentation link above for instructions on how to get a platform-specific `bq` binary. To build `bq` from source, do the following:

* [Download](https://github.com/graalvm/graalvm-ce-builds/releases) and install GraalVM
* Checkout and build the repo:
```
git clone git@github.com:bootique/bootique-tool.git
cd bootique-tool
export JAVA_HOME=<GraalVM Home>
mvn package -Pnative-image
```
  

