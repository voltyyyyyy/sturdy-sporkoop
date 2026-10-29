# Restaurant Rush - Stage 1

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Mingchen, Karthik, Yuma, Qiyun, Chamadol Thongthornpatch

This is a playable, offline Stage 1 restaurant game with an 18-turn command-line interface. The implementation contains the 17 types in the Stage 1 design and uses no external dependencies.

## Work allocation

| Owner | Files |
| --- | --- |
| Mingchen | `Customer`, `RegularCustomer`, `CustomerState` |
| Karthik | `MenuItem`, `Menu`, `Order`, `OrderStatus` |
| Qiyun | `Restaurant`, `Table`, `WaitingQueue`, `GameResult` |
| Yuma | `Staff`, `Waiter`, `Chef`, `Cashier` |
| Chamadol Thongthornpatch | `GameMgr`, `ConsoleUI` |

## Structure

- `src/main/java/restaurant/ui/ConsoleUI.java` is the only class that reads from or writes to the console. It formats money for display.
- `src/main/java/restaurant/control/GameMgr.java` owns the 18-turn loop and sequences the domain actions.
- `src/main/java/restaurant/domain/` contains the customer, menu, order, restaurant and staff classes.
- [Stage 1 contracts and test notes](docs/stage-1-contracts.md) records domain responsibilities and verification cases.

The game implements Stage 1 only. It does not include the VIP/Critic customers, Host, combo meals or Happy Hour from later stages.

## Build and run

Requires JDK 17 or newer.

In Windows PowerShell:

```powershell
.\build.ps1
.\run.ps1
```

Or compile and run from the repository root:

```sh
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out @out/sources.txt
java -cp out restaurant.control.GameMgr
```

## Tests

The test runners require JDK 17+ and Bash. Run them from the repository root:

```sh
bash tests/domain/run.sh
bash tests/restaurant/run.sh
bash tests/staff/run.sh
bash tests/game/run.sh
```

The domain suite checks customer and order guards. The restaurant and staff suites use isolated test doubles to test their modules. The game suite launches the real application and checks an 18-turn win, an 18-turn loss, scheduled arrivals, service/payment timing and retryable invalid choices.

## Stage 1 rules

- Two tables, one Waiter, one Chef and one Cashier.
- Arrivals on turns 1, 4, 7, 10, 13 and 16; each customer starts at satisfaction 100.
- The Waiter can seat, take an order or serve once per turn. The Chef can advance one preparation unit once per turn. The Cashier automatically collects at most one eligible payment per turn.
- Customers use a documented deterministic rule: they select the first menu item, Salad. The UI displays the selected dish when the order is placed.
- Salad costs 900 cents and takes one preparation unit; Burger costs 1500 cents and takes one unit; Pasta costs 1800 cents and takes two units.
- Waiting or seated customers who have not been served lose 8 satisfaction at turn end, down to zero. Served customers stop losing satisfaction. A customer at zero leaves without paying.
- After all 18 turns, victory requires at least four paid customers, at least 4500 cents revenue and an average satisfaction of at least 60 among paid customers.
- Money is stored as integer cents and formatted with two decimal places in the UI.

The game always completes all 18 turns. The turn-16 arrival cannot pay before the game ends.
