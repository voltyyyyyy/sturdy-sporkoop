#!/usr/bin/env bash
set -euo pipefail
project_root=$(cd "$(dirname "$0")/../.." && pwd)
test_output=$(mktemp -d)
trap 'rm -rf "$test_output"' EXIT

sources=()
while IFS= read -r -d '' source; do
    sources+=("$source")
done < <(find "$project_root/src/main/java" -type f -name '*.java' -print0)
sources+=("$project_root/tests/stage3/Stage3BehaviorTest.java")

mkdir -p "$test_output/classes"
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d "$test_output/classes" \
    "${sources[@]}"
java -cp "$test_output/classes" restaurant.domain.Stage3BehaviorTest
