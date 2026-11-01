# Restaurant Rush - Stage 2

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Yang Mingchen, Gangula Karthik, Ochi Yuma, Zhang Qiyun, Chamadol Thongthornpatch (Pluem)

This folder is the starting point for Stage 2 development, copied from the playable Stage 1 restaurant game. It currently runs the same offline, 18-turn command-line game, contains the 17 types in the Stage 1 design and uses no external dependencies.

Stage 2 features have not been implemented yet. The VIP/Critic customers, Host, combo meals and Happy Hour are not available in this copy.

The repository root contains Stage 1. Make Stage 2 changes inside `stage-2/`; all paths below are relative to this folder unless stated otherwise.

## Inherited task allocation

The following responsibilities describe the Stage 1 baseline and can be extended as Stage 2 work is defined.

| Member and area | Files | Responsibilities |
| --- | --- | --- |
| **Yang Mingchen — Customer behaviour** | `Customer.java`, `RegularCustomer.java`, `CustomerState.java` | Guard customer-state changes; handle satisfaction loss, clamping and abandonment; define the deterministic food choice and serving/readiness turns. |
| **Gangula Karthik — Menu and orders** | `MenuItem.java`, `Menu.java`, `Order.java`, `OrderStatus.java` | Validate dishes and prices; provide menu lookup and one-unit preparation; guard serving, payment and cancellation while keeping order prices locked. |
| **Zhang Qiyun — Restaurant, storage and scoring** | `Restaurant.java`, `Table.java`, `WaitingQueue.java`, `GameResult.java` | Enforce table and queue rules; manage eligible payments, snapshots and cleanup; calculate the final score and victory result. |
| **Ochi Yuma — Staff actions** | `Staff.java`, `Waiter.java`, `Chef.java`, `Cashier.java` | Enforce one successful staff task per turn; implement Waiter seating, ordering and serving; delegate Chef preparation and Cashier collection to domain methods. |
| **Chamadol Thongthornpatch — Game flow and console** | `GameMgr.java`, `ConsoleUI.java` | Handle prompts, displays, parsing and errors; sequence all 18 turns, arrivals and staff actions; run automatic eating/payment and display the final result. |

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
