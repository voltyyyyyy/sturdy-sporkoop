Run the staff checks from the repository root with JDK 17+ and Bash:

```sh
bash tests/staff/run.sh
```

The runner compiles the production staff, customer, menu and enum sources against
test-only Order, Restaurant, Table and WaitingQueue doubles in a temporary
directory. These doubles isolate the staff contract while the other owners'
methods remain unfinished; they are never included in the production build.

Checks cover one successful action per turn, resets, retryable failures, queue
fairness, restaurant membership, order attachment, preparation delegation,
serving validation before mutation, and oldest-eligible payment delegation.
Full gameplay and accounting still require integration checks once the actual
collaborators are implemented.
