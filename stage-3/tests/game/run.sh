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
    printf '%s\n' 9 0 9 1 2 1 1 1
    printf '%s\n' 0 0 1 1
    printf '%s\n' 0 3 1 0
    printf '%s\n' 0 2 2 1 2
    printf '%s\n' 0 3 2 0
    printf '%s\n' 0 0 0
    printf '%s\n' 0 2 3 1 3 0 0 1 3 0 3 3 0
    printf '%s\n' 0 2 4 1 4 0 0 1 4 0 0 1 4 0 3 4 0
    printf '%s\n' 0 2 5 1 5 0 0 1 5 0 3 5 0
    printf '%s\n' 0 0 0 0 0 0
} > "$test_output/victory.input"

java -cp "$test_output/classes" restaurant.control.GameMgr \
    < "$test_output/victory.input" > "$test_output/victory.log"

grep -Fq 'Invalid choice: Choose 0 or 1.' "$test_output/victory.log"
grep -Fq 'Invalid choice: Choose 0, 1, 2 or 3.' "$test_output/victory.log"
grep -Fq 'Invalid choice: Nobody is waiting to be seated.' "$test_output/victory.log"

grep -Fq 'Host seated customer #1 at table 1.' "$test_output/victory.log"
grep -Fq 'Host seated customer #2 at table 2.' "$test_output/victory.log"
grep -Fq 'Host seated customer #4 at table 2.' "$test_output/victory.log"

grep -Fq 'Regular customer #1 ordered Salad + Burger (2 preparation units, $21.60).' "$test_output/victory.log"
grep -Fq 'Critic customer #4 ordered Pasta + Burger (3 preparation units, $29.70).' "$test_output/victory.log"
grep -Fq 'VIP customer #2 ordered Salad (1 preparation unit, $8.10).' "$test_output/victory.log"

grep -Fq 'Cashier collected payment from customer #4 [Critic] ($29.70).' "$test_output/victory.log"
grep -Fq 'Discount applied: saved $0.90 off the listed $9.00.' "$test_output/victory.log"

grep -Fq '=== VICTORY ===' "$test_output/victory.log"
grep -Fq 'Paid customers: 5' "$test_output/victory.log"
grep -Fq 'Unhappy departures: 0' "$test_output/victory.log"
grep -Fq 'Revenue: $102.60' "$test_output/victory.log"
grep -Fq 'Average paid-customer satisfaction: 80.6' "$test_output/victory.log"
grep -Fq 'Turns used: 18' "$test_output/victory.log"
grep -Fq 'Unpaid bills: $0.00 | Lost to walkouts: $0.00' "$test_output/victory.log"

awk '
    BEGIN { expected[1]=1; expected[2]=4; expected[3]=7; expected[4]=10; expected[5]=13; expected[6]=16 }
    /^=== Turn / { turn=$3+0 }
    / arrived\.$/ { n++; if (turn != expected[n]) exit 1 }
    END { if (n != 6) exit 1 }
' "$test_output/victory.log"

{ for _ in $(seq 1 18); do printf '0\n0\n0\n'; done; } > "$test_output/defeat.input"
java -cp "$test_output/classes" restaurant.control.GameMgr \
    < "$test_output/defeat.input" > "$test_output/defeat.log"
grep -Fq '=== DEFEAT ===' "$test_output/defeat.log"
grep -Fq 'Paid customers: 0' "$test_output/defeat.log"
grep -Fq 'Average paid-customer satisfaction: N/A' "$test_output/defeat.log"

echo "Passed full-game victory, defeat, host-seating, combo and Happy Hour input checks."
