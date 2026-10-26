package restaurant.domain;




public final class Table {
    private final int tableId;
    private Customer occupant;

    Table(int tableId) {
        this.tableId = tableId;
    }

    public int getTableId() { return tableId; }
    public Customer getOccupant() { return occupant; }
    public boolean isFree() { return occupant == null; }

    public void seat(Customer customer) {
        throw new UnsupportedOperationException("TODO: Qiyun - exclusive table occupancy");
    }

    public void clear() {
        throw new UnsupportedOperationException("TODO: Qiyun - guarded table clearing");
    }
}
