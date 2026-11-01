# Restaurant Rush - Stage 2

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Yang Mingchen, Gangula Karthik, Ochi Yuma, Zhang Qiyun, Chamadol Thongthornpatch (Pluem)

This folder contains the Stage 1 game plus **unfinished Stage 2 prototypes** for two new customer types and a factory. It uses no external dependencies. The new method bodies are deliberately left for the assigned members to implement.

The prototypes compile, but Stage 2 behaviour is not connected yet: running the game still uses the regular-only Stage 1 flow. New stub methods throw `UnsupportedOperationException` with the owner's name if called before implementation. Host, combo meals and Happy Hour belong to Stage 3.

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

## Where to fill the blanks

Search for `TODO(STAGE2,` to find the owner-marked work. Implement the throwing method bodies first, then connect the TODO call sites in `GameMgr`, `Waiter` and `ConsoleUI`. Existing baseline method bodies are retained until those replacements are ready.

Each blank has an `IMPLEMENT HERE` guide. Replace its `UnsupportedOperationException` with your code. In existing working methods, replace only the marked area or line. Start with customer methods and the factory, then order pricing, controller/UI integration and tests.

| Owner | Starting files |
| --- | --- |
| Gangula Karthik | `domain/Customer.java`, `RegularCustomer.java`, `CriticCustomer.java`, `Order.java`, `Waiter.java` |
| Yang Mingchen | `domain/VIPCustomer.java`, `control/CustomerFactory.java`, `control/GameMgr.java` |
| Ochi Yuma | `ui/ConsoleUI.java` (`showCustomer`, `showPayment`, `showOrderPlaced`) |
| Pluem | `tests/stage2/Stage2BehaviorTest.java` and the test notes below |
| Zhang Qiyun | The transition table and report checklist below |

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
```

The domain suite checks customer and order guards. The restaurant and staff suites use isolated test doubles to test their modules. The game suite launches the real application and checks an 18-turn win, an 18-turn loss, scheduled arrivals, service/payment timing and retryable invalid choices.

The new Stage 2 test skeleton is separate and intentionally fails until Pluem fills its bodies. After building, compile and run it from `stage-2/`:

```powershell
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -cp out -d out tests/stage2/Stage2BehaviorTest.java
java -cp out restaurant.domain.Stage2BehaviorTest
```

Keep the repository-root Stage 1 suites passing. After Stage 2 integration, update the copied game suite's expected output for the VIP discount and Critic's two-unit Pasta while preserving the rule/timing checks.

### Stage 2 test notes — TODO(Pluem)

| Run | Input fixture / replay command | Expected paid count, revenue, average and result | Verified commit |
| --- | --- | --- | --- |
| Victory, including VIP and Critic | TODO | TODO | TODO |
| Defeat, including VIP and Critic | TODO | TODO | TODO |

## Stage 1 → Stage 2 transition — TODO(Zhang Qiyun)

This table describes the scaffold; finish it against the implemented stage before release.

| Files | Change and reason |
| --- | --- |
| New: `VIPCustomer.java`, `CriticCustomer.java` | Subclass prototypes for the two additional behaviours. |
| New: `control/CustomerFactory.java` | Factory prototype matching the UML; owns scheduled subtype creation. |
| Changed: `Customer.java`, `RegularCustomer.java` | Add abstract pricing/label hooks and Regular implementation stubs. |
| Changed: `Order.java`, `Waiter.java` | Locked-price constructor stub and a TODO to apply customer pricing at order creation. |
| Changed: `GameMgr.java`, `ConsoleUI.java` | Factory field and owner-marked arrival/display integration points. |
| Changed: `build.ps1` | Allow additional Stage 2 sources instead of requiring exactly 17 types. |
| New: `tests/stage2/Stage2BehaviorTest.java`; changed: `README.md` | Blank test methods, responsibilities, rules and handoff notes. |
| Deleted production files | None. |

Final Stage 1 → Stage 2 commit/compare link: **TODO(Qiyun)**.

- [ ] TODO(Qiyun): inspect the existing local `stage-1` tag and verify the intended runnable baseline and remote tag.
- [ ] TODO(Qiyun): resolve integration conflicts and link passing tests and both reproducible runs.
- [ ] TODO(Gangula/Qiyun): check the final diagram against code and coordinate publication. UML files remain excluded from this push.
- [ ] TODO(Qiyun): tag `stage-2` only after the new behaviours are implemented and playable; fill the final compare link.
- [ ] TODO(Qiyun): write the report's design overview delta, P1 dispatch through three subclasses and shared-flow LSP evidence, with code/test references.

Report placeholders for Qiyun to fill:

- Design change and reason: **[fill in]**; relevant classes/methods: **[fill in]**.
- P1 example: **[Customer-typed call]**; behaviour in each subclass: **[fill in]**.
- LSP evidence: **[shared service-flow test and result]**; tested commit: **[fill in]**.

## Current gameplay rules (Stage 1 baseline)

- Two tables, one Waiter, one Chef and one Cashier.
- Arrivals on turns 1, 4, 7, 10, 13 and 16; each customer starts at satisfaction 100.
- The Waiter can seat, take an order or serve once per turn. The Chef can advance one preparation unit once per turn. The Cashier automatically collects at most one eligible payment per turn.
- Customers use a documented deterministic rule: they select the first menu item, Salad. The UI displays the selected dish when the order is placed.
- Salad costs 900 cents and takes one preparation unit; Burger costs 1500 cents and takes one unit; Pasta costs 1800 cents and takes two units.
- Waiting or seated customers who have not been served lose 8 satisfaction at turn end, down to zero. Served customers stop losing satisfaction. A customer at zero leaves without paying.
- After all 18 turns, victory requires at least four paid customers, at least 4500 cents revenue and an average satisfaction of at least 60 among paid customers.
- Money is stored as integer cents and formatted with two decimal places in the UI.

The game always completes all 18 turns. The turn-16 arrival cannot pay before the game ends.
