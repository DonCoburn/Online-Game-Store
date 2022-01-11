package src.Test;

import src.Store;
import src.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestSuite {

    User fs, fs2, s, s2, b, b2, a;
    Store store;

    public ArrayList<ArrayList<String>> makeGames(){
        ArrayList<String> newGame01 = new ArrayList<>();
        newGame01.add("Call of Duty");
        newGame01.add("100.00");
        newGame01.add("50.00");
        newGame01.add("s");

        ArrayList<String> newGame02 = new ArrayList<>();
        newGame02.add("Fortnight");
        newGame02.add("99.00");
        newGame02.add("30.00");
        newGame02.add("s");

        ArrayList<String> newGame03 = new ArrayList<>();
        newGame03.add("fakegame1");
        newGame03.add("-99.00");
        newGame03.add("30.00");
        newGame03.add("s");

        ArrayList<String> newGame04 = new ArrayList<>();
        newGame04.add("fakegame2");
        newGame04.add("99.00");
        newGame04.add("-30.00");
        newGame04.add("s");

        ArrayList<ArrayList<String>> games = new ArrayList<>();
        games.add(newGame01);
        games.add(newGame02);
        games.add(newGame03);
        games.add(newGame04);

        return games;
    }

    @BeforeEach
    public void setUp(){
        store = new Store(false);

        fs = new User("gamba18@", "FS",  999999.00f);
        s = new User("GeorgeM||123|5", "SS",  1234.00f);
        b = new User("Arnold @#$%^&", "BS", 198.00f);
        a = new User("Admin225", "AA", 0.00f);

        fs2 = new User("John Jacobs 2267", "FS", 0.00f); // shouldn't work username too long
        b2 = new User("Arnold @#$%^", "BS", 999999999999999.99f); // shouldn't work balance too large
        s2 = new User("GeorgeM||123|5", "SS",  123.00f); // shouldn't word same name
    }

    @Test
    public void testCreateAccount(){
        ArrayList<User> database = new ArrayList<>();
        database.add(s);
        String transaction = "";

        store.currentUser = a;

        // test for proper input
        String msg = store.createAccount("gamba18@", "FS", 999999.00f, transaction);
        assertEquals("Successfully created new user: " + fs.getUserName() + ". \n \tContinuing...", msg);

        // test for improper username
        String msg2 = store.createAccount("John Jacobs 2267", "FS", 0.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error: User name is too long (" +
                "Error on transaction: " + transaction + ".\\> \n \tContinuing...", msg2);

        // test for balance too large
        String msg3 = store.createAccount("Arnold @#$%^", "BS", 999999999999999.99f,
                transaction);
        assertEquals("ERROR: \\<Failed constraint error: Balance is greater than the maximum amount for a " +
                "user. Error on transaction: " + transaction + ".\\> \n \tContinuing...", msg3);

        // test for username the same
        store.createAccount("GeorgeM||123|5t", "SS",  1234.00f, transaction);
        String msg5 = store.createAccount("GeorgeM||123|5t", "BS", 123.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error: A user in the database already has the name " +
                "GeorgeM||123|5t. Error on transaction: " + transaction + ".\\> \n \tContinuing...", msg5);
    }

    @Test
    public void testRefund() {
        // test buyer gets money back from seller
        String transaction = "";
        store.currentUser = a;
        store.refund(b, s, 40.00f, transaction);

        assertEquals(1194.00, s.getBalance());
        assertEquals(238.00, b.getBalance());

        // test seller not in database
        String msg = store.refund(b, null, 40.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error: User not in database. Error on transaction: " +
                transaction + ".\\> \n \tContinuing...", msg);

        // test buyer not in database
        String msg2 = store.refund(null, s, 40.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error: User not in database. Error on transaction: " +
                transaction + ".\\> \n \tContinuing...", msg);

        // test seller has insufficient funds
        String msg3 = store.refund(fs, s, 2000.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error: Insufficient balance in " + s.getUserName() +
                "'s account to do a refund. Error on transaction: " + transaction + ".\\> \n \tContinuing...", msg3);
    }

    @Test
    public void testDeleteAccount() {
        String transaction = "";
        store.currentUser = a;
        store.createAccount(fs.getUserName(), fs.getUserType(), 1234.00f, transaction);

        // test user successfully deleted
        String msg = store.deleteAccount(fs.getUserName(), transaction);
        assertEquals("Successfully deleted user: " + fs.getUserName() + ". \n \tContinuing...", msg);

        // test create a user with the name that was deleted
        String msg2 = store.deleteAccount(s.getUserName(), transaction);
        assertEquals("ERROR: \\<Failed constraint error: User not in database. Error on transaction: " +
                transaction + ".\\> \n \tContinuing...", msg2);
    }

    @Test
    public void testBuy() {
        String transaction = "";
        ArrayList<ArrayList<String>> games = makeGames();
        s.addGame(games.get(0));
        s.addGame(games.get(1));
        store.currentUser = b;

        // test buyer balance is subtracted, seller balance increased, seller game in inventory, game in inventory
        String msg01 = store.buy("Call of Duty", "", s, b);
        assertEquals("Transaction successfully! Call of Duty was added to " + b.getUserName() +
                "'s inventory. \n \tContinuing...", msg01);
        assertEquals(98.00, b.getBalance());
        assertEquals(1334.00, s.getBalance());

        // test buyer balance not enough
        String msg02 = store.buy("Fortnight", "", s, b);
        assertEquals("ERROR: \\<Failed constraint error: Insufficient balance to purchase " +
                "Fortnight" + " on this transaction: " + transaction + ".\\> \n \tContinuing...", msg02);

        // test seller doesn't have the game
        String msg03 = store.buy("FIFA21", "", s, b);
        assertEquals("ERROR: \\<Failed constraint error: FIFA21 is not up for sale. " + "Error on transaction: " +
                transaction + ".\\> \n \tContinuing...", msg03);

        // test buy when there is an auction sale on
        store.currentUser = a;
        store.switchActionSale(a, transaction);
        store.currentUser = b;
        String msg04 = store.buy("Fortnight", "", s, b);
        assertEquals("Transaction successfully! Fortnight was added to " + b.getUserName() +
                "'s inventory. \n \tContinuing...", msg04);
        assertEquals("28.70",  String.format("%.2f", b.getBalance()));
        assertEquals("1403.30", String.format("%.2f", s.getBalance()));
    }

    @Test
    public void testSell() {
        // test price non negative
        ArrayList<ArrayList<String>> games = makeGames();
        String transaction = "";
        s.addGame(games.get(2));
        store.currentUser = s;
        String msg = store.sell(games.get(2).get(0), s, games.get(2).get(1), games.get(2).get(2),  transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game price: " + games.get(2).get(1) +
                " is invalid. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg);

        // test discount price is valid discount
        String msg2 = store.sell(games.get(3).get(0), s, games.get(3).get(1), games.get(3).get(2),  transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game discount: " + games.get(3).get(2) +
                " is invalid. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg2);

        // test price greater than max
        games.get(2).set(1, "1000.00");
        String msg3 = store.sell(games.get(2).get(0), s, games.get(2).get(1), games.get(2).get(2),  transaction);
        assertEquals(1234.00, s.getBalance());
        assertEquals("ERROR: \\<Failed constraint error: Game price: " + games.get(2).get(1)
                + " is invalid. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg3);

        // test discount greater than max
        games.get(3).set(2, "91.00");
        String msg4 = store.sell(games.get(3).get(0), s, games.get(3).get(1), games.get(3).get(2),  transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game discount: " + games.get(3).get(2) +
                " is invalid. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg4);

        // test game name is within requirements of 25 characters
        games.get(0).set(0, "Call of Duty Modern Warfare");
        s.addGame(games.get(0));
        String msg5 = store.sell(games.get(0).get(0), s, games.get(0).get(1), games.get(0).get(2),  transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game name is too long. Error on transaction " +
                transaction + ".\\> \n \tContinuing...", msg5);
    }

    @Test
    public void testAddCredit() {
        String transaction = "";

        // test credit balance doesn't exceed 1000.00 in one day
        store.currentUser = b;
        store.addCredit(b, 999.00f, transaction);
        assertEquals(1197.00, b.getBalance());

        // test credit balance when amount exceeds 1000.00 in one day
        String msg01 = store.addCredit(b, 2.00f, transaction);
        assertEquals("ERROR: \\<Failed constraint error on this transaction: You can only add up to $ "
                + b.MAX_AMT + " per day. Error on transaction: " + transaction + "\\> \n \tContinuing...", msg01);

        // test account balance doesn't exceed max when add credit
        store.currentUser = a;
        store.addCredit(a, 1000.00f, transaction);
        assertEquals(1000.00, a.getBalance());

        // test account balance when it exceed max when add credit
        store.currentUser = fs;
        store.addCredit(fs, 10.00f, transaction);
        assertEquals(999999.00, fs.getBalance());
    }

    @Test
    public void testAuctionSale() {
        ArrayList<ArrayList<String>> games = makeGames();
        String transaction = "";

        // test for applying auction-sale
        store.currentUser = a;
        store.switchActionSale(a, transaction);
        s.addGame(games.get(0));
        store.currentUser = b;
        store.buy(games.get(0).get(0), transaction, s, b);
        assertEquals(148.00, b.getBalance());

        // test for ending auction-sale
        store.currentUser = a;
        store.switchActionSale(a, transaction);
        s.addGame(games.get(1));
        store.currentUser = b;
        store.buy(games.get(1).get(0), transaction, s, b);
        assertEquals(49.00, b.getBalance());
    }

    @Test
    public void testGift() {
        ArrayList<ArrayList<String>> games = makeGames();
        String transaction = "";

        // test user to user
        store.currentUser = b;
        b.addGame(games.get(0));
        String msg = store.gift(games.get(0).get(0), b, fs, transaction);
        assertEquals("Transaction successful! " + games.get(0).get(0) + " has been gifted to "
                + fs.getUserName() + " by " + b.getUserName() + ". \n \tContinuing...", msg);
        store.removeGame(games.get(0).get(0), null, b, transaction);

        // test user without game to user: constraint
        String msg2 = store.gift(games.get(0).get(0), b, a, transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game: " + games.get(0).get(0)
                + " is not in owner's inventory. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg2);

        // test same day, bought or sold: constraint
        s.addGame(games.get(1));
        store.buy(games.get(1).get(0), transaction, s, b);
        String msg3 = store.gift(games.get(1).get(0), b, fs, transaction);
        assertEquals("ERROR: \\<Failed constraint error: " + games.get(1).get(0) + " was purchased" +
                " or put up for sale today. Error on transaction: " + transaction + " is invalid.\\> " +
                "\n \tContinuing...", msg3);
        store.removeGame(games.get(1).get(0), null, b, transaction);

        // test user to user with same game: constraint
        b.addGame(games.get(1));
        fs.addGame(games.get(1));
        String msg4 = store.gift(games.get(1).get(0), b, fs, transaction);
        assertEquals("ERROR: \\<Failed constraint error: Game: " + games.get(1).get(0) +
                " already in receiver's inventory. Error on transaction " + transaction + ".\\> \n \tContinuing...", msg4);

        // test user to sell only user: constraint
        store.currentUser = fs;
        String msg5 = store.gift(games.get(1).get(0), fs, s, transaction);
        assertEquals("ERROR: \\<Failed constraint error: Sellers cannot accept gifts. Error on transaction: " +
                transaction + " is invalid.\\> \n \tContinuing...", msg5);
    }

    @Test
    public void testRemoveGame() {
        // name, price, discount, type
        ArrayList<ArrayList<String>> games = makeGames();
        String transaction = "";
        s.addGame(games.get(0));

        // test user remove their own game
        // test user with the game
        store.currentUser = s;
        store.removeGame(games.get(0).get(0), null, s, transaction);
        assertNull(s.getGame(games.get(0).get(0)));

        // test user remove game up for sale. shouldn't be able to buy the game
        s.addGame(games.get(1));
        store.sell(games.get(1).get(0), s,  games.get(1).get(1), games.get(1).get(2), transaction);
        store.removeGame(games.get(1).get(0), null, s, transaction);
        store.currentUser = b;
        String msg = store.buy(games.get(1).get(0), transaction, s, b);
        assertEquals("ERROR: \\<Failed constraint error: Fortnight is not up for sale. " +
                "Error on transaction: .\\> \n \tContinuing...", msg);

        // test user selling the game but tries to remove
        store.currentUser = s;
        store.sell(games.get(1).get(0), s,  games.get(1).get(1), games.get(1).get(2), transaction);
        String msg2 = store.removeGame(games.get(1).get(0), null, s, transaction);
        assertEquals("ERROR: \\ <Failed constraint error: " + games.get(1).get(0) + " was purchased or" +
                " put up for sale today by " + s.getUserName() + ". Error on transaction: " + transaction +
                " is invalid.\\> \n \tContinuing...", msg2);

        store.currentUser = a;
        b.addGame(games.get(0));
        String msg4 = store.removeGame(games.get(0).get(0), a, b, transaction);
        assertEquals("Transaction successful! " + games.get(0).get(0) + " has been removed from " +
                b.getUserName() + ". \n \tContinuing...", msg4);

        String msg6 = store.removeGame(games.get(1).get(0), a, b, transaction);
        assertEquals("ERROR: \\ <Failed constraint error: " + games.get(1).get(0) + " is not in " + b.getUserName()
                + "'s inventory. Error on transaction: " + transaction + ".\\> \n \tContinuing...", msg6);
    }

}