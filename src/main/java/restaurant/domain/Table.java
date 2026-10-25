package restaurant.domain;

import java.util.Objects;

/** A table that can hold at most one customer. */
public final class Table {

    private final int tableId;
    private Customer occupant;

    public Table(int tableId) {
        if (tableId <= 0) {
            throw new IllegalArgumentException("Table id must be positive.");
        }
        this.tableId = tableId;
    }

    public boolean isFree() {
        return occupant == null;
    }

    public void seat(Customer customer) {
        if (!isFree()) {
            throw new IllegalActionException("Table " + tableId + " is already occupied.");
        }
        occupant = Objects.requireNonNull(customer, "customer");
    }

    public void vacate() {
        occupant = null;
    }

    public int getTableId() {
        return tableId;
    }

    /** Returns the occupant, or null when the table is free. */
    public Customer getOccupant() {
        return occupant;
    }
}
