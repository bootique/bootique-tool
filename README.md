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

[![verify](https://github.com/bootique/bootique-tool/actions/workflows/verify.yml/badge.svg)](https://github.com/bootique/bootique-tool/actions/workflows/verify.yml)

# `bq` : Bootique CLI tool

`bq` is an interactive tool to create and manage Bootique projects.

## Documentation

https://bootique.io/docs/latest/bootique-tool-docs/

## How to Build


### MacOS / Linux

Follow documentation link above for instructions on how to get a platform-specific `bq` binary. To build `bq` from
source, do the following:

* [Download](https://github.com/graalvm/graalvm-ce-builds/releases) and unpack GraalVM 8. (As of this writing builds
don't yet work with GraalVM 11)
* If you are on MacOS, make sure GraalVM is allowed to run:
```
 xattr -d com.apple.quarantine /path/to/graalvm-ce-java8-X.X.X/
```

* Checkout and build the repo:
```
git clone git@github.com:bootique/bootique-tool.git
cd bootique-tool
export JAVA_HOME=<GraalVM Home>
mvn package -Pnative-image
```
The binary is created at `bootique-tool/target/bq`
  
### Windows build

Windows build is a little trickier than other platforms. 
Mainly this is because `native-image` support is not perfect on Windows so there is a hope that this will improve over time.

There are different actions required for different versions of GraalVM.

#### Java 8
For Java 8 you need to install Windows SDK 7.1, the easiest way to do this is via `Chocolotey`:

```bash
choco install windows-sdk-7.1 kb2519277
``` 
From the cmd prompt, activate the sdk-7.1 environment:

```bash
call "C:\Program Files\Microsoft SDKs\Windows\v7.1\Bin\SetEnv.cmd"
```

Now you can start build as usual (note, that you should use same cmd prompt as for command above).

```bash
mvn package -Pnative-image -DskipTests
```

#### Java 11
  
For Java 11 versions of GraalVM you should use newer dev tools:

```bash
choco install visualstudio2017-workload-vctools
``` 

And then call:
```bash
call "C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\VC\Auxiliary\Build\vcvars64.bat"
```

Build is the same:
```bash
mvn package -Pnative-image -DskipTests
```

Other problems with GraalVM on Windows:

* there could be a problem installing GraalVM not on a "C:" drive;
* staring from version 20.0.0 you should use `gu install native-image` to install `native-image` binary as it no longer bundled by default;
* for GraalVM 20.0.0 and earlier for Java 11 you may need to compile `native-image` into a binary executable (this should be done
  from the command prompt with initialized dev tools environment, as with `bq` tool build):
    ```bash
    cd %GRAALVM_HOME%\bin
    native-image.cmd -jar ..\lib\graalvm\svm-driver.jar
    ``` 
  
## TODO: Windows binary distribution

To publish on [chocolatey community portal](https://chocolatey.org/community) we need the following:

- [x] check win binary actually works
- [x] add app icon, see https://github.com/chocolatey/choco/wiki/CreatePackages#package-icon-guidelines
- [x] check nuspec file, see https://github.com/chocolatey/package-validator/wiki
- [x] add to the final bundle:
    - [x] license text 
    - [x] verification instructions 
    - [x] app icon 
- [ ] check https://github.com/chocolatey/package-verifier/wiki 
- [ ] enable deployment in AppVeyor



