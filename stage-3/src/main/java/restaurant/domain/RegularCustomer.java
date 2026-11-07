package restaurant.domain;

public final class RegularCustomer extends Customer {
    public RegularCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 8;
    }

    @Override
    public String typeLabel() {
        return "Regular";
    }

    @Override
    public MenuItem chooseFood(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu missing");
        }
        MenuItem cheapest = null;
        MenuItem secondCheapest = null;
        for (MenuItem item : menu.getItems()) {
            if (cheapest == null || item.getPriceCents() < cheapest.getPriceCents()) {
                secondCheapest = cheapest;
                cheapest = item;
            } else if (secondCheapest == null || item.getPriceCents() < secondCheapest.getPriceCents()) {
                secondCheapest = item;
            }
        }
        return new ComboMeal(cheapest, secondCheapest);
    }

    @Override
    public long discountedPrice(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return priceCents;
    }
}
