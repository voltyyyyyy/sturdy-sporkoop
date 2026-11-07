#!/usr/bin/env bash
set -euo pipefail
project_root=$(cd "$(dirname "$0")/../.." && pwd)
test_output=$(mktemp -d)
trap 'rm -rf "$test_output"' EXIT
domain="$project_root/src/main/java/restaurant/domain"
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d "$test_output" \
    "$domain"/{Staff,Waiter,Chef,Cashier,Customer,RegularCustomer,CustomerState,Menu,MenuItem,ComboMeal,HappyHour,OrderStatus}.java \
    "$project_root"/tests/staff/fixtures/restaurant/domain/*.java
java -cp "$test_output" restaurant.domain.StaffActionsTest
