package src.Commands;

import src.Command;
import src.Store;

/**
 * Command that allows an admin to create a new user.
 */
public class CreateCommand implements Command {

    Store store;
    String userName;
    String userType;
    float amount;
    String transaction;

    /**
     * Constructs a create command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public CreateCommand(Store s, Object ... args) {
        store = s;
        userName = (String) args[0];
        userType = (String) args[1];
        amount = (float) args[2];
        transaction = (String) args[3];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() { System.out.println(store.createAccount(userName, userType, amount, transaction)); }
}
