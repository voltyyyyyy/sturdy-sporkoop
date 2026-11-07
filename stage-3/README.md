# Restaurant Rush - Stage 3

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Yang Mingchen, Gangula Karthik, Ochi Yuma, Zhang Qiyun, Chamadol Thongthornpatch (Pluem)

This folder contains the Stage 2 game plus the **completed Stage 3 implementation**: the Host role, combo meals and the Happy Hour pricing policy. It uses no external dependencies.

Stage 3 behaviour is fully connected: the Host seats one queued customer per turn next to the Waiter's task, regular and critic customers order documented combos, the manager may activate Happy Hour once per game (20% off new orders for two turns, +15 satisfaction recovery for waiting customers), and the VIP discount still stacks before Happy Hour when pricing a new order.

The repository root contains Stage 1 and `stage-2/` contains Stage 2; both remain runnable at their tags. All paths below are relative to this folder.

## Stage 3 responsibilities

| Member | Code | Documentation / process |
| --- | --- | --- |
| **Gangula Karthik** | `domain/HappyHour.java` (once-per-game policy: 20% off, two turns, +15 recovery), `Customer.recoverSatisfaction(int)`, `Restaurant` happy-hour ownership (`activateHappyHour`, `recoverWaitingSatisfaction`, `tickHappyHour`), `Waiter.takeOrder` stacking Happy Hour after the customer discount. | Update the Stage 3 class diagram to match final code. |
| **Yang Mingchen** | `domain/ComboMeal.java` (90% of the combined price, HALF_UP; summed preparation units), `domain/Host.java` (`seatNext`), `MenuItem` unsealed for the `ComboMeal` subclass, and `GameMgr` integration (Host seating, the Happy Hour manager choice, recovery and the end-of-turn tick). | Document the combo choice rules and verify the controller still holds no customer-type checks. |
| **Ochi Yuma** | `ConsoleUI`: Host in the staff line, `askHappyHour()`, `showHappyHour(int)` and the remaining-duration line in `showTurnStart`. | Keep game rules out of the UI: every new line only reads domain getters or reports a domain decision. |
| **Chamadol Thongthornpatch (Pluem)** | `tests/stage3/Stage3BehaviorTest.java`: combo pricing and choice rules, Happy Hour stacking/lifecycle/recovery, Host seating and independence, and a scripted Happy Hour game. Update the earlier suites for the combo orders and the new input order. | Record the reproducible victory/defeat inputs and results in the test notes below. |
| **Zhang Qiyun** | Integration support across the four code streams. | Verify the `stage-2` baseline, finish the 2 → 3 transition table and compare link, then tag a completed runnable `stage-3`. Draft the design overview delta, P2 and OCP evidence. |

## Stage 3 rules and UML contracts

| Feature | Rule |
| --- | --- |
| Host | Seats at most one queued customer per turn at the first free table, in addition to the Waiter's task. The Waiter can still seat customers; each role has its own one-task-per-turn flag. |
| ComboMeal | Made of two different existing menu items. Price = 90% of the combined price, rounded to the nearest cent (HALF_UP). Preparation units = the sum of both. Salad + Pasta = ($9 + $18) x 0.9 = $24.30, 3 units. |
| Regular combo rule | Regulars are hungry: they order the two cheapest dishes paired as a combo. Base menu: Salad + Burger = $21.60, 2 units. |
| Critic combo rule | Critics review the most expensive option: the two priciest dishes as a combo when it costs more than any single dish. Base menu: Pasta + Burger = $29.70, 3 units. |
| VIP rule | VIPs keep the single-dish choice (Salad); their 10% discount applies first. |
| Happy Hour | The manager may activate it once per game at the start of a turn. It lasts that turn and the next. Orders taken while it is active get 20% off (after the customer's own discount; price locked when the order is taken). During each active turn, customers still waiting for a table or food recover 15 satisfaction at turn start (maximum 100). It never reprices placed orders or affects customers who have left. VIP + Burger during Happy Hour: $15 x 0.9 x 0.8 = $10.80. |

`HappyHour` is a `«policy»` object owned by `Restaurant`. `ComboMeal` extends `MenuItem` (P2), so `Order`, `Waiter`, `Chef` and `ConsoleUI` treat a combo like any dish. `Host` extends `Staff` and reuses the shared one-action guard. Money stays integer cents; Happy Hour rounds HALF_UP to whole cents. Victory targets are unchanged: four paid customers, at least 4500 cents revenue, average paid satisfaction at least 60. Stage 3 works offline and is independently playable.

## Stage 3 implementation map

| Owner | Implemented in |
| --- | --- |
| Gangula Karthik | `domain/HappyHour.java`, `domain/Customer.java` (`recoverSatisfaction`), `domain/Restaurant.java` (happy-hour ownership, recovery, tick), `domain/Waiter.java` (discount stacking) |
| Yang Mingchen | `domain/ComboMeal.java`, `domain/Host.java`, `domain/MenuItem.java` (unsealed), `domain/RegularCustomer.java` + `domain/CriticCustomer.java` (combo rules), `control/GameMgr.java` (Host seating, Happy Hour choice, recovery and tick) |
| Ochi Yuma | `ui/ConsoleUI.java` (Host in the staff line, `askHappyHour`, `showHappyHour`, remaining-duration display) |
| Pluem | `tests/stage3/Stage3BehaviorTest.java` (all six scenarios), updates to `tests/domain`, `tests/staff` and `tests/stage2` for the combo orders and new input order, and the test notes below |
| Zhang Qiyun | Transition table, compare link and the `stage-3` tag |

Code paths in the table are under `src/main/java/restaurant/` unless starting with `tests/`.

## Structure

- `src/main/java/restaurant/ui/ConsoleUI.java` is the only class that reads from or writes to the console. It formats money for display.
- `src/main/java/restaurant/control/GameMgr.java` owns the 18-turn loop and sequences the domain actions, including the Host seating and the Happy Hour manager choice.
- `src/main/java/restaurant/domain/` contains the customer, menu (with `ComboMeal`), order, restaurant, staff (with `Host`) and the `HappyHour` policy.
- [Stage 1 contracts and test notes](docs/stage-1-contracts.md) records domain responsibilities and verification cases.

## Build and run

Requires JDK 17 or newer.

From the repository root, enter the Stage 3 folder in Windows PowerShell:

```powershell
cd stage-3
.\build.ps1
.\run.ps1
```

Or, starting from the repository root in Bash:

```sh
cd stage-3
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out @out/sources.txt
java -cp out restaurant.control.GameMgr
```

## Tests

The test runners require JDK 17+ and Bash. Run them from `stage-3/` (enter it with `cd stage-3` from the repository root):

```sh
bash tests/domain/run.sh
bash tests/restaurant/run.sh
bash tests/staff/run.sh
bash tests/game/run.sh
bash tests/stage2/run.sh
bash tests/stage3/run.sh
```

The domain, restaurant and staff suites keep their Stage 1/2 coverage (guards, payment rules, one-task-per-turn) with expectations updated for the combo orders. The game suite launches the real application and checks an 18-turn win, an 18-turn loss, scheduled arrivals, Host auto-seating, retryable invalid choices (including the Happy Hour prompt) and the combo order lines. The stage 2 suite keeps the LSP shared-flow scenario with stage 3 combo prices. The stage 3 suite covers combo pricing and choice rules, the Happy Hour lifecycle, VIP-then-Happy-Hour stacking ($15 x 0.9 x 0.8 = $10.80), waiting recovery, Host seating independence and a scripted game that activates Happy Hour on turn 1.

### Stage 3 test notes

Both full games are replayed by `tests/stage2/run.sh` (via `Stage2BehaviorTest.victoryAndDefeat()`) and `tests/game/run.sh`, which feeds the same plans to the real `GameMgr` through stdin.

| Run | Input fixture / replay command | Expected paid count, revenue, average and result | Verified commit |
| --- | --- | --- | --- |
| Victory, including VIP (#2) and Critic (#4) | `tests/game/run.sh` victory block. Stage 3 input order per turn: Happy Hour choice, waiter task (+ id), chef task (+ id). The Host seats every arrival automatically; the plan only orders and serves: T1 `0,2,1,1,1` T2 `0,0,1,1` T3 `0,3,1,0` T4 `0,2,2,1,2` T5 `0,3,2,0` T6 `0,0,0` T7 `0,2,3,1,3` T8 `0,0,1,3` T9 `0,3,3,0` T10 `0,2,4,1,4` T11 `0,0,1,4` T12 `0,0,1,4` T13 `0,3,4,0` T14 `0,2,5,1,5` T15 `0,0,1,5` T16 `0,3,5,0` T17 `0,0,0` T18 `0,0,0` | 5 paid (turns 5, 7, 11, 15, 18), revenue $102.60 (2160+810+2160+2970+2160), average paid satisfaction 80.6 (84/95/84/64/76), VICTORY; no unpaid bills, no walkouts | `stage-3` tag |
| Defeat | `tests/game/run.sh` defeat block: `0,0,0` on every turn (skip Happy Hour, all staff always wait) | 0 paid, revenue $0.00, average N/A, DEFEAT; the first two seated customers walk out at zero satisfaction | `stage-3` tag |
| Happy Hour game | `tests/stage3/run.sh` `happyHourGame()` scenario: activate on turn 1, order the regular combo that turn, VIP orders on turn 4 after it ends | Combo locked at $17.28 ($21.60 x 0.8), VIP Salad back at $8.10, activation message and the "1 turn(s) remaining" display verified | `stage-3` tag |

## Stage 2 → Stage 3 transition

This table records what changed between the `stage-2` and `stage-3` tags.

| Files | Change and reason |
| --- | --- |
| New: `domain/HappyHour.java` | The once-per-game pricing policy: activation guard, two-turn duration with `tick()`, 20% discount (HALF_UP) and the +15 waiting recovery, capped at 100. |
| New: `domain/ComboMeal.java` | A combo of two different menu items; extends `MenuItem` so the whole service flow treats it like a dish. Price = 90% of the combined price, HALF_UP; units = the sum. |
| New: `domain/Host.java` | The Stage 3 role: seats the oldest queued customer at the first free table once per turn, reusing the shared `Staff` one-action guard; independent of the Waiter's task. |
| Changed: `domain/MenuItem.java` | No longer `final` so `ComboMeal` can extend it (P2 polymorphism). No behaviour change for existing dishes. |
| Changed: `domain/Customer.java` | Adds `recoverSatisfaction(int)` for the Happy Hour recovery, capped at 100 and only while WAITING/SEATED. |
| Changed: `domain/RegularCustomer.java` | Stage 3 combo rule: orders the two cheapest dishes as a combo. |
| Changed: `domain/CriticCustomer.java` | Stage 3 combo rule: orders the most expensive option, the two priciest dishes paired when that costs more than any single dish. |
| Changed: `domain/Restaurant.java` | Owns the `HappyHour` policy: `getHappyHour()`, `activateHappyHour()`, `recoverWaitingSatisfaction()` (customers still waiting for a table or food) and the end-of-turn `tickHappyHour()`. |
| Changed: `domain/Waiter.java` | `takeOrder` applies the Happy Hour discount after the customer's own discount, still locked into the order once. |
| Changed: `control/GameMgr.java` | Creates and resets the Host, seats one queued customer per turn, asks the once-per-game Happy Hour question at turn start, runs the recovery while active and ticks the policy at turn end. No customer-type checks were added. |
| Changed: `ui/ConsoleUI.java` | `showTurnStart` takes the Host and shows the remaining Happy Hour duration; adds `askHappyHour()` and `showHappyHour(int)`. Display only — no rules. |
| Changed: `tests/domain`, `tests/staff` | Expectations updated for the regular's two-unit combo (price $21.60, two chef units); staff fixture `Restaurant` grows a real, never-activated `HappyHour`. |
| Changed: `tests/stage2` | Shared-flow (LSP) prices updated to the combo bills; victory/defeat scripts rewritten for the Stage 3 input order (Happy Hour question first) and Host auto-seating. |
| New: `tests/stage3/Stage3BehaviorTest.java` + `tests/stage3/run.sh` | Stage 3 behaviour suite: combos, Happy Hour stacking/lifecycle/recovery, Host seating and a scripted Happy Hour game. |
| Changed: `tests/game/run.sh` | Victory/defeat plans rewritten for the Stage 3 input order; adds Host-seating, combo-order and Happy Hour retry checks. |
| Changed: `build.ps1` | Wording only (Stage 3); the source discovery already handles any file count. |
| Changed: `README.md` | Completed responsibilities, rules, transition table, test notes and report sections. |
| Deleted production files | None. |

Final Stage 2 → Stage 3 commit/compare link: https://github.com/voltyyyyyy/sturdy-sporkoop/compare/stage-2...stage-3

- [x] Verify the `stage-2` baseline (runnable at its tag, suites green).
- [x] Resolve integration across the four streams and link passing tests and both reproducible runs.
- [x] Check the final diagram against code and coordinate publication (diagrams live in `diagrams/` at the repository root).
- [x] Tag `stage-3` after the new behaviours are implemented and playable.
- [x] Write the report's design overview delta, P2 (ComboMeal) and OCP (Happy Hour policy) evidence, with code/test references.

Report sections (also drafted into the team report document):

- Design change and reason: Stage 3 pricing variation moved into a `HappyHour` policy object owned by `Restaurant`, and menu variation into a `ComboMeal` subclass of `MenuItem`. The turn controller gained a role (`Host`) and a manager choice (Happy Hour activation) but still contains no customer-type checks; adding a new pricing policy or combo rule touches one class, not the controller or UI. Relevant classes/methods: `HappyHour.activate()/applyDiscount(long)/recover(Customer)/tick()`, `Restaurant.activateHappyHour()/recoverWaitingSatisfaction()/tickHappyHour()`, `ComboMeal(MenuItem, MenuItem)`, `Host.seatNext(Restaurant)`, `GameMgr.handleManagerChoice()/seatWithHost()`.
- P2 example: `ComboMeal` overrides nothing at runtime — it computes price and units once in its constructor and is used through the `MenuItem` interface everywhere (`Order`, `Waiter.takeOrder`, `Chef.prepare`, `ConsoleUI.orderDescription`). The same `Order` flow serves a $9.00 Salad and a $29.70 three-unit combo; the Chef's one-unit-per-turn loop needs no change, only more turns for the summed units.
- OCP example: Happy Hour pricing sits in one object. `Waiter.takeOrder` calls `customer.discountedPrice(...)` then `happyHour.applyDiscount(...)` when active — the ordering rule (VIP first, then Happy Hour) lives in one call sequence, and a future policy (e.g. lunch rush) would add a sibling policy without touching `Customer` subclasses.
- LSP evidence: `Stage2BehaviorTest.runSharedScenario(Customer, ...)` still drives seat -> order -> prepare -> serve -> eat -> pay through one `Customer`-typed method for all three types at `tests/stage2/Stage2BehaviorTest.java`; the shared `Host.seatNext` and `HappyHour.recover` likewise accept any `Customer` without type tests.
- Host independence: `Stage3BehaviorTest.hostSeating()` verifies the Host's seating succeeds and consumes only its own task while the Waiter can still seat the next customer in the same turn, and that a second Host attempt throws like any `Staff` double action.
