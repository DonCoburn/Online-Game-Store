package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command logs an existing user out of the store.
 */
public class LogoutCommand implements Command {

    Store store;
    User logoutUser;
    String transaction;

    /**
     * Constructs a logout command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public LogoutCommand(Store s, Object ... args) {
        store = s;
        logoutUser = (User) args[0];
        transaction = (String) args[1];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() {
        System.out.println(store.logout(logoutUser, transaction));
    }
}