#!/usr/bin/env bash
set -euo pipefail
project_root=$(cd "$(dirname "$0")/../.." && pwd)
test_output=$(mktemp -d)
trap 'rm -rf "$test_output"' EXIT
domain="$project_root/src/main/java/restaurant/domain"
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d "$test_output" \
    "$domain"/{Restaurant,Table,WaitingQueue,GameResult,Customer,RegularCustomer,CustomerState,Menu,MenuItem,OrderStatus}.java \
    "$project_root"/tests/restaurant/fixtures/restaurant/domain/*.java
java -cp "$test_output" restaurant.domain.RestaurantTest
