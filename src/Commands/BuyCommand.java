package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that allows buyers to buy games.
 */
public class BuyCommand implements Command {

    Store store;
    User buyer;
    User seller;
    String game;
    String transaction;

    /**
     * Constructs a buy command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public BuyCommand(Store s, Object ... args) {
        store = s;
        game = (String) args[0];
        transaction = (String) args[1];
        seller = (User) args[2];
        buyer = (User) args[3];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() {
        System.out.println(store.buy(game, transaction, seller, buyer));
    }
}
