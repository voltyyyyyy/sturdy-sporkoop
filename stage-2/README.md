# Restaurant Rush - Stage 2

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Yang Mingchen, Gangula Karthik, Ochi Yuma, Zhang Qiyun, Chamadol Thongthornpatch (Pluem)

This folder contains the Stage 1 game plus the **completed Stage 2 implementation**: two additional customer types (VIP and Critic), a `CustomerFactory` with the fixed arrival schedule, and per-type pricing wired through to the bill. It uses no external dependencies.

Stage 2 behaviour is fully connected: the turn controller creates arrivals through the factory, orders lock in each customer's discounted price once, and the UI labels every customer row by type. Host, combo meals and Happy Hour belong to Stage 3.

The repository root contains Stage 1. Make Stage 2 changes inside `stage-2/`; all paths below are relative to this folder unless stated otherwise.

## Stage 2 responsibilities

| Member | Code | Documentation / process |
| --- | --- | --- |
| **Gangula Karthik** | Refactor `Customer` with abstract `waitingPenalty()`, `discountedPrice(long)` and `typeLabel()`. Complete Regular hooks and `CriticCustomer` with the most-expensive-item rule. Connect discounted order creation through `Order` and `Waiter`. | Update the Stage 2 class diagram to match final code; check arrow directions, missing members and multiplicities. |
| **Yang Mingchen** | Complete `VIPCustomer` (penalty 5, 10% off) and `control/CustomerFactory.create(int, int)`. In `GameMgr`, replace direct customer construction with the factory. | Verify the exact six-arrival schedule below and no customer-type checks in the turn controller. |
| **Ochi Yuma** | Update `ConsoleUI`: customer type labels in queue/table rows, VIP discount line at payment and actual Critic order display. | Keep game rules out of the UI. Apply the report's reflection question: would this rule still exist in a GUI? Put such rules in domain/control objects. |
| **Chamadol Thongthornpatch (Pluem)** | Fill `tests/stage2/Stage2BehaviorTest.java`: VIP penalty/discount, Critic preference, factory schedule, shared service flow (LSP), victory and defeat. | Keep Stage 1 regressions green. Record reproducible Stage 2 victory and defeat inputs/results, each including a VIP and a Critic, in the test notes below. |
| **Zhang Qiyun** | Integration support: resolve merge conflicts from the four code/test streams. | Verify the runnable `stage-1` baseline, coordinate diagram publication, then tag a completed runnable `stage-2`. Finish the 1 → 2 transition table and compare link. Draft the design overview delta, P1 with three subclasses and LSP evidence. |

## Stage 2 rules and UML contracts

| Customer | Waiting penalty | Bill | Food choice |
| --- | --- | --- | --- |
| Regular | 8 | Full price | First menu item (Salad) |
| VIP | 5 | 10% off | First menu item (Salad) |
| Critic | 12 | Full price | Most expensive item (Pasta); ties use menu order |

Arrival schedule: **1 Regular, 4 VIP, 7 Regular, 10 Critic, 13 Regular, 16 Regular**.
The exact schedule and Critic preference are team choices implementing the brief's Stage 2 requirements.

As in the updated UML, `Customer` declares the behaviour hooks and all three subclasses implement them. `CriticCustomer` also overrides `chooseFood(Menu)`. `CustomerFactory` belongs to the **control** package, exposes instance method `create(int customerId, int turn): Customer`, and is held by `GameMgr`. It returns `null` on turns with no arrival; `processArrivals()` wraps that result in `Optional<Customer>`. Reject invalid IDs and turns outside 1–18.

All types share the existing service lifecycle. Dispatch through `Customer` references; do not branch on `instanceof`, class names or type labels. Keep `Restaurant` unchanged: it already calls `waitingPenalty()` and reads the locked order price. `Waiter` must pass `customer.discountedPrice(item.getPriceCents())` to the new `Order(MenuItem, long)` constructor once when ordering; never discount again at payment.

Store money as integer cents. Round VIP prices to the nearest cent; use HALF_UP for half-cent ties as the team's convention. Display two decimals. Keep the same 18-turn limit, staff/timing rules and victory targets: four paid customers, at least 4500 cents and average paid satisfaction at least 60. Stage 2 must work offline.

## Stage 2 implementation map

Every `TODO(STAGE2, ...)` marker from the scaffold has been implemented and the suites below pass. The owner-marked work landed as follows.

| Owner | Implemented in |
| --- | --- |
| Gangula Karthik | `domain/Customer.java` (abstract hooks), `RegularCustomer.java`, `CriticCustomer.java`, `Order.java` (locked-price constructor), `Waiter.java` (discounted order creation) |
| Yang Mingchen | `domain/VIPCustomer.java`, `control/CustomerFactory.java`, `control/GameMgr.java` (factory arrival integration) |
| Ochi Yuma | `ui/ConsoleUI.java` (`showCustomer` type labels, `showPayment` VIP discount line, `showOrderPlaced`) |
| Pluem | `tests/stage2/Stage2BehaviorTest.java` (all five scenarios) and the test notes below |
| Zhang Qiyun | Transition table, compare link, diagram publication and the `stage-2` tag |

Code paths in the table are under `src/main/java/restaurant/` unless starting with `tests/`.

## Structure

- `src/main/java/restaurant/ui/ConsoleUI.java` is the only class that reads from or writes to the console. It formats money for display.
- `src/main/java/restaurant/control/GameMgr.java` owns the 18-turn loop and sequences the domain actions.
- `src/main/java/restaurant/domain/` contains the customer, menu, order, restaurant and staff classes.
- [Stage 1 contracts and test notes](docs/stage-1-contracts.md) records domain responsibilities and verification cases.

## Build and run

Requires JDK 17 or newer.

From the repository root, enter the Stage 2 folder in Windows PowerShell:

```powershell
cd stage-2
.\build.ps1
.\run.ps1
```

Or, starting from the repository root in Bash:

```sh
cd stage-2
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out @out/sources.txt
java -cp out restaurant.control.GameMgr
```

## Tests

The test runners require JDK 17+ and Bash. Run them from `stage-2/` (enter it with `cd stage-2` from the repository root):

```sh
bash tests/domain/run.sh
bash tests/restaurant/run.sh
bash tests/staff/run.sh
bash tests/game/run.sh
bash tests/stage2/run.sh
```

The domain suite checks customer and order guards. The restaurant and staff suites use isolated test doubles to test their modules. The game suite launches the real application and checks an 18-turn win, an 18-turn loss, scheduled arrivals, service/payment timing and retryable invalid choices, including the VIP discount line and Critic's two-unit Pasta. The stage 2 suite covers VIP penalty/discount and rounding, the Critic's most-expensive rule, the factory schedule, the shared service flow for all three types (LSP) and both scripted full games.

The repository-root Stage 1 suites are unchanged and still pass against the `stage-1` tag.

### Stage 2 test notes

Both runs are replayed by `tests/stage2/run.sh` (via `Stage2BehaviorTest.victoryAndDefeat()`) and by `tests/game/run.sh`, which feeds the same plans to the real `GameMgr` through stdin.

| Run | Input fixture / replay command | Expected paid count, revenue, average and result | Verified commit |
| --- | --- | --- | --- |
| Victory, including VIP (#2) and Critic (#4) | `tests/game/run.sh` victory block: seat/order/serve each arrival on schedule; critic gets two chef turns for Pasta. Per turn (waiter task [+ id], chef task [+ id]): T1 `1,1,0` T2 `2,1,1,1` T3 `3,1,0` T4 `1,2,0` T5 `2,2,1,2` T6 `3,2,0` T7 `1,1,0` T8 `2,3,1,3` T9 `3,3,0` T10 `1,2,0` T11 `2,4,1,4` T12 `0,1,4` T13 `3,4,0` T14 `1,1,0` T15 `2,5,1,5` T16 `3,5,0` T17 `1,2,0` T18 `2,6,1,6` | 5 paid (turns 5, 8, 11, 15, 18), revenue $53.10 (900+810+900+1800+900), average paid satisfaction 79.6 (84/90/84/64/76), VICTORY; customer #6 keeps an unpaid $9.00 bill | `stage-2` tag |
| Defeat, including VIP and Critic | `tests/game/run.sh` defeat block: `0,0` on every turn (both staff always wait) | 0 paid, revenue $0.00, average N/A, DEFEAT; customers #1 (Regular, turn-1 arrival) and #4 (Critic, turn-10 arrival) walk out at zero satisfaction on turns 13 and 18; the VIP never drops to zero with penalty 5 | `stage-2` tag |

## Stage 1 → Stage 2 transition

This table records what changed between the `stage-1` and `stage-2` tags.

| Files | Change and reason |
| --- | --- |
| New: `domain/VIPCustomer.java` | VIP behaviour as a `Customer` subclass: waiting penalty 5, 10% off the listed price (HALF_UP to whole cents), label "VIP". |
| New: `domain/CriticCustomer.java` | Critic behaviour as a `Customer` subclass: waiting penalty 12, full price, label "Critic", and `chooseFood(Menu)` overridden to pick the most expensive item (Pasta; ties keep menu order). |
| New: `control/CustomerFactory.java` | Factory in the control package owning the fixed arrival schedule (1 Regular, 4 VIP, 7 Regular, 10 Critic, 13 Regular, 16 Regular); returns `null` on other turns and rejects invalid ids/turns. |
| Changed: `domain/Customer.java` | Declares the abstract Stage 2 hooks `waitingPenalty()`, `discountedPrice(long)` and `typeLabel()` next to the stage 1 state machine; adds `getBillCents()`/`hasUnpaidBill()` reading the locked order. |
| Changed: `domain/RegularCustomer.java` | Implements the three hooks for the stage 1 behaviour (penalty 8, full price, label "Regular"), so the base flow is unchanged. |
| Changed: `domain/Order.java` | Adds `Order(MenuItem, long)` so the price is captured once at creation and never recomputed; the stage 1 constructor is unchanged. |
| Changed: `domain/Waiter.java` | `takeOrder` passes `customer.discountedPrice(item.getPriceCents())` to the new constructor; no subtype checks. |
| Changed: `control/GameMgr.java` | Holds `CustomerFactory` and creates arrivals through `create(id, turn)` in `processArrivals()`; order display delegated to `ConsoleUI.showOrderPlaced(Customer)`. |
| Changed: `ui/ConsoleUI.java` | Queue/table rows show the domain-supplied type label; `showPayment` adds the label and a savings line when the locked bill is below the listed price; `showOrderPlaced` prints dish, units and locked price for every type. |
| Changed: `build.ps1` | Allows additional Stage 2 sources instead of requiring exactly 17 types. |
| New: `tests/stage2/Stage2BehaviorTest.java` + `tests/stage2/run.sh` | Stage 2 behaviour suite: VIP pricing/rounding, critic rule, factory schedule, shared service flow (LSP) and scripted victory/defeat. |
| Changed: `tests/game/run.sh` | Expected outputs updated for type labels, the VIP discount line, the critic's two-unit Pasta and its shifted payment turn; rule and timing checks preserved. |
| Changed: `tests/staff/fixtures/restaurant/domain/Order.java` | Fixture gains the locked-price constructor used by the production `Waiter`. |
| Changed: `README.md` | Completed responsibilities, transition table, test notes and stage 2 rules. |
| Deleted production files | None. |

Final Stage 1 → Stage 2 commit/compare link: https://github.com/voltyyyyyy/sturdy-sporkoop/compare/stage-1...stage-2

- [x] Inspect the existing local `stage-1` tag and verify the intended runnable baseline and remote tag.
- [x] Resolve integration conflicts and link passing tests and both reproducible runs.
- [x] Check the final diagram against code and coordinate publication (diagrams live in `diagrams/` at the repository root).
- [x] Tag `stage-2` after the new behaviours are implemented and playable.
- [x] Write the report's design overview delta, P1 dispatch through three subclasses and shared-flow LSP evidence, with code/test references.

Report sections (also drafted into the team report document):

- Design change and reason: Customer behaviour that varies by type (waiting penalty, price, label, food choice) moved into abstract hooks on `Customer`, implemented by `RegularCustomer`, `VIPCustomer` and `CriticCustomer`. Creation moved into `CustomerFactory`, so adding a type touches the factory and one new subclass, not the controller or `Restaurant`; relevant classes/methods: `Customer.waitingPenalty()/discountedPrice(long)/typeLabel()/chooseFood(Menu)`, `CustomerFactory.create(int, int)`, `GameMgr.processArrivals()`, `Waiter` order creation.
- P1 example: `Waiter.takeOrder` calls `customer.discountedPrice(item.getPriceCents())` through a `Customer` reference. `RegularCustomer` returns the price unchanged; `VIPCustomer` multiplies by 0.90 with HALF_UP rounding (900 → 810 cents); `CriticCustomer` returns the full price. The same single call site prices every type — no `instanceof`, no branch on labels. `Restaurant.applyWaitingPenalties()` likewise calls `c.waitingPenalty()` polymorphically (8 / 5 / 12).
- LSP evidence: `Stage2BehaviorTest.runSharedScenario(Customer, ...)` drives seat → order → prepare → serve → eat → pay through one `Customer`-typed method for all three subclasses; all assert the identical state sequence and table release, with only the locked price differing (900 / 810 / 1800 cents). The scripted full-game runs reach VICTORY with the VIP and Critic flowing through the unchanged turn controller. Tested commit: `stage-2` tag.

## Current gameplay rules (Stage 2)

- Two tables, one Waiter, one Chef and one Cashier.
- Arrivals on turns 1, 4, 7, 10, 13 and 16, created by `CustomerFactory`: Regular, VIP, Regular, Critic, Regular, Regular. Each customer starts at satisfaction 100.
- The Waiter can seat, take an order or serve once per turn. The Chef can advance one preparation unit once per turn. The Cashier automatically collects at most one eligible payment per turn.
- Regular and VIP customers select the first menu item, Salad. Critics select the most expensive item, Pasta (1800 cents, two preparation units). The UI displays the selected dish, its units and the locked price when the order is placed.
- Salad costs 900 cents and takes one preparation unit; Burger costs 1500 cents and takes one unit; Pasta costs 1800 cents and takes two units.
- Waiting or seated customers who have not been served lose satisfaction at turn end: Regular 8, VIP 5, Critic 12. Served customers stop losing satisfaction. A customer at zero leaves without paying; a placed order left behind is cancelled and recorded as a lost bill.
- VIPs pay 10% off the listed price, rounded to whole cents with HALF_UP; the discount is locked into the order when it is placed and never recomputed. Regulars and Critics pay the full listed price.
- After all 18 turns, victory requires at least four paid customers, at least 4500 cents revenue and an average satisfaction of at least 60 among paid customers.
- Money is stored as integer cents and formatted with two decimal places in the UI.

The game always completes all 18 turns. The turn-16 arrival cannot pay before the game ends.
