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

## What we build

Three runnable stages, each tagged in this repository. Each stage builds on the one before it.

| Stage | Adds | Git tag |
| --- | --- | --- |
| 1 - Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, both endings | `stage-1` |
| 2 - Customer behaviour | VIP and Critic customers | `stage-2` |
| 3 - New role and menu policy | Host, ComboMeal, Happy Hour | `stage-3` |

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
