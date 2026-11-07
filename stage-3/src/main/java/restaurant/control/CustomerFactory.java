package restaurant.control;

import restaurant.domain.CriticCustomer;
import restaurant.domain.Customer;
import restaurant.domain.RegularCustomer;
import restaurant.domain.VIPCustomer;


public final class CustomerFactory {



    public Customer create(int customerId, int turn) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive");
        }
        if (turn < 1 || turn > 18) {
            throw new IllegalArgumentException("Turn must be between 1 and 18");
        }
        return switch (turn) {
            case 1, 7, 13, 16 -> new RegularCustomer(customerId, turn);
            case 4 -> new VIPCustomer(customerId, turn);
            case 10 -> new CriticCustomer(customerId, turn);
            default -> null;
        };
    }
}
