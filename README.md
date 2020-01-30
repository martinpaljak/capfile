# capfile · [![Build
# Status](https://github.com/martinpaljak/capfile/workflows/Release%20CI/badge.svg)](https://github.com/martinpaljak/capfile/actions) [![Coverity Scan](https://scan.coverity.com/projects/16101/badge.svg)](https://scan.coverity.com/projects/martinpaljak-capfile) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.martinpaljak/capfile/badge.svg)](https://mvnrepository.com/artifact/com.github.martinpaljak/capfile) [![Javadocs](https://www.javadoc.io/badge/com.github.martinpaljak/capfile.svg?color=blue)](https://www.javadoc.io/doc/com.github.martinpaljak/capfile) [![Release](	https://img.shields.io/github/release/martinpaljak/capfile/all.svg)](https://github.com/martinpaljak/capfile/releases) [![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/martinpaljak/capfile/blob/master/LICENSE)

> Handle JavaCard CAP files, from command line or Java project

    java -jar capfile.jar <capfile>

## Off-card verification

    java -jar capfile.jar -v <path to JavaCard SDK> [<targetsdkpath>] <capfile> [<expfiles...>]

(SDK-s usable on Unix machines are conveniently available from https://github.com/martinpaljak/oracle_javacard_sdks/). EXP files can be plain EXP files or JAR files containing EXP files. Please use JavaCard 3.0.5u3 as the SDK and verify target SDK.

## DAP signing
Usable with [GlobalPlatformPro](https://github.com/martinpaljak/GlobalPlatformPro). At the moment, only PKCS#1 v1.5 SHA1 signature with 1024 bit RSA key is supported.

    java -jar capfile.jar -s <keyfile.pem> <capfile>

A sample flow would look along the lines of:

```shell
openssl genrsa 1024 > dap.pem                          # generate DAP key
capfile -s dap.pem applet.cap                          # sign CAP with DAP key
gp -domain $SSD_AID -privs DAPVerification --allow-to  # create SSD with DAP
gp -sdaid $SSD_AID -put-key dap.pem -key $SSD_SCP_KEY  # add DAP key to SSD
gp -load applet.cap -to $SSD_AID                       # load signed CAP file to SSD
```
