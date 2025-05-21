#!/bin/bash

BIN_DIR="bin"
JAR_NAME="RushHourSolver.jar"
MAIN_CLASS="RushHourMain"
SRC_DIR="src"
PROJECT_ROOT=$(pwd)

error_exit() {
		exit 1
}

if [ -d "$BIN_DIR" ]; then
		rm -rf "$BIN_DIR"
fi
mkdir -p "$BIN_DIR"

javac -d bin src/*.java src/controller/heuristic/*.java src/controller/solver/*.java src/controller/*.java src/model/core/*.java src/model/*.java src/utils/*.java src/view/gui/*.java

jar cfe bin/RushHourSolver.jar RushHourMain -C bin .

cd "$BIN_DIR"
find . -mindepth 1 -maxdepth 1 -not -name "$JAR_NAME" -exec rm -rf {} +
cd "$PROJECT_ROOT"

echo "File JAR berhasil dibuat di: $PROJECT_ROOT/$BIN_DIR/$JAR_NAME"