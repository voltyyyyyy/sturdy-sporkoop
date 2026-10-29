Run the staff checks from the repository root with JDK 17+ and Bash:

```sh
bash tests/staff/run.sh
```

The runner compiles the production staff, customer, menu and enum sources against
test-only Order, Restaurant, Table and WaitingQueue doubles in a temporary
directory. These doubles isolate staff behavior from collaborator internals;
they are never included in the production build.

Checks cover one successful action per turn, resets, retryable failures, queue
fairness, restaurant membership, order attachment, preparation delegation,
serving validation before mutation, and oldest-eligible payment delegation.
Run `bash tests/game/run.sh` for full-game integration checks.
