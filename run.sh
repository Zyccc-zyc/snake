#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build
javac -encoding UTF-8 -d build src/SnakeGame.java
java -cp build SnakeGame
