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
        if (customer == null){
            throw new IllegalArgumentException("Cannot seat a null customer");
        }
        if(!isFree()){
            throw new IllegalStateException("Table" + tableId + " is already occupied");
        }
        occupant = customer;
    }

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
