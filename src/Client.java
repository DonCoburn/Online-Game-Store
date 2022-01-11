package src;

import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {

        /*
        Note: the following method calls are to serve as a demonstration as to how the code would run based on data
        from the templated daily.txt. The actual program will be using a Command factory to execute these
        transaction rather than having to call each individual method and creating placeholder strings / users.
         */

        // for reference see the sample daily file for all transactions
        Processor processor01 = new Processor("daily1.txt");

        // === Hard-code initial Admin users to create the other users ===
        User Michael = new User(String.format("%-15s", "Michael M"), "AA", 1000.00f);
        User Hilda = new User(String.format("%-15s", "Hilda"), "AA", 1100.00f);
        User Austin = new User(String.format("%-15s", "Austin"), "AA", 1200.00f);
        User Harry = new User(String.format("%-15s", "Harry"), "AA", 1300.00f);
        User Akeem = new User(String.format("%-15s", "Akeem"), "AA", 1400.00f);

        ArrayList<User> admins = new ArrayList<>();
        admins.add(Michael);
        admins.add(Hilda);
        admins.add(Austin);
        admins.add(Harry);
        admins.add(Akeem);

        processor01.makeExistingAdmin(admins);

        // import existing users from our database
        processor01.readDatabase();

        // read each line and perform the action specified by the transaction
        processor01.readDaily();

        // make updates to the database at the end of the daily when all transactions have being completed.
        processor01.write();

        // == new day==
        Processor processor02 = new Processor("daily2.txt");

        processor02.readDatabase();

        processor02.readDaily();

        processor02.write();

        // == new day==
        Processor processor03 = new Processor("daily3.txt");

        processor03.readDatabase();

        processor03.readDaily();

        processor03.write();
    }

}