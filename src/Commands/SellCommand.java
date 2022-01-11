package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that allows a seller to put a game up for sale.
 */
public class SellCommand implements Command {

    Store store;
    User seller;
    String game;
    String price;
    String discount;
    String transaction;

    /**
     * Constructs a sell command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public SellCommand(Store s, Object ... args) {
        store = s;
        game = (String) args[0];
        seller = (User) args[1];
        price = (String) args[2];
        discount = (String) args[3];
        transaction = (String) args[4];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.sell(game, seller, price, discount, transaction)); }
}
