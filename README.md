# Features

## HOW TO RUN / SETUP

The client class gives an example with how to run three (3)  sample daily templates. Each user story is covered with
these sample files, so you can see all our method being executed. 

Ensure to call the "makeExistingAdmin(admins)" method to create pre-existing admins.
This method requires an ArrayList of Users to be passed in as parameter. 

Again, see the client class for a neat example :)

NOTE: THIS LIST MUST NOT BE EMPTY!

## CODE DESIGN

### Overview
The program takes in two text files, daily.txt and database.txt. daily.txt is responsible for providing the list of 
transactions the program will have to process while database.txt is responsible for providing information regarding 
previous users of the "Store".

**Processor.java** will be reading information from both daily.txt and database.txt and will do the following:

**daily.txt** - Information from this text file will be used to create a command, which will then be processed and 
carried out in the command factory. 


**database.txt** - Information from this text file will be used to create an Arraylist of User objects. 
These User objects are used to represent customers / users of the "Store" and will contain information such as 
username, user type, and balance. If database.txt is empty, such as the case when the program runs for the very first
time, an empty Arraylist will be created, which simply indicates that there exists no information regarding previous
users.

The Arraylist obtained from database.txt will be modified according to valid transactions carried out by the command 
factory. In other words, for each valid transaction, data for a User object in the Arraylist will be changed based on 
the transaction.

At the end, **Processor.java** will write to database.txt based on information from the database Arraylist.

### Design Patterns Chosen
 - Command: We have different transactions that performs different actions, so the Command design pattern allows us to 
   execute different actions relating to the transaction being processed.
   
- Factory: We need to create different commands according to the transaction code, so the Factory design pattern is
  appropriate to check and return the command that is required for a specific transaction.

### Classes

**Processor.java:**

As mentioned previously, this class is responsible for reading daily.txt (and creating commands accordingly)
and database.txt and then writing to database.txt at the end. 

Firstly, the constructor of Processor asks for the name of the daily text file, after-which it reads and create the
existing users in our database (database.txt). Calling "makeExistingAdmin(admins)" writes some pre-existing admins
that are necessary to carry out important initial tasks such as creating other users. 

Finally, we read and process each transaction line by line in daily.txt and update the database at the end of each day
after processing all the new information.


**User.java:**

This class is used to create User objects. Each user object will have a username, user type, balance, and a double 
Arraylist storing the games they own. Information on each game comes in the following format: 

[game name, base price, discounted price, game status]

- Game Status:
  - "s" = fit for sale
  - "st" = a game that was put up for sale today
  - "b" = a game that was bought
  - "bt" = a game that was bought today

Aside from getters and setters for its attributes, the class also contains the following methods:

- **addToBalance(credit)**: Adds a set amount of credits to user's balance
  

- **getGame(gameName)**: Returns an array containing a game, and it's info specified by the given
  game name. Returns null if the game doesn't exist in the user's inventory.
  

- **pay(credit, other)**: Subtracts credit from user's total balance and transfer the credit to the other user. It 
  returns true if this transfer was successful.
  

- **addGame(game)**: Updates the status of the games in the user's inventory at the end of the day

**Store.java**
 
This class contains methods representing all transactions that users can do (including privileged transactions):

Note: Each class has different parameters + a transaction parameter. The transaction parameter is mainly used for
error messages as the parameters before it provides all information necessary to carry out the transaction.

- **login(user, transaction)**: Switches currentUser to user
  

- **logout(user, transaction)**: Switches currentUser to null
  

- **switchAuctionSale(user, transaction)**: Switches the auction sale status based on its current status
  

- **addCredit(u, amount, transaction)**: Add credit to user u's balance if the amount is less than the daily max.


- **buy(game, transaction, seller, buyer)**: Buys a game from seller and puts the game into buyer's inventory (assuming
  transaction is valid). 
  
  
- **sell(game, seller, transaction)**: Allows for a seller to put a game up for sale (assuming transaction is valid).
  

- **gift(game, owner, receiver, transaction)**: Gifts a game from owner to receiver if the game is in owners inventory
  

- **removeGame(game, admin, gameOwner, transaction)**: Allows an admin to remove a game from a user's inventory or user
  to remove a game from their own inventory


- **createAccount(userName, userType, balance, transaction)**: Allows an admin to create a new account and add it into
the database Arraylist
  

- **refund(buyer, seller, credit, transaction)**: Allows an admin to process a refund from seller's account to buyer's.


- **deleteAccount(userName, transaction)**: Allows an admin to delete a user if they exist but cannot delete 
  themselves.
  
Aside from these methods, Store.java also contains the following:


- **findUser(name)**: Looks through the database ArrayList of a user object with username "name" and returns the user
object if it exists


- **createDatabaseAccount(userInformation)**: This method is used to create the accounts present in database.txt

  
**Command Factory Classes:**

These classes are simply made to create commands according to the transaction being dealt with. i.e. 
AddCreditCommand.java is responsible for creating an AddCreditCommand. The command factory oversees which command needs
to be created depending on the transaction.


**Client.java:** 

This class simply serves as a showcase to display how the program runs.
