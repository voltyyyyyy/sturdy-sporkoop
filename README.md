# Restaurant Rush

SC2002 / SC2302 Object-Oriented Design and Programming group project.

Repository: https://github.com/voltyyyyyy/sturdy-sporkoop

A turn-based restaurant management game with a command-line interface, built in three runnable stages. Each stage is a tagged, runnable version in this repository. The game runs offline with no external dependencies and requires JDK 17 or newer.

## The three stages

### Stage 1 - core game (tag `stage-1`, repository root)

An 18-turn restaurant game with two tables and three staff roles (Waiter, Chef, Cashier). Six customers arrive on turns 1, 4, 7, 10, 13 and 16 with satisfaction 100. Each staff member performs at most one successful task per turn. Unserved customers lose satisfaction each turn and leave without paying at zero. Victory after 18 turns requires at least four paid customers, at least $45.00 revenue and an average paid-customer satisfaction of at least 60. Money is stored as integer cents. 17 types, no external dependencies.

### Stage 2 - customer types and factory (tag `stage-2`, `stage-2/`)

Adds VIP and Critic customer types through abstract behaviour hooks on `Customer` (`waitingPenalty()`, `discountedPrice(long)`, `typeLabel()`), so no `instanceof` checks exist anywhere. A `CustomerFactory` in the control package owns the fixed arrival schedule (Regular, VIP, Regular, Critic, Regular, Regular). Each order locks in the customer's discounted price once, at creation. VIPs lose 5 satisfaction per turn and pay 10% less (HALF_UP rounding); Critics lose 12 per turn and order the most expensive dish.

### Stage 3 - Host, combos and Happy Hour (tag `stage-3`, `stage-3/`)

Adds a `Host` role that automatically seats one queued customer per turn, `ComboMeal` (a `MenuItem` subclass priced at 90% of the combined price, HALF_UP), and a once-per-game `HappyHour` policy owned by `Restaurant`: 20% off new orders for two turns, stacking after the customer's own discount, plus +15 satisfaction recovery for waiting customers each active turn. Regulars order the two cheapest dishes as a combo; Critics order the two priciest.

## Stage comparisons

### Stage 1 to Stage 2

Compare link: https://github.com/voltyyyyyy/sturdy-sporkoop/compare/stage-1...stage-2

| Files | Change and reason |
| --- | --- |
| New: `domain/VIPCustomer.java`, `domain/CriticCustomer.java` | Customer behaviour that varies by type (waiting penalty, price, label, food choice) implemented as `Customer` subclasses instead of type checks. |
| New: `control/CustomerFactory.java` | Owns the fixed arrival schedule; the controller no longer constructs customers directly. |
| New: `tests/stage2/` | Behaviour suite: VIP pricing and rounding, Critic preference, factory schedule, shared service flow (LSP), scripted victory and defeat. |
| Changed: `domain/Customer.java`, `domain/RegularCustomer.java` | Abstract Stage 2 hooks declared on the base class; the Regular subclass keeps the Stage 1 behaviour. |
| Changed: `domain/Order.java` | Locked-price constructor so the discounted price is captured once and never recomputed. |
| Changed: `domain/Waiter.java`, `control/GameMgr.java`, `ui/ConsoleUI.java` | Ordering passes through `discountedPrice()`, arrivals go through the factory, and the UI shows domain-supplied type labels. |
| Changed: `tests/*`, `build.ps1` | Expectations updated for type labels and the Critic's two-unit Pasta; build allows more than 17 types. |
| Deleted | None. |

### Stage 2 to Stage 3

Compare link: https://github.com/voltyyyyyy/sturdy-sporkoop/compare/stage-2...stage-3

| Files | Change and reason |
| --- | --- |
| New: `domain/HappyHour.java` | Once-per-game pricing policy: two-turn duration, 20% discount (HALF_UP), +15 waiting recovery. |
| New: `domain/ComboMeal.java` | A combo of two different menu items; extends `MenuItem` so the whole service flow treats it like a dish. |
| New: `domain/Host.java` | Seats the oldest queued customer at the first free table once per turn, reusing the shared `Staff` guard. |
| New: `tests/stage3/` | Behaviour suite: combos, Happy Hour stacking/lifecycle/recovery, Host seating, scripted Happy Hour game. |
| Changed: `domain/MenuItem.java` | No longer `final`, so `ComboMeal` can extend it (polymorphism). No behaviour change for existing dishes. |
| Changed: `domain/Customer.java`, `domain/RegularCustomer.java`, `domain/CriticCustomer.java` | `recoverSatisfaction(int)` for the Happy Hour recovery; Stage 3 combo choice rules. |
| Changed: `domain/Restaurant.java`, `domain/Waiter.java` | Restaurant owns the policy (activation, recovery, end-of-turn tick); ordering applies Happy Hour after the customer discount. |
| Changed: `control/GameMgr.java`, `ui/ConsoleUI.java` | Host seating, the Happy Hour manager choice, and the remaining-duration display. No customer-type checks added. |
| Changed: `tests/*`, `build.ps1` | Expectations and scripts updated for combo orders and the new input order. |
| Deleted | None. |

## Build and run

Requires JDK 17 or newer. From the folder of the stage you want to run (repository root for Stage 1, `stage-2/`, `stage-3/`):

Windows PowerShell:

```powershell
.\build.ps1
.\run.ps1
```

Bash:

```sh
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out @out/sources.txt
java -cp out restaurant.control.GameMgr
```

## Tests

The test runners require JDK 17+ and Bash. Run them from the stage folder:

```sh
bash tests/domain/run.sh
bash tests/restaurant/run.sh
bash tests/staff/run.sh
bash tests/game/run.sh
bash tests/stage2/run.sh
bash tests/stage3/run.sh
```

The suites cover domain guards, payment and one-task-per-turn rules, the shared service flow for all customer types (LSP), combo pricing, the Happy Hour lifecycle, Host seating, and full scripted victory and defeat runs through the real `GameMgr`.

## Video timestamps

The recorded video walks through the design first (0:00 to 11:40), then shows a live demo.

| Time | Speaker | Content |
| --- | --- | --- |
| 0:00 | Zhang Qiyun | Title, agenda, what we built, winning and losing |
| 1:45 | Zhang Qiyun | System assumptions, architecture, one turn in six steps |
| 3:00 | Chamadol Thongthornpatch (Pluem) | Final class diagram, staff one-task-per-turn, customer hierarchy |
| 4:30 | Chamadol Thongthornpatch (Pluem) | State machines, encapsulation and composition, robust input |
| 6:05 | Yang Mingchen | How the design grew: Stage 1 to 2, ComboMeal, Host, Happy Hour |
| 8:15 | Yang Mingchen | Change analysis: 3 added, 6 modified, 11 unchanged; then 3 added, 8 modified, 12 unchanged |
| 8:55 | Ochi Yuma | SOLID scorecard, sequence diagram, trade-offs |
| 10:30 | Ochi Yuma | Testing and reproducibility, GitHub evidence, handoff |
| 11:40 | Gangula Karthik | Live demo: build, a victory run, a defeat run |
