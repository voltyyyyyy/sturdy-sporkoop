public class Order {
    private final MenuItem item;
    private final long priceCents;
    private OrderStatus status = OrderStatus.ORDERED;
    private int unitsDone;

    public Order(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order requires a menu item");
        }
        this.item = item;
        this.priceCents = item.priceCents();
    }

    public MenuItem item() {
        return item;
    }

    public long priceCents() {
        return priceCents;
    }

    public OrderStatus status() {
        return status;
    }

    public int unitsDone() {
        return unitsDone;
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    public boolean advanceOneUnit() {
        if (status != OrderStatus.ORDERED) {
            return false;
        }
        if (unitsDone >= item.prepUnits()) {
            return false;
        }
        unitsDone++;
        if (unitsDone == item.prepUnits()) {
            status = OrderStatus.READY;
        }
        return true;
    }

    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot serve an order that is " + status);
        }
        status = OrderStatus.SERVED;
    }

    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalStateException("Cannot pay an order that is " + status);
        }
        status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status != OrderStatus.ORDERED && status != OrderStatus.READY) {
            throw new IllegalStateException("Cannot cancel an order that is " + status);
        }
        status = OrderStatus.CANCELLED;
    }
}
