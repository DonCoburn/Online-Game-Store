package src.Commands;

import src.Command;
import src.Store;
import src.User;

/**
 * Command that add credits to a user's account.
 */
public class AddCreditCommand implements Command {

    Store store;
    User u;
    float amount;
    String transaction;

    /**
     * Constructs an add-credit command.
     *
     * @param s     current store
     * @param args  parameters needed to process this command
     */
    public AddCreditCommand(Store s, Object ... args) {
        store = s;
        u = (User) args[0];
        amount = (float) args[1];
        transaction = (String) args[2];
    }

    /**
     * Perform the action specified by the command.
     */
    @Override
    public void execute() {
        System.out.println(store.addCredit(u, amount, transaction));
    }
}
