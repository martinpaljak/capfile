# capfile · [![Build Status](https://github.com/martinpaljak/capfile/workflows/Continuous%20Integration/badge.svg?branch=master)](https://github.com/martinpaljak/capfile/actions) [![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/martinpaljak/capfile/blob/master/LICENSE)
[![Release](https://img.shields.io/github/release/martinpaljak/capfile/all.svg)](https://github.com/martinpaljak/capfile/releases) [![Maven version](https://img.shields.io/maven-metadata/v?label=javacard.pro%20version&metadataUrl=https%3A%2F%2Fjavacard.pro%2Fmaven%2Fcom%2Fgithub%2Fmartinpaljak%2Fcapfile%2Fmaven-metadata.xml)](https://gist.github.com/martinpaljak/c77d11d671260e24eef6c39123345cae) [![Maven Central](https://img.shields.io/maven-central/v/com.github.martinpaljak/capfile)](https://mvnrepository.com/artifact/com.github.martinpaljak/capfile) 

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
