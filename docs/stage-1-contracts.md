# Stage 1 contracts and ownership

The supplied report is the architectural reference. These are implementation contracts for its 17 types, not a claim that the TODOs are implemented. The report's diagram image was not supplied; this scaffold follows the types, relationships and responsibilities described in its text.

## Boundaries and OOP evidence

| Concern | Owner and contract |
| --- | --- |
| Boundary | ConsoleUI alone reads input and prints. It displays model getters and GameResult; it never computes satisfaction, scoring, payment totals or victory. Dollar formatting is presentation work. |
| Control | GameMgr creates one Restaurant and one of each staff role, schedules arrivals and sequences turns. It depends on concrete ConsoleUI: DIP remains partial. |
| Encapsulation E1 | Order.status and priceCents are private; only advanceOneUnit(), markServed(), markPaid() and cancel() transition an order. markPaid() requires SERVED. Customer satisfaction is private with no setter. |
| Polymorphism P1 | Customer is abstract. RegularCustomer overrides waitingPenalty() with 8. Call customer.waitingPenalty() through Customer everywhere; no subclass tests or switches. |
| Composition C1 | Restaurant constructs exactly two Tables and retains them for its lifetime. It exposes an unmodifiable list. Tables are not injected, transferable or shared between restaurants. Menu constructs its three MenuItems. |
| Aggregation | WaitingQueue receives Customer objects created outside it. Removing a customer never destroys the customer. Queue order is arrival order. |
| Shared staff rule | Staff is abstract and owns name plus actedThisTurn. All three roles extend it. Only a successful valid action consumes a task. |
| Value object | GameResult contains a verdict and final values computed by Restaurant; the UI never recalculates them. |
| Deferred design | No interfaces in Stage 1: ISP is absent. No PricingPolicy, State-pattern classes, VIP, Critic, Host, ComboMeal or Happy Hour. |

A Table's private occupant reference implements the UML association to Customer. Do not add another customer/occupant-id field representing the same relationship. The report's statement about deleting an attribute from its diagram does not remove the reference required to implement the association in Java.

## Shared error and data conventions

- Use IllegalArgumentException for malformed arguments and IllegalStateException for forbidden actions. Do not add a custom exception type.
- Validate an action completely before changing model state. A failed action leaves the staff task available and must not partially update a queue, table, customer or score.
- ConsoleUI handles parsing and prints errors; GameMgr catches expected rejected actions and lets the manager retry. UnsupportedOperationException denotes unfinished code, not a manager input error.
- Customer.getOrder() is null before ordering. Table.getOccupant() is null when free. Optional<Customer> means no customer is available. OptionalDouble.empty() means no paid-customer average; display N/A.
- All money fields/parameters use long cents. Order captures MenuItem.getPriceCents() once in its constructor; there is no price setter or later menu lookup.
- Collections returned to callers are read-only snapshots or immutable collections.
- Restaurant's servedCount is the report's completed-service score: count customers who have paid, once each. Do not count a SERVED-but-unpaid order toward the four-paid-customer target.
- Track customer serving and readiness turns separately so eating and payment cannot occur prematurely.
- Standard library collections and Optional types are permitted; add no project types beyond the report's 17.

## Six turn steps - Pluem sequences, domain owners implement

Before the steps, reset Waiter, Chef and Cashier once and display the start-of-turn state.

1. **Arrivals.** On turns 1, 4, 7, 10, 13 and 16, GameMgr creates a RegularCustomer and adds it to the waiting queue. Customer constructor starts at satisfaction 100 and WAITING.
2. **Manager choices.** Read at most one successful Waiter task and one successful Chef task, or wait for either. Only the customer chooses food. Numeric task codes belong to input/controller dispatch, not the domain model.
3. **Kitchen and serving.** Execute the Waiter before the Chef. The Chef may work on an order taken this turn. Prompting can be interleaved with execution to make that new order selectable. A dish completed by the Chef this turn cannot be served until the next turn at the earliest.
4. **Eating and payment.** Delegate eating progression to customers through Restaurant. A customer served on turn N becomes READY_TO_PAY on N+1 at the earliest and pays on N+2 at the earliest. Cashier automatically collects at most one payment from the oldest eligible customer. Restaurant owns payment accounting and table clearing.
5. **Waiting and abandonment.** For each queued or unserved seated Customer, call loseSatisfaction(waitingPenalty()). At zero, transition to LEFT, cancel any unserved order, remove from queue or clear its table, and count one abandonment. EATING, READY_TO_PAY, PAID and LEFT customers lose nothing.
6. **Result.** Show the end-of-turn values. Always finish all 18 turns; do not stop early after reaching a target. After turn 18 ask Restaurant for GameResult and display it.

The turn-16 arrival cannot pay by turn 18. Verify this after implementation; no special-case rule should artificially exclude that customer.

## Module handoffs for parallel work

### Mingchen - customer behaviour

Own Customer, RegularCustomer and CustomerState. Implement customer-owned food selection and document its deterministic rule. Keep state/satisfaction guards in Customer. Work against Menu.getItems() and Order.getStatus(); do not implement menu, preparation or restaurant scoring.

CustomerState has exactly WAITING, SEATED, EATING, READY_TO_PAY, PAID and LEFT. Taking an order leaves a customer SEATED. markServed(turn) starts EATING only after the order is SERVED. Customer never prints, updates restaurant revenue or removes itself from a table.

Check waiting penalty 8 through a Customer reference, satisfaction clamping, invalid transitions, one order per customer, and serving/eating/payment timing.

### Karthik - menu and orders

Own MenuItem, Menu, Order and OrderStatus. MenuItem is immutable dish data. Menu owns exactly Salad (900/1), Burger (1500/1) and Pasta (1800/2). No dish subclasses. PLACED is the initial order status; the five values are PLACED, READY, SERVED, PAID and CANCELLED.

Only Order owns preparation arithmetic and order-status guards. Order knows its chosen item and captured price; it does not know a Table. Check one/two-unit completion, rejecting preparation after READY, rejecting payment before SERVED, cancellation and locked pricing.

### Qiyun - restaurant, storage and result

Own Restaurant, Table, WaitingQueue and GameResult. Implement ownership, queue order, read-only access, eligibility lookup, paid-only accounting and abandonment cleanup. Delegate customer/order state changes to their public methods.

recordPayment(customer, turn) owns the payment transaction: validate eligibility and no duplicate payment, mark Order PAID then Customer PAID, record locked cents and a satisfaction snapshot, increment servedCount and clear the table. Cashier must not duplicate this arithmetic.

Implement the average using snapshots from payment time. Victory is a conjunction: servedCount >= 4, revenueCents >= 4500 and average >= 60. No average means defeat. Check table capacity, queue removal, duplicate payment, abandonment, zero payments and each exact threshold.

### Yuma - staff

Own Staff, Waiter, Chef and Cashier. Their package is restaurant.domain because the report labels staff as entities.

Waiter coordinates domain methods for seating, taking orders and serving. Chef delegates one unit to Order. Cashier validates the oldest eligible customer and delegates accounting to Restaurant.recordPayment(). All roles share Staff's action flag. Each role offers only its own actions; do not switch on staff type.

Check a successful action consumes the turn, a second action fails, an invalid attempt consumes nothing, resets restore availability, and each action updates its collaborators once.

### Pluem - controller and UI

Own GameMgr and ConsoleUI. All method signatures are present so orchestration and display can be written before the other modules are complete. Use local fixtures/test doubles for unfinished collaborators.

GameMgr.main() is the entry point; do not add Main, Action, Request, TurnEngine or GameConfig. Keep price constants, satisfaction arithmetic, eligibility guards and victory calculations out of GameMgr and ConsoleUI. Display model values and call staff/domain operations.

Check input retries, staff resets, six arrival turns, exactly 18 turns, Waiter-before-Chef ordering, automatic payment, result display and no console access outside ConsoleUI.

## Integration checklist - after implementing TODOs

- Compile all 17 types with JDK 17+; agree API changes across owners before committing them.
- Reproduce Pasta arrival/seat on turn 1, order/unit 1 on turn 2, unit 2 on turn 3, serve on turn 4, READY_TO_PAY on turn 5 and payment on turn 6; satisfaction is 76 at payment.
- Verify a complete winning run and a complete losing run. Record actual input and output then; no old transcript is retained as evidence.
- Verify abandoned/unpaid orders contribute no revenue, invalid actions are retryable, and the final arrival cannot pay in time.
- Produce a class diagram from the implemented design and verify E1, P1 and C1 against the code.
