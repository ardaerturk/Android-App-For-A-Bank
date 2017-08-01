package com.bank.bank;

import com.bank.account.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasehelper.SerializableDatabase;
import com.bank.databasemap.RolesMap;
import com.bank.generics.Roles;
import com.bank.security.PasswordHelpers;
import com.bank.user.Admin;
import com.bank.user.Customer;
import com.bank.user.Teller;
import com.bank.user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jr on 13/07/17.
 */
public class AdminUI {
  private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  private static User currentAdmin;

  /**
   * Creates a new user.
   * @param roleId is the desired role ID of the new user.
   */
  private static void createUser(int roleId) throws SQLException,
          IOException, NumberFormatException {
    System.out.println("Enter your name:");
    String name = br.readLine();
    System.out.println("Enter a password:");
    String password = br.readLine();
    System.out.println("Enter your address:");
    String address = br.readLine();
    System.out.println("Enter your age:");
    int age = Integer.parseInt(br.readLine());
    int id = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
    // Error creating admin
    if (id == -1) {
      System.out.println("Error creating user...");
    } else {
      System.out.println("User created successfully! Your id is: " + id);
    }
  }

  /**
   * creates a new admin on the database
   */
  public static void createAdmin() throws SQLException, IOException, NumberFormatException {
    System.out.println("Creating an admin...");
    System.out.println("Enter your name:");
    String name = br.readLine();

    System.out.println("Enter a password:");
    String password = br.readLine();

    System.out.println("Enter your address:");
    String address = br.readLine();

    System.out.println("Enter your age:");
    int age = Integer.parseInt(br.readLine());

    int id = AdminTerminal.makeNewAdmin(name, age, address, password);

    // Error creating admin
    if (id == -1) {
      System.out.println("Error creating admin...");
    } else {
      System.out.println("Admin created successfully! Your id is: " + id);
    }
  }

  /**
   * create a new teller on the database
   */
  private static void createTeller() throws SQLException, IOException, NumberFormatException {
    System.out.println("Creating a teller...");
    System.out.println("Enter your name:");
    String name = br.readLine();

    System.out.println("Enter a password:");
    String password = br.readLine();

    System.out.println("Enter your address:");
    String address = br.readLine();

    System.out.println("Enter your age:");
    int age = Integer.parseInt(br.readLine());

    int id = AdminTerminal.makeNewTeller(name, age, address, password);

    // Error creating admin
    if (id == -1) {
      System.out.println("Error creating admin...");
    } else {
      System.out.println("Teller created successfully! Your id is: " + id);
    }
  }

  /**
   * If login is successful, opens the Admin menu.
   */
  public static void adminLogin() throws SQLException, IOException, NumberFormatException {
    // Login, create tellers
    System.out.println("Admin login...");
    System.out.println("Please enter your id:");
    int id = Integer.parseInt(br.readLine());
    System.out.println("Please enter your password:");
    String password = br.readLine();

    // Login if password is right and user is admin
    boolean correctRole = DatabaseSelectHelper.getUserRole(id) == RolesMap.getId(Roles.ADMIN);
    boolean correctPassword = PasswordHelpers
        .comparePassword(DatabaseSelectHelper.getPassword(id), password);
    User possibleAdmin = DatabaseSelectHelper.getUserDetails(id);
    if (correctRole && correctPassword) {
      currentAdmin = possibleAdmin;
      adminMenu();
    } else {
      System.out.println("Incorrect admin id or password!\n");
    }
  }
  
  /**
   * lists all the admins in the database.
   */
  public static void viewAdmins() throws SQLException {
    List<Admin> admins = AdminTerminal.getAdmins();
    if (admins == null) {
      System.out.println("No Admin Found");
    } else {
      for (Admin admin : admins) {
        if (admin != null) {
          System.out.println("Id:" + " " + admin.getId() + " | " + "Name:" + " " + admin.getName());
        }
      }
    }
  }
  
  /**
   * lists all the tellers in the database.
   */
  public static void viewTellers() throws SQLException {
    List<Teller> tellers = AdminTerminal.getTellers();
    if (tellers == null) {
      System.out.println("No Teller Found");
    } else {
      // Print out all the tellers
      for (Teller teller : tellers) {
        if (teller != null) {
          System.out.println("Id:" + " " + teller.getId() + " | " + "Name:" + " " + teller.getName());
        }
      }
    }
  }
  
  /**
   * lists all the customers in the database.
   */
  public static void viewCustomers() throws SQLException {
    List<Customer> customers = AdminTerminal.getCustomers();
    if (customers == null) {
      System.out.println("No Customer Found");
    } else {
      // Print out all the tellers
      for (Customer customer : customers) {
        if (customer != null) {
          System.out.println("Id:" + " " + customer.getId() + " | " + "Name:" + " "
              + customer.getName());
        }
      }
    }
  }

  /**
   * Changes the role of a teller, to that of an admin.
   */
  public static void promoteTellerToAdmin() throws SQLException,
          IOException, NumberFormatException {
    System.out.println("The id of the Teller that is going to be promoted: ");
    int id = Integer.parseInt(br.readLine());
    User teller = DatabaseSelectHelper.getUserDetails(id);
    if (teller.getRoleId() == RolesMap.getId(Roles.TELLER)) {
      DatabaseUpdateHelper.updateUserRole(1, id);
      System.out.println("Successfully promoted Ms. " + teller.getName() + " " + "with the id " +teller.getId());
    } else if (teller.getRoleId() == RolesMap.getId(Roles.CUSTOMER)) {
      System.out.println("Sorry, you can't promote customers!");
    } else if ( teller.getRoleId() == RolesMap.getId(Roles.ADMIN)) {
      System.out.println("The user with the id " + id + " " + "is already an admin!");
    } else {
      System.out.println("Sorry, something went wrong :-(");
    }
  }

  /**
   * get the total balance of all accounts that belong to a particular user.
   */
  public static void checkBalance() throws SQLException, IOException, NumberFormatException {
    System.out.print("Which customer do you want to check (id): ");
    int customerId = Integer.parseInt(br.readLine());

    if (DatabaseSelectHelper.getUserRole(customerId) == 3) {
    List<Integer> allAccountIds = DatabaseSelectHelper.getAccountIds(customerId);

    int i = 0;
    int size = allAccountIds.size();
    BigDecimal accountBalance = new BigDecimal("0.00");

    while (size != 0) {
      Account account = DatabaseSelectHelper.getAccountDetails(allAccountIds.get(i));

      if (account != null) {
        accountBalance = accountBalance.add(account.getBalance());

        System.out.println("Account id: " + account.getId());
        System.out.println("Account type: " + DatabaseSelectHelper.getAccountTypeName(account.getType()));
        System.out.println("Account name: " + account.getName());
        System.out.println("Account balance: $" + account.getBalance());
        System.out.println();
      }
      size--;
      i++;

    }
    System.out.println("Total amount of money in the accounts: $" + accountBalance);
    currentAdmin.sendMessage(customerId, "An admin has viewed the total balance "
            + "of your accounts.");
    } else {
      System.out.println("Please enter a Customer's id!");
    }
  }

  /**
   * get the total balance of all accounts in the bank.
   */
  public static void getTotalBalance() throws SQLException {
    List<Customer> customers = AdminTerminal.getCustomers();

    BigDecimal accountBalance = new BigDecimal("0.00");
    int i = 0;
    int customerTotal = customers.size();

    while (customerTotal != 0) {
      Customer customer = customers.get(i);
      for (Account account : customer.getAccounts()) {
        if (account != null) {
          accountBalance = accountBalance.add(account.getBalance());
        }
      }
      customerTotal--;
      i++;
    }
    System.out.println("Total amount of money in the bank: $" + accountBalance);
  }

  /**
   * Prints the desired message.
   */
  public static void viewMyMessages() throws IOException, NumberFormatException {
    String messageIds = currentAdmin.PrintMessageIds();
    // Check if the admin has any messages
    if (!messageIds.equalsIgnoreCase("")) {
      System.out.println("Messages:");
      System.out.println(messageIds);
      System.out.println("Please enter the ID of the message you wish to view: ");
      int messageId = Integer.parseInt(br.readLine());
      System.out.println(AdminTerminal.viewMyMessages(currentAdmin, messageId));
    } else {
      System.out.println("You have no messages.");
    }
  }

  /**
   * View the messages of a user in the bank without changing their view status.
   */
  public static void viewOthersMessage() throws SQLException, IOException, NumberFormatException {
    int userId;
    User user;
    int messageId;
    System.out.println("Please enter the ID of the user whose messages you wish to view: ");
    userId =  Integer.parseInt(br.readLine());
    user = DatabaseSelectHelper.getUserDetails(userId);
    String messageIds = user.PrintMessageIds();
    // Check if the user has any messages
    if (!messageIds.equalsIgnoreCase("")) {
      System.out.println("Messages: ");
      System.out.println(messageIds);
      System.out.print("Enter the ID of the message you wish to view: ");
      messageId = Integer.parseInt(br.readLine());
      System.out.println(AdminTerminal.viewOtherMessages(user, messageId));
    } else {
      System.out.println("This user has no messages.");
    }
  }

  /**
   * Sends a message to a desired user.
   */
  public static void sendMessage() throws IOException, NumberFormatException {
    int recipientId;
    String message;
    System.out.println("Please enter the ID of the user you wish to message: ");
    recipientId = Integer.parseInt(br.readLine());
    System.out.println("Message you wish to send: ");
    message = br.readLine();
    AdminTerminal.sendMessage(currentAdmin, recipientId, message);
  }

  /**
   * Displays the option that an admin can take, and prompts them for their choice.
   * @throws IOException thrown when there is an issue with reading the input.
   * @throws SQLException thrown when there is a problem connecting to the database.
   */
  private static void adminMenu() {
    int selection;
    try {
      do {
        System.out.println("Admin menu...");
        System.out.println("1 - Create Teller");
        System.out.println("2 - Create Admin");
        System.out.println("3 - View Admins");
        System.out.println("4 - View Tellers");
        System.out.println("5 - View Customers");
        System.out.println("6 - Promote a Teller to an Admin");
        System.out.println("7 - View A Customer's Balance");
        System.out.println("8 - View Total Balance in the Bank");
        System.out.println("9 - View Messages");
        System.out.println("10 - View Someone's Messages");
        System.out.println("11 - Send Message");
        System.out.println("12 - Serialize database");
        System.out.println("13 - Deserialize database");
        System.out.println("0 - Exit");
        selection = Integer.parseInt(br.readLine());

        if (selection == 1) {
          createTeller();
        } else if (selection == 2) {
          createAdmin();
        } else if (selection == 3) {
          viewAdmins();
        } else if (selection == 4) {
          viewTellers();
        } else if (selection == 5) {
          viewCustomers();
        } else if (selection == 6) {
          promoteTellerToAdmin();
        } else if (selection == 7) {
          checkBalance();
        } else if (selection == 8) {
          getTotalBalance();
        } else if (selection == 9) {
          viewMyMessages();
        } else if (selection == 10) {
          viewOthersMessage();
        } else if (selection == 11) {
          sendMessage();
        } else if (selection == 12) {
          SerializableDatabase sd = new SerializableDatabase();
          sd.exportDatabase();
          System.out.println("Exported database");
        } else if (selection == 13) {
          // Import database
          SerializableDatabase sd = new SerializableDatabase();
          boolean success = sd.importDatabase();

          if (!success) {
            System.out.println("Could not import database. Rolled back to previous version.");
          } else {
            System.out.println("Imported database successfully!");
            break;
          }
          break;
        } else if (selection != 0) {
          System.out.println("Please enter a valid number.");
        }
      
      } while (selection != 0);

    } catch (NumberFormatException e) {
      System.out.println("This was not a valid input. \n");
      adminMenu();
    } catch (IOException e) {
      System.out.println("There was a problem reading the input.");
    } catch (SQLException e) {
      System.out.println("There was a probelm connecting to the database.");
    }
  }
}
