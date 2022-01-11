package src;

import java.util.ArrayList;

/**
 * User class that defines a user with a name, type and balance.
 * A user can perform various actions in the store depending on their type.
 */
public class User {

    public static float MAX_CREDIT = 999999.00f;
    public final float MAX_AMT = 1000.00f;
    public float CREDIT_TRACKER = MAX_AMT;

    String userName, userType;
    float balance;

    ArrayList<ArrayList<String>> games = new ArrayList<>();

    /**
     * Constructs a user who has a name, type and balance.
     *
     * @param name      name of the user
     * @param type      type of the user
     * @param balance   the amount of credit this user has
     */
    public User(String name, String type, float balance) {
        this.userName = String.format("%-15s", name);
        this.userType = type;
        this.balance = balance;
    }

    /**
     * Returns the name of the user.
     *
     * @return username
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Returns the type of the user.
     *
     * @return usertype
     */
    public String getUserType() { return this.userType; }

    /**
     * Returns the balance on the user's account
     *
     * @return user credit balance
     */
    public float getBalance() {return this.balance; }

    /**
     * Returns all the games the user has in their inventory.
     *
     * @return an array of the user's games
     */
    public ArrayList<ArrayList<String>> getGames() {
        return games;
    }

    /**
     * Add credit to the user's account if the amount is valid
     * and the balance isn't at max.
     */
    // since addCredit is required by a daily amount
    public void addToBalance(float amount) {
        this.balance += amount;
        this.CREDIT_TRACKER -= amount;
    }

    /**
     * Returns an array containing a game and it's info specified by the given
     * game name. Returns null if the game doesn't exist in the user's inventory.
     *
     * @param game  name of the game
     * @return  array with game or null
     */
    public ArrayList<String> getGame(String game) {
        for (ArrayList<String> gameInfo : games) {
            if (gameInfo.contains(game))
                return gameInfo;
        }
        return null;
    }

    /**
     * Allows this user to transfer credit or pay an amount to another user.
     *
     * @param amount    amount ot be transferred
     */
    public void pay(float amount, User other) {
        this.balance -= amount;
        other.balance += amount;
    }

    /**
     * Updates the user inventory with a new game and it's info like price etc.
     *
     * @param game  array containing game and it's info
     */
    public void addGame(ArrayList<String> game) {
        games.add(game);
    }

    /**
     * Update the status of the games in the user's inventory at the end of the day.
     */
    public void changeSymbolAtTheEnd() {
        for (ArrayList<String> game : games) {
            if (game.get(3).equals("st")) {
                game.set(3, "s");
            } 
            else if (game.get(3).equals("bt")) {
                game.set(3, "b");
            }
        }
    }

    /**
     * Pads the user's balance with zeros to the
     * left if all the spaces for digits are not used.
     *
     * @return  user's balance padded with zeros fit for writing
     */
    public String getBalanceAtTheEnd() {
        String accountBalance = String.valueOf(this.balance);
        String firstPart = accountBalance.split("\\.")[0];
        String lastPart = accountBalance.split("\\.")[1];

        firstPart = "0".repeat(6 - firstPart.length()) + firstPart;
        lastPart = lastPart + "0".repeat(2 - lastPart.length());
        return firstPart + "." + lastPart;
    }

}