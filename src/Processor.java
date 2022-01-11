package src;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for reading data from daily.txt and
 * storing information in database.txt.
 */
public class Processor {

    final String dailyFileName;
    final String DATABASE_FILENAME = "database.txt";

    Store store;
    boolean auctionSale;

    final String USER_FORMAT = "(.{15}) (AA|BS|SS|FS) ([0-9]{6}\\.[0-9]{2})";

    // order -> standard, refund, sell, buy, auction, remove, gift
    String[] TRANSACTIONS_REGEX_PATTERNS = {
            "(00|02|06|07|10) (.{15}) (AA|FS|BS|SS) ([0-9]{6}\\.[0-9]{2})",
            "(01) (.{15,}) (AA|FS|BS|SS) (-?[0-9]{6,}\\.[0-9]{2})", "(05) (.{15}) (.{15}) (-?[0-9]{6,}\\.[0-9]{2})",
            "(03) (.{25,}) (.{15}) (-?[0-9]{2,}\\.[0-9]{2}) (-?[0-9]{3,}\\.[0-9]{2})",
            "(04) (.{25}) (.{15}) (.{1,15})",
            "(08|09) (.{25}) (.{15}) (.{1,15})",
            "(08) (.{25}) (.{15}) (.{1,15})?"
    };

    // order -> single, >1 or empty
    String[] GAME_LIST_REGEX_PATTERNS = {
            "\\[(.{25}), ([0-9]{3}\\.[0-9]{2}), ([0-9]{2}\\.[0-9]{2}), (b|s)\\]",
            "\\[\\]|\\[(\\[(.{25}), ([0-9]{3}\\.[0-9]{2}), ([0-9]{2}\\.[0-9]{2}), (b|s)\\], )*(\\[(.{25}), " +
                    "([0-9]{3}\\.[0-9]{2}), ([0-9]{2}\\.[0-9]{2}), (b|s)\\])\\]"
    };

    Pattern p;
    Matcher m;

    /**
     * Process the file passed in for reading.
     *
     * @param daily   filename for the daily transaction file
     */
    public Processor(String daily) { this.dailyFileName = daily; }

    /**
     * Reads the entire database and store each person and their
     * information separated by whitespaces.
     */
    public void readDatabase() {
        int userInformationStart = 0;
        int userInformationEnd = 28;
        int userGameListStart = 29;

        try {
            File data = new File(DATABASE_FILENAME);
            Scanner dataReader = new Scanner(data);

            String auctionStatus = dataReader.nextLine();
            checkAuctionStatus(auctionStatus);
            store = new Store(auctionSale);

            while (dataReader.hasNextLine()) {
                String line = dataReader.nextLine();

                if (line.length() < (userInformationEnd+2)) {
                    System.out.println("ERROR: \\<Fatal error: Invalid line: " + line + ". Unexpected length for line " +
                            "with user info.\\>");
                }
                User user = createDatabaseAccount(line.substring(userInformationStart, userInformationEnd));
                splitGame(user, line.substring(userGameListStart));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create users as information from database if retrieved.
     *
     * @param userInformation   line containing user info
     * @return  user from database
     */
    public User createDatabaseAccount(String userInformation) {
        Pattern p = Pattern.compile(USER_FORMAT);
        Matcher m = p.matcher(userInformation);

        if (!m.matches()) {
            System.out.println("ERROR: \\<Fatal error: User data with wrong format (" + userInformation +
                    " is not a valid user information string.)\\>");
            return null;
        }

        float userBalance = Float.parseFloat(m.group(3));
        if (userBalance > User.MAX_CREDIT) {
            userBalance = User.MAX_CREDIT;
            System.out.println("Max credit reached! New account balance is $" + User.MAX_CREDIT);
        }
        String userName = m.group(1);
        if (userName.equals(String.format("%-15s", ""))){
            System.out.println("ERROR: \\<Failed constraint error: User name can not be only whitespaces (" +
                    userInformation + " is not a valid user information string.)\\>");
            return null;
        }
        if (store.findUser(userInformation) != null) {
            System.out.println("ERROR: \\<Failed constraint error: A user in the database already has the name " +
                    userName + ".\\> \n \tContinuing...");
            return null;
        }
        String userType = m.group(2);
        User newUser = new User(userName, userType, userBalance);
        store.usersInDatabase.add(newUser);
        return newUser;
    }

    /**
     * Checking if the auction sale is on.
     *
     * @param status The string representing Auction Sale status, 0: off, 1: on
     */
    private void checkAuctionStatus(String status) {
        if (status.equals("0")) {
            auctionSale = false;
        }
        else if (status.equals("1")) {
            auctionSale = true;
        }
        else {
            System.out.println("ERROR: \\<Fatal error: AuctionSale status with wrong format ("
                    + status + " is not a valid AuctionSale status string.)\\>");
        }
    }

    /**
     * Read the game list String of the User and store the
     * game in the correct format in the User.
     *
     * @param u the User who has this game list String
     * @param gameListString the game list of the user in String
     */
    public void splitGame(User u, String gameListString) {
        int index = 1;
        int indexDiff = 45;
        int toNextIndex = 47;

        Pattern p = Pattern.compile(GAME_LIST_REGEX_PATTERNS[1]);
        Matcher m = p.matcher(gameListString);

        if (u == null) {
            System.out.println("ERROR: \\<Fatal error: Invalid User(null is not a valid User)\\>");
        }
        else if (!m.matches()) {
            System.out.println("ERROR: \\<Fatal error: Invalid portions encountered while processing the game list: "
                    + gameListString + ".\\>");
        }
        else {
            while ((index + indexDiff) <= gameListString.length()) {
                ArrayList<String> game = splitGameInformation(gameListString.substring(index, index + indexDiff));
                index += toNextIndex;
                if (game.isEmpty()) {
                    return;
                }
                u.addGame(game);
            }
        }
    }

    /**
     * Transfer the game information string to a corresponding
     * string array list representing this game.
     *
     * @param gameString the string format of game information
     * @return the array list format of the game
     */
    public ArrayList<String> splitGameInformation(String gameString) {
        Pattern p = Pattern.compile(GAME_LIST_REGEX_PATTERNS[0]);
        Matcher m = p.matcher(gameString);
        ArrayList<String> gameInformation = new ArrayList<>();
        if (!m.matches()){
            System.out.println(gameString);
            System.out.println("ERROR: \\<Fatal error: Invalid portions encountered while processing the game list: "
                    + gameString + ".\\>");
        }
        for (int i = 1; i <= m.groupCount(); i++) {
            gameInformation.add(m.group(i));
        }
        return gameInformation;
    }

    /**
     * Check each line from daily to see if this is
     * a valid transaction based on its format.
     *
     * @param line  line containing the transaction
     */
    public void processLine(String line) {
        CommandFactory cf = new CommandFactory(store);
        Button controlButton = new Button();
        boolean found = false;

        for (String pattern : TRANSACTIONS_REGEX_PATTERNS) {
            p = Pattern.compile(pattern);
            m = p.matcher(line);

            if (m.matches()) {
                found = true;
                if (m.groupCount() == 3) {
                    controlButton.setCommand(cf.createCommand(m.group(0), m.group(1), m.group(2), m.group(3)));
                }
                else if (m.groupCount() == 4) {
                    controlButton.setCommand(cf.createCommand(m.group(0), m.group(1), m.group(2), m.group(3),
                            m.group(4)));
                }
                else if (m.groupCount() == 5) {
                    controlButton.setCommand(cf.createCommand(m.group(0), m.group(1), m.group(2), m.group(3),
                            m.group(4), m.group(5)));
                }
                controlButton.press();
                break;
            }
        }
        if (!found) {
            System.out.println("ERROR: \\<Fatal error: Invalid portions encountered while processing transaction: "
                    + line + ".\\>");
        }
    }

    /**
     * Read daily text file and run all the commands.
     */
    public void readDaily() {
        try {
            File daily = new File(dailyFileName);
            Scanner dailyReader = new Scanner(daily);

            System.out.println("\tNew day! Processing...");

            while (dailyReader.hasNextLine()) {
                processLine(dailyReader.nextLine());
            }
            if (store.getCurrentUser() != null) {
                System.out.println("ERROR: \\<Fatal error: The user " + store.getCurrentUser().getUserName() +
                        " haven't logged out at the end of the day.\\>");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * A method that help to set up default auction sale status
     * and the existing admin before running our code.
     *
     * @param admins the existing admins of our system.
     */
    public void makeExistingAdmin(ArrayList<User> admins) {
        try {
            FileWriter myWriter = new FileWriter("database.txt",false);

            // write the auction sale status
            myWriter.write("0\n");
            for (User admin : admins) {
                String toWrite = String.format("%s %s %s %s\n", admin.getUserName(), admin.getUserType(),
                        admin.getBalanceAtTheEnd(), admin.getGames());
                myWriter.write(toWrite);
                myWriter.flush();
            }
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the users to the database text file.
     */
    public void write() {
        try {
            FileWriter myWriter = new FileWriter("database.txt",false);

            // write the auction sale status
            if (store.auctionSale) { myWriter.write("1\n"); }
            else { myWriter.write("0\n"); }

            if (store.getCurrentUser() != null) {
                System.out.println("ERROR: \\<Failed constraint error: A user" + store.getCurrentUser().getUserName()
                        + "is still logged in at the end of a day.\\> \n \tContinuing...");
            }

            // write all users except the last one
            for (User u : store.getUsersInDatabase()) {
                u.changeSymbolAtTheEnd();
                String toWrite = String.format("%s %s %s %s\n", u.getUserName(), u.getUserType(), u.getBalanceAtTheEnd(),
                        u.getGames());
                myWriter.write(toWrite);
                myWriter.flush();
            }

            myWriter.close();
        } catch (IOException e) {
            System.out.println("ERROR: \\<Fatal error: Error on writing to the database text file.\\>");
            e.printStackTrace();
        }
    }

}