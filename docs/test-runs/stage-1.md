# Stage 1 test notes

## Environment

- Java 17 or newer. The sources compile with `javac --release 17` and use only the standard library.
- Verified with OpenJDK 24.0.2 on Windows 11 (PowerShell 7).
- Build with `.\build.ps1`; the `out/` directory is not committed.

## Reproducible runs

The game prints its seed at start-up. Dish choices are drawn from a `Random` seeded with that value, so a run is reproducible from the seed plus the listed key presses.

### Victory - seed 526

```powershell
.\build.ps1
Get-Content runs\victory.txt | java -cp out restaurant.app.Main --seed 526
```

- Input: [`runs/victory.txt`](../../runs/victory.txt) (65 lines, 18 turns).
- Full transcript: [`stage-1-victory.txt`](stage-1-victory.txt).
- Seed 526 makes all six customers choose Pasta, the two-unit dish, so this is the tightest kitchen case.

Expected result:

```text
RESULT: VICTORY
  Paid customers: 4 (target 4)
  Revenue: $72.00 (target $45.00)
  Average paid satisfaction: 66.0 (target 60.0)
  Served: 4    Unhappy departures: 0    Turns used: 18
```

The run demonstrates the required timing:

| Event | Turn |
| --- | --- |
| Order #1 READY | 3 |
| Order #1 served | 4 |
| Customer 1 ready to pay | 5 |
| Customer 1 pays | 6 |
| Customer 2 pays | 10 |
| Customer 3 pays | 14 |
| Customer 4 pays | 17 |
| Game ends | 18 |

The Waiter waits on turns 3, 10, 14 and 18 because no listed task has a legal target. The Chef works on an order the Waiter took in the same turn (turns 2, 6, 9, 13 and 17). The turn-16 customer never pays, which is intended.

### Defeat - seed 42

```powershell
Get-Content runs\defeat.txt | java -cp out restaurant.app.Main --seed 42
```

- Input: [`runs/defeat.txt`](../../runs/defeat.txt) (Waiter waits and Chef waits every turn).
- Full transcript: [`stage-1-defeat.txt`](stage-1-defeat.txt).

Expected result:

```text
RESULT: DEFEAT
  Paid customers: 0 (target 4)
  Revenue: $0.00 (target $45.00)
  Average paid satisfaction: N/A (target 60.0)
  Served: 0    Unhappy departures: 2    Turns used: 18
```

Two customers reach 0 satisfaction and leave. Revenue stays at $0.00 because no unpaid order is ever billed. The average shows `N/A` before any payment.

## Domain checks

A temporary harness exercised the domain directly; it is not part of the game. Its output is in [`stage-1-domain-checks.txt`](stage-1-domain-checks.txt).

| Check | Result |
| --- | --- |
| Taking a second order for the same customer | rejected |
| Serving an order still being prepared | rejected |
| Serving an order that became READY this turn | rejected |
| Order becomes READY after the last preparation unit | passed |
| Order can be served on a later turn | passed |
| No eating transition on the turn of serving | passed |
| READY_TO_PAY after one full turn of eating | passed |
| Cannot pay on the turn the customer becomes ready | passed |
| Pays on the next turn | passed |
| Table freed after payment | passed |
| Satisfaction reaches 0 | passed |
| Unfinished order cancelled on abandonment | passed |
| Table freed on abandonment | passed |
| Unhappy departure counted | passed |

The timing rules are therefore enforced by turn stamps (`turnReady`, `turnServed`, `readyToPayTurn`) and not by the order in which the UI happens to call methods.

## Invalid-choice checks

These were checked manually; each attempt explains the problem and re-asks without consuming the staff action.

1. A non-numeric or out-of-range menu option, for example `9` at `Choose 1-4`:

   ```text
   Choose 1-4: Enter a whole number from 1 to 4.
   ```

2. Serve when no order is READY:

   ```text
   Choose 1-4: No order is READY yet.
   ```

3. Seat at an occupied table (domain rejection):

   ```text
   Table id: Table 1 is already occupied.
   ```

   The Waiter prompt returns and the same Waiter task can be used again.

4. Chef prepare with nothing in the kitchen:

   ```text
   Choose 1-2: No order is waiting in the kitchen.
   ```

## Notes

- The game always plays all 18 turns; there is no early ending.
- Money is stored as integer cents and displayed with two decimals.
- The average satisfaction is the average of paid customers only, recorded when each customer pays.
- The seed and the fixed base setting are printed at start-up for reproducibility.
- Stage 1 contains no VIP, Critic, Host, ComboMeal or Happy Hour code.
