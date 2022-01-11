package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that switches the auction-sale on or off.
 */
public class AuctionSaleCommand implements Command {
    Store store;
    User user;
    String transaction;

    /**
     * Constructs an auction-sale command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public AuctionSaleCommand(Store s, Object ... args) {
        store = s;
        user = (User) args[0];
        transaction = (String) args[1];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() {
        System.out.println(store.switchActionSale(user, transaction));
    }

}