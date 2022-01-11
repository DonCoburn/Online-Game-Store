package src;

import src.Commands.*;

/**
 * Factory that create the command specified for an action.
 */
public class CommandFactory {

    final String login = "00";
    final String create = "01";
    final String delete = "02";
    final String addCredit = "06";
    final String logout = "10";
    final String refund = "05";
    final String sell = "03";
    final String buy = "04";
    final String auction = "07";
    final String removeGame = "08";
    final String gift = "09";

    Store store;

    /**
     * Constructs a factory for all commands.
     *
     * @param s current store
     */
    public CommandFactory(Store s) { this.store = s; }

    /**
     * Returns the commands corresponding to transaction.
     *
     * @param args   line from daily.txt that contains a specific transaction
     * @return  command matching the transaction if the conditions are met
     */
    public Command createCommand(String ... args) {
        String typeCode = args[1];
        String transaction = args[0]; // entire group

        if (typeCode.equals(login)) {
            User user = store.findUser(args[2]);
            return new LoginCommand(store, user, transaction);
        }
        if (typeCode.equals(create)) {
            String user = args[2];
            String userType = args[3];
            float amount = Float.parseFloat(args[4]);
            return new CreateCommand(store, user, userType, amount, transaction);
        }
        if (typeCode.equals(delete)) {
            String user = args[2];
            return new DeleteCommand(store, user, transaction);
        }
        if (typeCode.equals(addCredit)) {
            User user = store.findUser(args[2]);
            float amount = Float.parseFloat(args[4]);
            return new AddCreditCommand(store, user, amount, transaction);
        }
        if (typeCode.equals(logout)) {
            User user = store.findUser(args[2]);
            return new LogoutCommand(store, user, transaction);
        }
        if (typeCode.equals(refund)) {
            User buyer = store.findUser(args[2]);
            User seller = store.findUser(args[3]);
            float amount = Float.parseFloat(args[4]);
            return new RefundCommand(store, buyer, seller, amount, transaction);
        }
        if (typeCode.equals(sell)) {
            String gameName = args[2];
            User seller = store.findUser(args[3]);
            String price = args[5];
            String discount = args[4];
            return new SellCommand(store, gameName, seller, price, discount, transaction);
        }
        if (typeCode.equals(buy)) {
            String gameName = args[2];
            String buyerName = String.format("%-15s", args[4]);
            User seller = store.findUser(args[3]);
            User buyer = store.findUser(buyerName);
            return new BuyCommand(store, gameName, transaction, seller, buyer);
        }
        if (typeCode.equals(auction)) {
            User user = store.findUser(args[2]);
            return new AuctionSaleCommand(store, user, transaction);
        }
        if (typeCode.equals(removeGame)) {
            User admin = null;
            String userName = "";

            if (args.length == 4) {
                userName = String.format("%-15s", args[3]);
            }
            else if (args.length == 5) {
                userName = String.format("%-15s", args[4]);
                admin = store.findUser(args[3]);
            }
            String gameName = String.format("%-25s", args[2]);
            User owner = store.findUser(userName);
            return new RemoveCommand(store, admin, owner, gameName, transaction);
        }
        if (typeCode.equals(gift)) {
            String gameName = args[2];
            User owner = store.findUser(args[3]);
            String receiverName = String.format("%-15s", args[4]);
            User receiver = store.findUser(receiverName);
            return new GiftCommand(store, gameName, owner, receiver, transaction);
        }
        System.out.println("ERROR: \\<Fatal error: Transaction String with wrong transaction code: " + transaction +
                ". This is not a valid transaction string.\\>");
        return null;
    }

}