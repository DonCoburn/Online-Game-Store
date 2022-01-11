package src;

import java.util.ArrayList;

/**
 * Main store that holds all the
 * actions users can perform.
 */
public class Store {

    final float MAX_DISCOUNT = 90.00f;
    final float MAX_PRICE = 999.99f;
    final int MAX_GAME_LENGTH = 25;
    final int MAX_USER_NAME_LENGTH = 15;

    public User currentUser;
    ArrayList<User> usersInDatabase = new ArrayList<>();
    boolean auctionSale;

    /**
     * Construct a store with all the actions
     * users are able to perform.
     *
     * @param actionSale indicates whether there's an auction-sale going or not
     */
    public Store(boolean actionSale) {
        this.auctionSale = actionSale;
    }

    /**
     * A getter to get the current login user
     *
     * @return the user who's currently logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Allows a user to login to the system and make transactions.
     *
     * @param u           user who wants to login
     * @param transaction transaction line from daily.txt
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String login(User u, String transaction) {
        if (currentUser != null) {
            return ("ERROR: \\<Fatal error on this transaction: " + transaction + ". There is some other " +
                    "user login already.\\> \n \tContinuing...");
        }
        if (u == null) {
            return ("ERROR: \\<Failed constraint error on this transaction: " + transaction + ". The user who wants " +
                    "to login is not in the database.\\> \n \tContinuing...");
        }
        currentUser = u;
        return ("Transaction successfully! " + u.getUserName() + " was logged in. \n \tContinuing...");
    }

    /**
     * Allows a user to logout of the system.
     *
     * @param u           user who wants to logout
     * @param transaction transaction line from daily.txt
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String logout(User u, String transaction) {
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error on this transaction: " + transaction + ". There is some other " +
                    "user login already.\\> \n \tContinuing...");
        }
        if (u == null) {
            return ("ERROR: \\<Failed constraint error on this transaction: " + transaction + ". The user who wants " +
                    "to login is not in the database.\\> \n \tContinuing...");
        }
        if (!currentUser.getUserName().equals(u.getUserName())) {
            return "ERROR: \\<Failed constraint error: User: " + u.getUserName() + " is not logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...";
        }
        currentUser = null;
        return ("Transaction successfully! " + u.getUserName() + " was logged out. \n \tContinuing...");
    }

    /**
     * Returns all the users who already exist in the database.
     *
     * @return an array of all the users from database.
     */
    public ArrayList<User> getUsersInDatabase() {
        return usersInDatabase;
    }

    /**
     * Toggle auction sale on or off to apply discounts
     * when an admin user specifies this action.
     */
    public String switchActionSale(User user, String transaction) {
        if (currentUser == null) {
            return "ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...";
        }
        if (!user.getUserType().equals("AA")) {
            return ("ERROR: \\<Failed constraint error on this transaction: " + transaction + ". The user cannot" +
                    "switch auction sale status because they are not an admin.\\> \n \tContinuing...");
        }
        if (!user.getUserName().equals(currentUser.getUserName())) {
            return ("ERROR: \\<Failed constraint error on this transaction: " + transaction + ". The user cannot" +
                    "switch auction sale status because they are not currently logged in.\\> \n \tContinuing...");
        }
        this.auctionSale = !this.auctionSale;
        return ("Transaction successfully! Auction sale is now (true = on, false = off): " + auctionSale + " \n " +
                "\tContinuing...");
    }

    /**
     * Find and return user by given name if
     * they already exist in the database.
     *
     * @param name name of the user
     * @return the user if they exists in the database, null otherwise.
     */
    public User findUser(String name) {
        for (User u : usersInDatabase) {
            if (u.getUserName().equals(name))
                return u;
        }
        return null;
    }

    /**
     * Add credit to user's balance if the
     * amount is less than the daily max.
     *
     * @param u           user who is adding credit
     * @param amount      credit amount to be added
     * @param transaction transaction line from daily.txt
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String addCredit(User u, float amount, String transaction) {
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (currentUser != u) {
            return ("ERROR: \\<Failed fatal error: User: " + u.getUserName() + " is not logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (u.CREDIT_TRACKER < amount) {
            return ("ERROR: \\<Failed constraint error on this transaction: You can only add up to $ " + u.MAX_AMT +
                    " per day. Error on transaction: " + transaction + "\\> \n \tContinuing...");
        }
        if ((u.getBalance() + amount) > User.MAX_CREDIT) {
            float added = User.MAX_CREDIT - u.getBalance();
            u.addToBalance(added);
            return ("Max credit reached! Only " + added + " was added to the User " + u.getUserName() + "'s account");
        }
        u.addToBalance(amount);
        return ("$" + amount + " has been added to " + u.getUserName() + "'s account! New balance is now $"
                + u.getBalance() + ". \n \tContinuing...");
    }

    /**
     * Helper method to buy a game.
     *
     * @param game        name of the game being bought
     * @param transaction transaction line from daily.txt
     * @param seller      user who is selling the game
     * @param buyer       user who is buying the game
     * @return true if the buy was successfull, false otherwise
     */
    private boolean buyGame(User buyer, User seller, String game, String transaction, ArrayList<String> gameInfo) {
        float price = Float.parseFloat(gameInfo.get(1));
        // apply discount
        if (this.auctionSale) {
            price = (float) Math.floor(price * (100.00 - Float.parseFloat(gameInfo.get(2)))) / 100.00f;
        }
        if (buyer.getBalance() < price) { return false; }
        ArrayList<String> newGame = new ArrayList<>(gameInfo); // copy game
        newGame.set(3, "bt"); // type change: s -> bt
        buyer.addGame(newGame);
        buyer.pay(price, seller);
        return true;
    }

    /**
     * Allows a buyer to buy a game from a seller if the seller has
     * the game up for sale.
     *
     * @param game        name of the game being bought
     * @param transaction transaction line from daily.txt
     * @param seller      user who is selling the game
     * @param buyer       user who is buying the game
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String buy(String game, String transaction, User seller, User buyer) {
        if (seller == null || buyer == null) {
            return ("ERROR: \\<Failed constraint error: Seller or buyer is not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (currentUser != buyer) {
            return "ERROR: \\<Failed fatal error: User: " + buyer.getUserName() + " is not logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...";
        }
        if (buyer.getGame(game) != null) {
            return ("ERROR: \\<Failed constraint error: Buyer " + buyer.getUserName() + " already have the game " +
                    game + ". Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }

        ArrayList<String> gameInfo = seller.getGame(game);
        if (gameInfo == null || gameInfo.get(3).equals("b") || gameInfo.get(3).equals("bt")) {
            return ("ERROR: \\<Failed constraint error: " + game + " is not up for sale. " + "Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo.get(3).equals("st")) {
            return ("ERROR: \\<Failed constraint error: " + game + " is put up for sale on the same day. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }

        if (buyGame(buyer, seller, game, transaction, gameInfo)) {
            return ("Transaction successfully! " + game + " was added to " + buyer.getUserName() +
                    "'s inventory. \n \tContinuing...");
        }
        return ("ERROR: \\<Failed constraint error: Insufficient balance to purchase " + game + " on this " +
                "transaction: " + transaction + ".\\> \n \tContinuing...");
    }

    /**
     * Allows a seller to put up game for sale.
     *
     * @param game        name of the game being put up for sale
     * @param transaction transaction line from daily.txt
     * @param seller      user who is putting the game up for sale
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String sell(String game, User seller, String price, String discount, String transaction) {
        if (seller == null) {
            return ("ERROR: \\<Failed constraint error: Seller or buyer is not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        ArrayList<String> gameInfo = seller.getGame(game);
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (currentUser != seller) {
            return ("ERROR: \\<Failed fatal error: User: " + seller.getUserName() + " is not logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }

        // handle the case which game name has only whitespaces
        if (game.equals(String.format("%-25s", ""))) {
            return ("ERROR: \\<Failed constraint error: Game name cannot contain only whitespace. Error on transaction "
                    + transaction + ".\\> \n \tContinuing...");
        }
        if (game.length() > MAX_GAME_LENGTH) {
            return ("ERROR: \\<Failed constraint error: Game name is too long. Error on transaction " + transaction +
                    ".\\> \n \tContinuing...");
        }

        float gameDiscount = Float.parseFloat(discount);
        float gamePrice = Float.parseFloat(price);

        if (gamePrice < 0 || gamePrice > MAX_PRICE) {
            return ("ERROR: \\<Failed constraint error: Game price: " + price + " is invalid. Error on transaction "
                    + transaction + ".\\> \n \tContinuing...");
        }
        if (gameDiscount < 0 || gameDiscount > MAX_DISCOUNT) {
            return ("ERROR: \\<Failed constraint error: Game discount: " + discount + " is invalid. Error on transaction "
                    + transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo != null) {
            return ("ERROR: \\<Failed constraint error: Game is already sold or bought by the same user. " +
                    seller.getUserName() + ". Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }

        ArrayList<String> newGame = new ArrayList<>();
        newGame.add(game);
        newGame.add(price);
        newGame.add(discount);
        newGame.add("st");
        seller.addGame(newGame);
        return ("Transaction successful! " + game + " is being put out for sale by " + seller.getUserName() +
                ". \n \tContinuing...");
    }

    /**
     * Allows a user to gift a game to another user if the game exist in their
     * inventory. An admin can gift between users but a non-admin can only gift
     * from their inventory.
     *
     * @param game        name of the game being gifted
     * @param owner       the user gifting the game
     * @param receiver    the user receiving the game
     * @param transaction transaction line from daily.txt
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String gift(String game, User owner, User receiver, String transaction) {
        if (owner == null || receiver == null) {
            return ("ERROR: \\<Failed constraint error: Owner or receiver is not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        ArrayList<String> gameInfo = owner.getGame(game);
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (currentUser != owner && !currentUser.getUserType().equals("AA")) {
            return ("ERROR: \\<Failed fatal error: Appropriate user is not logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (receiver.getUserType().equals("SS")) {
            return ("ERROR: \\<Failed constraint error: Sellers cannot accept gifts. Error on transaction: " +
                    transaction + " is invalid.\\> \n \tContinuing...");
        }
        if (receiver.getGame(game) != null) {
            return ("ERROR: \\<Failed constraint error: Game: " + game + " already in receiver's inventory. Error on " +
                    "transaction " + transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo == null) {
            return ("ERROR: \\<Failed constraint error: Game: " + game + " is not in owner's inventory. " +
                    "Error on transaction " + transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo.get(3).equals("st") || gameInfo.get(3).equals("bt")) {
            return ("ERROR: \\<Failed constraint error: " + game + " was purchased or put up for sale today. " +
                    "Error on transaction: " + transaction + " is invalid.\\> \n \tContinuing...");
        }
        if (gameInfo.get(3).equals("b")) {
            owner.games.remove(gameInfo);
        }

        ArrayList<String> giftedGame = new ArrayList<>(gameInfo); // copy game
        giftedGame.set(3, "bt"); // type change: a gifted game is treated the same as one that was bought.
        receiver.addGame(giftedGame);
        return ("Transaction successful! " + game + " has been gifted to " + receiver.getUserName() + " by " +
                owner.getUserName() + ". \n \tContinuing...");
    }

    /**
     * Allows a user to remove a game from their inventory.
     *
     * @param game        name of the game being removed
     * @param gameOwner   the user who wants to remove the game
     * @param transaction transaction line from daily.txt
     * @return string indicating a successful transaction or error
     * message if conditions are not met.
     */
    public String removeGame(String game, User admin, User gameOwner, String transaction) {
        if (gameOwner == null) {
            return ("ERROR: \\<Failed constraint error: Owner or receiver is not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        ArrayList<String> gameInfo = gameOwner.getGame(game);
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (currentUser != gameOwner && currentUser != admin) {
            return ("ERROR: \\<Failed fatal error: Appropriate user is not logged in to remove a game. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo == null) {
            return ("ERROR: \\ <Failed constraint error: " + game + " is not in " + gameOwner.getUserName()
                    + "'s inventory. Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (gameInfo.get(3).equals("bt") || gameInfo.get(3).equals("st")) {
            return ("ERROR: \\ <Failed constraint error: " + game + " was purchased or put up for sale today by "
                    + gameOwner.getUserName() + ". Error on transaction: " + transaction + " is invalid.\\>" +
                    " \n \tContinuing...");
        }
        gameOwner.games.remove(gameInfo);
        return "Transaction successful! " + game + " has been removed from " + gameOwner.getUserName() +
                ". \n \tContinuing...";
    }

    /**
     * Allows a new user to be created by an admin.
     *
     * @param userName      name of the new user
     * @param userType      type of the new user
     * @param balance       new user's balance
     * @param transaction   transaction line from daily.txt
     * @return string indicating a successful transaction or error
     *          message if conditions are not met.
     */
    public String createAccount (String userName, String userType, float balance, String transaction) {
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (!currentUser.getUserType().equals("AA")) {
            return ("ERROR: \\<Failed constraint error: Only admins can create users. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (userName.equals(String.format("%-15s", ""))) {
            return ("ERROR: \\<Failed constraint error: User name can not be only whitespaces (" +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (userName.length() > MAX_USER_NAME_LENGTH) {
            return ("ERROR: \\<Failed constraint error: User name is too long (" +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (balance > User.MAX_CREDIT) {
            return ("ERROR: \\<Failed constraint error: Balance is greater than the maximum amount for a user. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (findUser(userName) != null) {
            return ("ERROR: \\<Failed constraint error: A user in the database already has the name " +
                    userName + ". Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        User user = new User(userName, userType, balance);
        usersInDatabase.add(user);
        return ("Successfully created new user: " + user.getUserName() + ". \n \tContinuing...");
    }

    /**
     * Allows an admin to process a refund from seller's account to buyer's.
     *
     * @param buyer         user who is receiving the refund
     * @param seller        user who is transferring the credit
     * @param credit        amount to be reimbursed
     * @param transaction   transaction line from daily.txt
     * @return string indicating a successful transaction or error
     *          message if conditions are not met.
     */
    public String refund (User buyer, User seller,float credit, String transaction){
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (buyer == null || seller == null) {
            return ("ERROR: \\<Failed constraint error: User not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (!currentUser.getUserType().equals("AA")) {
            return ("ERROR: \\<Failed constraint error: Only admins can process refunds. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (credit < 0) {
            return ("ERROR: \\<Failed constraint error: " + credit + " is not an invalid refund value. Error on " +
                    "transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (seller.getBalance() < credit) {
            return ("ERROR: \\<Failed constraint error: Insufficient balance in " + seller.getUserName() +
                    "'s account to do a refund. Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        seller.pay(credit, buyer);
        return ("Successfully processed refund for buyer " + buyer.getUserName() + ". \n \tContinuing...");
    }

    /**
     * Allows an admin to delete a user if
     * they exist but cannot delete themselves.
     *
     * @param userName      name of the user to delete
     * @param transaction   transaction line from daily.txt
     * @return string indicating a successful transaction or error
     *          message if conditions are not met.
     */
    public String deleteAccount (String userName, String transaction){
        User user = findUser(userName);
        if (currentUser == null) {
            return ("ERROR: \\<Fatal error: No User is currently logged in. " +
                    "Error on transaction: " + transaction + ".\\> \n \tContinuing...");
        }
        if (!currentUser.getUserType().equals("AA")) {
            return ("ERROR: \\<Failed constraint error: Only admins can delete users. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (userName.equals(currentUser.getUserName())) {
            return ("ERROR: \\<Failed constraint error: Admin cannot delete themselves. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        if (user == null) {
            return ("ERROR: \\<Failed constraint error: User not in database. Error on transaction: " +
                    transaction + ".\\> \n \tContinuing...");
        }
        usersInDatabase.remove(user);
        return ("Successfully deleted user: " + userName + ". \n \tContinuing...");
    }

}