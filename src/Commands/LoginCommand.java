package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command logs an existing user in the store.
 */
public class LoginCommand implements Command {

    Store store;
    User user;
    String transaction;

    /**
     * Constructs a login command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public LoginCommand(Store s, Object... args) {
        store = s;
        user = (User) args[0];
        transaction = (String) args[1];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() {
        System.out.println(store.login(user, transaction));
    }

}