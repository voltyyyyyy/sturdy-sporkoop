package restaurant.domain;




public final class Cashier extends Staff {
    public Cashier(String name) {
        super(name);
    }





    public void collect(Restaurant restaurant, Customer customer, int turn) {
        throw new UnsupportedOperationException("TODO: Yuma - collect one eligible payment");
    }
}
