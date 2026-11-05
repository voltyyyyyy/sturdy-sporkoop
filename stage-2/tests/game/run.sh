#!/usr/bin/env bash
set -euo pipefail
project_root=$(cd "$(dirname "$0")/../.." && pwd)
test_output=$(mktemp -d)
trap 'rm -rf "$test_output"' EXIT

sources=()
while IFS= read -r -d '' source; do
    sources+=("$source")
done < <(find "$project_root/src/main/java" -type f -name '*.java' -print0)

mkdir -p "$test_output/classes"
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d "$test_output/classes" "${sources[@]}"

{
    printf '%s\n' 9 1 9 1 1 0
    printf '%s\n' 2 1 1 1
    printf '%s\n' 3 1 0
    printf '%s\n' 1 2 0
    printf '%s\n' 2 2 1 2 3 2 0 1 1 0 2 3 1 3 3 3 0
    printf '%s\n' 1 2 0 2 4 1 4
    printf '%s\n' 0 1 4 3 4 0
    printf '%s\n' 1 1 0 2 5 1 5 3 5 0
    printf '%s\n' 1 2 0 2 6 1 6
} > "$test_output/victory.input"

java -cp "$test_output/classes" restaurant.control.GameMgr \
    < "$test_output/victory.input" > "$test_output/victory.log"

grep -Fq 'Invalid choice: Choose 0, 1, 2 or 3.' "$test_output/victory.log"
grep -Fq 'Invalid choice: No table with table id 9' "$test_output/victory.log"

grep -Fq 'Regular customer #1 ordered Salad (1 preparation unit, $9.00).' "$test_output/victory.log"
grep -Fq 'VIP customer #2 ordered Salad (1 preparation unit, $8.10).' "$test_output/victory.log"
grep -Fq 'Critic customer #4 ordered Pasta (2 preparation units, $18.00).' "$test_output/victory.log"

grep -Fq 'Cashier collected payment from customer #2 [VIP] ($8.10).' "$test_output/victory.log"
grep -Fq 'Discount applied: saved $0.90 off the listed $9.00.' "$test_output/victory.log"
grep -Fq 'Customer #2: Salad $8.10' "$test_output/victory.log"

grep -Fq '=== VICTORY ===' "$test_output/victory.log"
grep -Fq 'Paid customers: 5' "$test_output/victory.log"
grep -Fq 'Unhappy departures: 0' "$test_output/victory.log"
grep -Fq 'Revenue: $53.10' "$test_output/victory.log"
grep -Fq 'Average paid-customer satisfaction: 79.6' "$test_output/victory.log"
grep -Fq 'Turns used: 18' "$test_output/victory.log"
grep -Fq 'Unpaid bills: $9.00 | Lost to walkouts: $0.00' "$test_output/victory.log"
if grep -Fq 'Cashier collected payment from customer #6' "$test_output/victory.log"; then
    echo 'The turn-16 arrival must not pay by turn 18.' >&2
    exit 1
fi

awk '
    BEGIN { expected[1]=1; expected[2]=4; expected[3]=7; expected[4]=10; expected[5]=13; expected[6]=16 }
    /^=== Turn / { turn=$3+0 }
    / arrived\.$/ { n++; if (turn != expected[n]) exit 1 }
    END { if (n != 6) exit 1 }
' "$test_output/victory.log"

awk '
    BEGIN { expected[1]=5; expected[2]=8; expected[3]=11; expected[4]=15; expected[5]=18 }
    /^=== Turn / { turn=$3+0 }
    /Cashier collected payment/ { n++; if (turn != expected[n]) exit 1 }
    END { if (n != 5) exit 1 }
' "$test_output/victory.log"

awk '
    /^=== Turn / { turn=$3+0 }
    /Chef advanced Salad for customer #1/ { prepared=turn }
    /Waiter served customer #1/ { served=turn }
    END { if (prepared != 2 || served != 3) exit 1 }
' "$test_output/victory.log"

grep -Fq 'Chef advanced Pasta for customer #4 (1/2 units; PLACED).' "$test_output/victory.log"
grep -Fq 'Chef advanced Pasta for customer #4 (2/2 units; READY).' "$test_output/victory.log"

{
    for ((turn=1; turn<=18; turn++)); do
        printf '0\n0\n'
    done
} > "$test_output/defeat.input"
java -cp "$test_output/classes" restaurant.control.GameMgr \
    < "$test_output/defeat.input" > "$test_output/defeat.log"
grep -Fq '=== Turn 18 / 18 ===' "$test_output/defeat.log"
grep -Fq '=== DEFEAT ===' "$test_output/defeat.log"
grep -Fq 'Paid customers: 0' "$test_output/defeat.log"
grep -Fq 'Unhappy departures: 2' "$test_output/defeat.log"
grep -Fq 'Turns used: 18' "$test_output/defeat.log"
grep -Fxq '  (none)' "$test_output/defeat.log"

echo 'Passed full-game victory, defeat, turn-order, arrival, timing and input-retry checks.'
