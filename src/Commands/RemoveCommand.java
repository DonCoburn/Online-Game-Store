package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that removes a game from a user's inventory.
 */
public class RemoveCommand implements Command {

    Store store;
    User admin;
    User u;
    String game;
    String transaction;

    /**
     * Constructs a remove game command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public RemoveCommand(Store s, Object ... args) {
        store = s;
        admin = (User) args[0];
        u = (User) args[1];
        game = (String) args[2];
        transaction = (String) args[3];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.removeGame(game, admin, u, transaction)); }
}
