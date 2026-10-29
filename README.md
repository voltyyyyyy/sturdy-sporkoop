# Restaurant Rush - Stage 1 implementation scaffold

SC2002 / SC2302 Object-Oriented Design and Programming

Team: Mingchen, Karthik, Yuma, Qiyun, Pluem

This repository replaces the previous implementation with prototypes for the **17 types in the supplied Stage 1 design**. Constructors, fields, basic getters and enum values provide a shared starting point. Business rules and console interaction are deliberately left as TODOs that throw `UnsupportedOperationException`; this is not a playable game or evidence of completed requirements.

## Independent work allocation

| Owner | Files |
| --- | --- |
| Mingchen | `Customer`, `RegularCustomer`, `CustomerState` |
| Karthik | `MenuItem`, `Menu`, `Order`, `OrderStatus` |
| Qiyun | `Restaurant`, `Table`, `WaitingQueue`, `GameResult` |
| Yuma | `Staff`, `Waiter`, `Chef`, `Cashier` |
| Chamadol Thongthornpatch | `GameMgr`, `ConsoleUI` |

All shared method signatures already exist and compile together. Implement your own files against these signatures. Agree any signature changes with affected owners first. Use small local test fixtures or test doubles while another owner's TODO remains unfinished; do not implement their rules in your module.

## Structure

- `src/main/java/restaurant/ui/ConsoleUI.java`: the only boundary class and the only place for console input/output and money formatting.
- `src/main/java/restaurant/control/GameMgr.java`: the only control class; owns turn sequencing and the entry point.
- `src/main/java/restaurant/domain/`: the other 15 types, including all staff, abstract classes and both enums.
- [Shared contracts and implementation checklist](docs/stage-1-contracts.md): rules, ownership, dependencies and handoff checks.

There is no extra `Main`, `TurnEngine`, configuration class, action/request type, pricing strategy, interface or custom exception. Stage 2/3 customer types, Host, combos and Happy Hour are not part of this scaffold.

## Build

Requires JDK 17 or newer. No external dependencies.

Windows PowerShell:

```powershell
.\build.ps1
```

Portable compilation from the repository root:

```sh
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -Xlint:all -Werror -d out @out/sources.txt
```

The skeleton is expected to compile. Running the entry point currently reaches unimplemented TODO methods; gameplay starts working only after the team implements them.

After implementation:

```powershell
.\run.ps1
```

Or:

```sh
java -cp out restaurant.control.GameMgr
```

## Required Stage 1 behaviour

- 18 turns, exactly two tables, one Waiter, one Chef and one Cashier.
- Arrivals on turns 1, 4, 7, 10, 13 and 16; initial satisfaction 100.
- Waiter: at most one seating, ordering or serving task each turn. Chef: at most one preparation unit. Cashier: at most one eligible payment, automatically.
- Regular waiting penalty: 8, clamped at zero. No loss after serving.
- Salad: 900 cents / 1 unit; Burger: 1500 cents / 1 unit; Pasta: 1800 cents / 2 units.
- Victory after turn 18 requires at least four **paid** customers, at least 4500 cents revenue, and average satisfaction of paid customers at least 60.
- All amounts use `long` cents; all domain state changes use guarded methods.

The pasted report names Salad and Pasta; Burger's values and the precise eating/payment timing come from the former repository's assignment brief. They fill omissions without changing the pasted report's class design.

The old source, assignment copies, diagrams, completed-game claims and run transcripts were removed as requested. They remain recoverable in Git history. Existing stage tags have not been moved and are not claims that this scaffold is playable.
