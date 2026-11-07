package restaurant.domain;

public final class CriticCustomer extends Customer {
    public CriticCustomer(int customerId, int arrivalTurn) {
        super(customerId, arrivalTurn);
    }

    @Override
    public int waitingPenalty() {
        return 12;
    }

    @Override
    public MenuItem chooseFood(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu missing");
        }
        MenuItem best = menu.getItems().get(0);
        for (MenuItem item : menu.getItems()) {
            if (item.getPriceCents() > best.getPriceCents()) {
                best = item;
            }
        }
        MenuItem priciest = best;
        MenuItem secondPriciest = null;
        for (MenuItem item : menu.getItems()) {
            if (item != priciest
                    && (secondPriciest == null || item.getPriceCents() > secondPriciest.getPriceCents())) {
                secondPriciest = item;
            }
        }
        MenuItem combo = new ComboMeal(priciest, secondPriciest);
        return combo.getPriceCents() > best.getPriceCents() ? combo : best;
    }

    @Override
    public String typeLabel() {
        return "Critic";
    }

    @Override
    public long discountedPrice(long priceCents) {
        if (priceCents < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        return priceCents;
    }
}
