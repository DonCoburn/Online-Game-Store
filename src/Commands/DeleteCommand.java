package src.Commands;

import src.Command;
import src.Store;

/**
 * Command that deletes a user from the database.
 */
public class DeleteCommand implements Command {

    Store store;
    String userName;
    String transaction;

    /**
     * Constructs a delete command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public DeleteCommand(Store s, Object ... args) {
        store = s;
        userName = (String) args[0];
        transaction = (String) args[1];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.deleteAccount(userName, transaction)); }
}
