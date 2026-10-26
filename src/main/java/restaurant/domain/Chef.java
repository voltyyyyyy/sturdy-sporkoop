package restaurant.domain;




public final class Chef extends Staff {
    public Chef(String name) {
        super(name);
    }

    public void prepare(Order order) {
        throw new UnsupportedOperationException("TODO: Yuma - delegate one preparation unit to Order");
    }
}
