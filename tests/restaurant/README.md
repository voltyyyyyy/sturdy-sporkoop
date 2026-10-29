Run the restaurant checks from the repository root with JDK 17+ and Bash:

```sh
bash tests/restaurant/run.sh
```

The runner compiles the production Restaurant, Table, WaitingQueue, GameResult,
customer, menu and enum sources against a test-only Order double in a temporary
directory. The double isolates Restaurant behavior from Order internals; it is
never included in the production build.

Checks cover table capacity and ownership, queue order and removal, read-only
snapshots, abandonment from the queue and from a table, payment timing,
oldest-eligible selection, duplicate payment, zero payments and each exact
victory threshold. Run `bash tests/game/run.sh` for full-game integration checks.
