package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that allows one user to gift their game to another user.
 */
public class GiftCommand implements Command {

    Store store;
    User owner;
    User receiver;
    String game;
    String transaction;

    /**
     * Constructs a gift command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public GiftCommand(Store s, Object ... args) {
        store = s;
        game = (String) args[0];
        owner = (User) args[1];
        receiver = (User) args[2];
        transaction = (String) args[3];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.gift(game, owner, receiver, transaction)); }
}

