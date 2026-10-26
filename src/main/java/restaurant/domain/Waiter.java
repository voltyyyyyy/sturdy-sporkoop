package restaurant.domain;




public final class Waiter extends Staff {
    public Waiter(String name) {
        super(name);
    }





    public void seat(Restaurant restaurant, Customer customer, Table table) {
        throw new UnsupportedOperationException("TODO: Yuma - seating action");
    }





    public Order takeOrder(Restaurant restaurant, Customer customer) {
        throw new UnsupportedOperationException("TODO: Yuma - order-taking action");
    }




    public void serve(Restaurant restaurant, Customer customer, int turn) {
        throw new UnsupportedOperationException("TODO: Yuma - serving action");
    }
}
