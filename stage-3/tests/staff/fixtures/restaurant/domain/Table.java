package restaurant.domain;

public final class Table {
    private Customer occupant;
    int seatCalls;
    public Customer getOccupant() { return occupant; }
    public boolean isFree() { return occupant == null; }
    public void seat(Customer customer) {
        if (!isFree()) { throw new IllegalStateException("Occupied"); }
        seatCalls++;
        occupant = customer;
    }
}
