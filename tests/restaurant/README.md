Run the restaurant checks from the repository root with JDK 17+ and Bash:

```sh
bash tests/restaurant/run.sh
```

The runner compiles the production Restaurant, Table, WaitingQueue, GameResult,
customer, menu and enum sources against a test-only Order double in a temporary
directory. The double isolates the restaurant contract while Order's methods
remain unfinished; it is never included in the production build.

Checks cover table capacity and ownership, queue order and removal, read-only
snapshots, abandonment from the queue and from a table, payment timing,
oldest-eligible selection, duplicate payment, zero payments and each exact
victory threshold. Full gameplay still requires integration checks once the
actual Order is implemented.
