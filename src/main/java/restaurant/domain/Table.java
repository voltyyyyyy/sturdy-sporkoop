package restaurant.domain;

/**
 * Stage 1 prototype. Owner: Qiyun.
 * See docs/stage-1-contracts.md before implementing TODO methods.
 */
public final class Table {
    private final int tableId;
    // Single association endpoint implementing Table -> Customer.
    // Do not add a second customer-id/occupant field for the same relationship.
    private Customer occupant;

    // Package-private construction: Restaurant creates its own two tables.
    Table(int tableId) {
        this.tableId = tableId;
    }

    public int getTableId() { return tableId; }
    public Customer getOccupant() { return occupant; }
    public boolean isFree() { return occupant == null; }

    /** Store one occupant only; reject an occupied table. Waiter handles markSeated(). */
    public void seat(Customer customer) {
        if (customer == null){
            throw new IllegalArgumentException("Cannot seat a null customer");
        }
        if(!isFree()){
            throw new IllegalStateException("Table" + tableId + " is already occupied");
        }
        occupant = customer;
    }

    /** Clear only after this occupant has become PAID or LEFT. */
    public void clear() {
        if (occupant == null){
            return;
        }
        CustomerState state = occupant.getState();
        if (state != CustomerState.PAID && state != CustomerState.LEFT){
            throw new IllegalStateException("Table" + tableId + " cannot be cleared while the customer is " + state);
        }

        occupant = null;
    }
}
