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
        throw new UnsupportedOperationException("TODO: Qiyun - exclusive table occupancy");
    }

    /** Clear only after this occupant has become PAID or LEFT. */
    public void clear() {
        throw new UnsupportedOperationException("TODO: Qiyun - guarded table clearing");
    }
}
