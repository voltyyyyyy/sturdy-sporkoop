#!/usr/bin/env bash
set -euo pipefail
project_root=$(cd "$(dirname "$0")/../.." && pwd)
test_output=$(mktemp -d)
trap 'rm -rf "$test_output"' EXIT

mkdir -p "$test_output/classes"
find "$project_root/src/main/java" -name '*.java' > "$test_output/sources.txt"
printf '%s\n' "$project_root/tests/stage3/Stage3BehaviorTest.java" >> "$test_output/sources.txt"
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d "$test_output/classes" "@$test_output/sources.txt"
java -cp "$test_output/classes" restaurant.domain.Stage3BehaviorTest
