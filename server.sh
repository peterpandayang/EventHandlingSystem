#!/usr/bin/env bash

java -cp ./target/sift-1.0-SNAPSHOT.jar:target/classes/json-simple-1.1.1.jar:target/classes/protobuf-java-3.4.0.jar edu.usfca.cs.sift.server.Server
