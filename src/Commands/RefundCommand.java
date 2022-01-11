package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that process a refund between a buyer and a seller.
 */
public class RefundCommand implements Command {

    Store store;
    User buyer;
    User seller;
    float amount;
    String transaction;

    /**
     * Constructs an refund command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public RefundCommand(Store s, Object ... args) {
        store = s;
        buyer = (User) args[0];
        seller = (User) args[1];
        amount = (float) args[2];
        transaction = (String) args[3];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.refund(buyer, seller, amount, transaction)); }
}
