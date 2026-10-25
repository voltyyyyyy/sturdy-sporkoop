# sturdy-sporkoop

SC2002 / SC2302 Object Oriented Design & Programming - **Group Assignment: Restaurant Rush**.

A turn-based restaurant management game in Java with a command-line interface.
The player is the manager and decides which customers and orders get staff attention first.

## Assignment files

| File | What it is |
| --- | --- |
| [`assignment/SC2002_Restaurant_Rush_Group_Assignment.docx`](assignment/SC2002_Restaurant_Rush_Group_Assignment.docx) | The official brief, downloaded from NTULearn. |
| [`assignment/assignment-brief.md`](assignment/assignment-brief.md) | The same brief as Markdown, for quick reading and diffing. |

Source: NTULearn course `26S1-SC2002-SC2302-OBJECT ORIENTED DES & PROG`, content folder **Group Assignment**, released 6 Oct 2026.

## Design documentation

| Document | What it is |
| --- | --- |
| [Stage 1 class diagram](docs/uml/stage-1-classes.png) ([SVG](docs/uml/stage-1-classes.svg), [PlantUML source](docs/uml/stage-1-classes.puml)) | Entities, control and boundary classes for the `stage-1` build, with responsibilities and multiplicities. |
| [Stage 1 design notes](docs/uml/stage-1-classes.md) | How to read the notation, the rules encoded in the model, and the Stage 2 and 3 changes the final report diagram must show. |

The diagram is updated at each stage; the PDF report must use the Stage 3 version.

## What we build

Three runnable stages, each tagged in this repository. Each stage builds on the one before it.

| Stage | Adds | Git tag |
| --- | --- | --- |
| 1 - Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings | `stage-1` |
| 2 - Customer behaviour | VIP and Critic customers | `stage-2` |
| 3 - New role and menu policy | Host, ComboMeal, Happy Hour | `stage-3` |

Stage 1 is implemented in this commit. Stage 2 and Stage 3 have not started.

## Build and run

Requires Java 17 or newer. There are no external dependencies, so the game builds and runs offline.

Windows PowerShell:

```powershell
.\build.ps1
.\run.ps1
```

`run.ps1` compiles first, then starts the game. Choose a different dish sequence with:

```powershell
.\run.ps1 --seed 526
```

Manual build, if you prefer not to use the script:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 --release 17 -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp out restaurant.app.Main
```

macOS or Linux:

```bash
mkdir -p out
find src/main/java -name '*.java' > out/sources.txt
javac -encoding UTF-8 --release 17 -d out @out/sources.txt
java -cp out restaurant.app.Main
```

The game prints a numbered menu every turn. Type the option number and press Enter. A seat action asks for the customer and then the table id; serve and prepare actions ask you to choose from the displayed list. The seed is printed at start-up, so the customer dish choices can be reproduced.

## Reproducible runs

| Run | Seed | Input | Final result | Transcript |
| --- | --- | --- | --- | --- |
| Victory | 526 | [`runs/victory.txt`](runs/victory.txt) | 4 paid, $72.00, average 66.0 | [`stage-1-victory.txt`](docs/test-runs/stage-1-victory.txt) |
| Defeat | 42 | [`runs/defeat.txt`](runs/defeat.txt) | 0 paid, $0.00, N/A | [`stage-1-defeat.txt`](docs/test-runs/stage-1-defeat.txt) |

After building:

```powershell
Get-Content runs\victory.txt | java -cp out restaurant.app.Main --seed 526
Get-Content runs\defeat.txt  | java -cp out restaurant.app.Main --seed 42
```

In `cmd.exe`, use input redirection:

```cmd
java -cp out restaurant.app.Main --seed 526 < runs\victory.txt
java -cp out restaurant.app.Main --seed 42 < runs\defeat.txt
```

Use `java` directly for scripted runs; `run.ps1` is for interactive play. The full test notes, including the rule checks, are in [`docs/test-runs/stage-1.md`](docs/test-runs/stage-1.md).

## Base setting (must always be reproducible)

- 18 turns maximum
- 2 tables
- Waiter x 1 (one task per turn), Chef x 1 (one preparation unit per turn), Cashier x 1 (one payment per turn)
- One customer arrives at the start of turns 1, 4, 7, 10, 13 and 16 (six in total)

Victory at the end of turn 18 needs all three: at least 4 paid customers, revenue of at least $45, and average satisfaction of paid customers at least 60.

Menu: Salad $9.00 / 1 unit, Burger $15.00 / 1 unit, Pasta $18.00 / 2 units.

## Submission checklist

- [ ] Source code with build and run instructions
- [ ] README with the repository URL, stage comparisons (Section 5) and video timestamps
- [ ] PDF report with diagrams, design discussion, functional evidence and reflections
- [ ] About 15 minutes of recorded video with readable screen capture
- [ ] Signed Declaration of Original Work and each member's Declaration on Use of GAI Assistance

Archive name: `LABGroup_AssignmentGrp.zip`, for example `FCS1_Grp1.zip`.
