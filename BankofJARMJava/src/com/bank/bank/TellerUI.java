package com.bank.bank;

import com.bank.account.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.databasemap.AccountTypesMap;
import com.bank.generics.AccountTypes;
import com.bank.security.PasswordHelpers;
import com.bank.user.Customer;
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
public class TellerUI {
  private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  private static TellerTerminal tellerTerminal;
  private static boolean hasCustomer;

  /**
   * Lets the user try to log in as a Teller.
   */
  public static void login() throws SQLException, IOException, NumberFormatException {
    System.out.print("Enter user id: ");
    int id = Integer.parseInt(br.readLine());
    System.out.print("Enter user password: ");
    String password = br.readLine();
    // Attempt to authenticate
    tellerTerminal = new TellerTerminal(id, password);
    if (!tellerTerminal.isCurrentUserAuthenticated()) {
      System.out.println("Incorrect teller id or password.");
    } else if (!DatabaseSelectHelper.getRole(DatabaseSelectHelper.getUserRole(id))
        .equalsIgnoreCase("TELLER")) {
      System.out.println("User is not a teller.");
    } else {
      hasCustomer = false;
      tellerMenu();
    }
  }

  /**
   * Allows a teller to try and connect to a customer's account.
   */
  public static void authenticateNewUser() throws SQLException, 
          IOException, NumberFormatException {
    // Authenticate new user
    System.out.print("Enter customer id to authenticate: ");
    int customerId = Integer.parseInt(br.readLine());
    System.out.print("Enter customer password to authenticate: ");
    String customerPassword = br.readLine();
    User user = DatabaseSelectHelper.getUserDetails(customerId);
    if (user instanceof Customer) {
      tellerTerminal.setCurrentCustomer((Customer)user);
      if (tellerTerminal.authenticateCurrentCustomer(customerPassword)) {
        hasCustomer = true;
        System.out.println("Authenticated and set the current customer!");
      } else {
        System.out.println("Password is incorrect.");
        hasCustomer = false;
        tellerTerminal.deAuthenticateCustomer();
      }
    } else {
      System.out.println("User is not a customer!");
      hasCustomer = false;
    }
  }

  /**
   * Attempts to add a new user to the database.
   */
  public static void makeNewUser() throws SQLException, IOException, NumberFormatException {
    // Make new User
    System.out.print("Enter the new user's name: ");
    String userName = br.readLine();
    System.out.print("Enter the new user's age: ");
    int userAge = Integer.parseInt(br.readLine()); 
    System.out.print("Enter the new user's address: ");
    String userAddress = br.readLine();
    System.out.print("Enter the new user's password: ");
    String userPassword = br.readLine();
    tellerTerminal.makeNewUser(userName, userAge, userAddress, userPassword);
    System.out.println("Sucessfully created user!");
  }

  /**
   * Adds interest to a given account.
   */
  public static void addInterestToAccount() throws SQLException,
          IOException, NumberFormatException {
    int accountID;
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Please enter the account ID for the account you wish to add interest: ");
    accountID = Integer.parseInt(br.readLine());
    tellerTerminal.giveInterestToAccount(accountID);
    System.out.println("Interest Added.");
  }

  public static void viewOneOfMyMessages() throws SQLException,
          IOException, NumberFormatException {
    int messageId;
    String messageIds = tellerTerminal.listTellerMessageIds();
    // Check if there are any messages to access
    if (!messageIds.equalsIgnoreCase("")) {
      System.out.println("Current message IDs:");
      System.out.println(tellerTerminal.listTellerMessageIds());
      System.out.println("Please enter the ID of the message you wish to view: ");
      messageId = Integer.parseInt(br.readLine());
      System.out.println(tellerTerminal.viewSpecificMessage(messageId));
    } else {
      System.out.println("There are no messages in your inbox.");
    }
  }

  public static void sendCustomerMessage() throws SQLException,
          IOException, NumberFormatException {
    int customerId;
    String message;
    System.out.println("Please enter the ID of the customer you wish to message.");
    customerId = Integer.parseInt(br.readLine());
    System.out.println("Enter the message you wish to send: ");
    message = br.readLine();
    tellerTerminal.sendMessageToCustomer(customerId, message);
  }

  /**
   * Attempts to add a new account to the database.
   */
  public static void makeNewAccount() throws SQLException, IOException, NumberFormatException {
    // Make new account
    System.out.print("Enter the new account's name: ");
    String name = br.readLine();
    System.out.print("Enter the new account's balance: ");
    BigDecimal balance = new BigDecimal(br.readLine());
    System.out.print("Enter the new account's type: \n(CHEQUING/SAVING/TFSA/RESTRICTED_SAVINGS/BALANCE_OWING): ");
    String typeName = br.readLine();
    int type = getAccountTypeIdGivenName(typeName);
    // Check if the account is savings and the balance is < 1000
    if (typeName.equalsIgnoreCase("SAVING") && (balance.compareTo(new BigDecimal(1000)) == -1)) {
      // If it is, change the account type to Chequing
      type = AccountTypesMap.getId(AccountTypes.CHEQUING);
      System.out.println("Insufficient funds to create a savings account.\nAccount has been changed to Chequing.");
    }
    boolean successful = tellerTerminal.makeNewAccount(name, balance, type);
    if (!successful) {
      System.out.println("ERROR: account creation not successful.");
    } else {
      System.out.println("Sucessfully created account!");
    }
  }

  /**
   * Adds interest to an account connected to the current user.
   */
  public static void giveInterest() throws SQLException, IOException, NumberFormatException {
    // Give interest
    System.out.print("Which account would you like to give interest to (id): ");
    int accountId = Integer.parseInt(br.readLine());
    tellerTerminal.giveInterest(accountId);
    tellerTerminal.messageUser(tellerTerminal.getCurrentCustomerId(), 
         "Interest has been added to your account with ID: " + accountId);
    System.out.print("Success\n");
  }

  /**
   * Makes a deposit into the desired account which is connected to the current user.
   */
  public static void makeDeposit() throws SQLException, IOException, NumberFormatException {
    // Make a deposit
    System.out.print("Which account do you want to deposit into (id): ");
    int accountId = Integer.parseInt(br.readLine());
    System.out.print("How much money do you want to deposit: $");
    BigDecimal deposit = new BigDecimal(br.readLine());
    // Attempt to make deposit
    if (!tellerTerminal.makeDeposit(deposit, accountId)) {
      System.out.println("ERROR: invalid deposit.");
    } else {
      System.out.println("Deposited $" + deposit + " into account " + accountId);
    }
  }

  public static void makeWithdrawal() throws SQLException, IOException, NumberFormatException {
    // Make a withdrawal
    System.out.print("Which account do you want to withdraw from (id): ");
    int accountId = Integer.parseInt(br.readLine());
    System.out.print("What amount do you want to withdraw: ");
    BigDecimal amount = new BigDecimal(br.readLine());
    boolean successful = tellerTerminal.makeWithdrawal(amount, accountId);
    if (successful) {
      System.out.println("Withdrew $" + amount + " from account " + accountId);
    }
  }

  public static void checkBalance() throws SQLException, IOException, NumberFormatException {
    // Check balance
    System.out.print("Which account do you want to check (id): ");
    int accountId = Integer.parseInt(br.readLine());
    BigDecimal balance = tellerTerminal.checkBalance(accountId);
    if (balance == null) {
      System.out.println("ERROR: incorrect account");
    } else if (balance.compareTo(new BigDecimal(0)) == -1) {
      // If the balance is negative, put the "-" sign before the "$"
      System.out.println("Balance: -$" + balance.multiply(new BigDecimal(-1)));
    } else {
      System.out.println("Balance: $" + balance);
    }
  }

  public static void listAccounts() throws SQLException {
    // List accounts
    List<Account> accounts = tellerTerminal.listAccounts();
    if (accounts == null) {
      System.out.println("You have no accounts!");
    } else {
      // Print out all accounts and balances
      for (Account account : accounts) {
        if (account != null) {
          System.out.println("Account id: " + account.getId());
          System.out.println("Account type: " + DatabaseSelectHelper.getAccountTypeName(account.getType()));
          System.out.println("Account name: " + account.getName());
          // Check if the balance is negative
          if (account.getBalance().compareTo(new BigDecimal(0)) == -1) {
            System.out.println("Account balance: -$" + account.getBalance().multiply(new BigDecimal(-1)));
          } else {
            System.out.println("Account balance: $" + account.getBalance());
          }
          System.out.println();
          }
        }
      }
  }

  /**
   * updates password, name, and address of a customer.
   * @throws IOException
   * @throws SQLException
   */
  public static void updateCustomerInfo() throws SQLException, IOException, NumberFormatException {
    // Make new User
    System.out.println("Leave blank if you do not want to change");
    int customerId = tellerTerminal.getId();
    System.out.print("Enter the new name: ");
    String name = br.readLine();
    if (!name.isEmpty()) {
      DatabaseUpdateHelper.updateUserName(name, customerId);
    }
    System.out.print("Enter the new address: ");
    String address = br.readLine();
    if (!address.isEmpty()) {
      DatabaseUpdateHelper.updateUserAddress(address, customerId);
    }
    System.out.print("Enter the new password: ");
    String password = br.readLine();
    if (!password.isEmpty()) {
      DatabaseUpdateHelper.updateUserPassword(PasswordHelpers.passwordHash(password), customerId);
    }
    System.out.println("Sucessfully updated!");
  }

  /**
   * Adds a User Account relationship into the database.
   */
  public static void linkAccountUser() throws IOException, NumberFormatException {
    System.out.println("Please enter an account ID: ");
    int accountId = Integer.parseInt(br.readLine());
    int userId = tellerTerminal.getCurrentCustomerId();
    tellerTerminal.linkUserAccount(userId, accountId);
    System.out.println("The account was linked.");
  }

  /**
   * Views the messages of the current customer.
   */
  public static void viewCustomerMessage() throws IOException, NumberFormatException {
    String messageIds = tellerTerminal.listCustomerMessageIds();
    // Check if the customer has any messages
    if (!messageIds.equalsIgnoreCase("")) {
      System.out.println("Current message IDs: ");
      System.out.println(tellerTerminal.listCustomerMessageIds());
      System.out.println("Please enter messageId: ");
      int messageId = Integer.parseInt(br.readLine());
      System.out.println(tellerTerminal.viewCustomerMessage(messageId));
    } else {
      System.out.println("There are no messages in the current customer's inbox.");
    }
  }

  /**
   * Sets the current customer to none.
   */
  public static void closeCustomerSession() {
    // Close customer session
    tellerTerminal.deAuthenticateCustomer();
    hasCustomer = false;
    System.out.println("Closed current customer session!");
  }

  /**
   * Opens the menu for the teller.
   */
  public static void tellerMenu() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    // Prompt user for id and pw, attempt to authenticate
    try {
      int selection;
      do {
        System.out.println("1. Authenticate new user");
        System.out.println("2. Make new User");
        System.out.println("3. Add interest to account");
        System.out.println("4. View my messages");
        System.out.println("5. Send a customer a message");
        if (hasCustomer) {
          System.out.println("6. Make new account");
          System.out.println("7. Give interest");
          System.out.println("8. Make a deposit");
          System.out.println("9. Make a withdrawal");
          System.out.println("10. Check balance");
          System.out.println("11. List accounts");
          System.out.println("12. Update user info");
          System.out.println("13. Link account to customer");
          System.out.println("14. View current cutomer's messages");
          System.out.println("15. Close customer session");
        }
        System.out.println("0. Exit");
        System.out.print("Enter Selection: ");
        selection = Integer.parseInt(br.readLine());
        if (selection == 1) {
          authenticateNewUser();
        } else if (selection == 2) {
          makeNewUser();
        } else if (selection == 3) {
          addInterestToAccount();
        } else if (selection == 4) {
          viewOneOfMyMessages();
        } else if (selection == 5) {
          sendCustomerMessage();
        } else if (hasCustomer && selection == 6) {
          makeNewAccount();
        } else if (hasCustomer && selection == 7) {
          giveInterest();
        } else if (hasCustomer && selection == 8) {
          makeDeposit();
        } else if (hasCustomer && selection == 9) {
          makeWithdrawal();
        } else if (hasCustomer && selection == 10) {
          checkBalance();
        } else if (hasCustomer && selection == 11) {
          listAccounts();
        } else if (hasCustomer && selection == 12) {
          updateCustomerInfo();
        } else if (hasCustomer && selection == 13) {
          linkAccountUser();
        } else if (hasCustomer && selection == 14) {
          viewCustomerMessage();
        } else if (hasCustomer && selection == 15) {
          closeCustomerSession();
        }
      } while (selection != 0);
    } catch (NumberFormatException e) {
      System.out.println("This is not a valid input.\n");
      tellerMenu();
    } catch (IOException e) {
      System.out.println("Somthing when wrong with reading the input.");
    } catch (SQLException e) {
      System.out.println("There was a problem connecting to the database.");
    }
  }

  /**
   * Returns the id of the account type in the database given its name.
   * @param typeName The name of the role (e.g. Chequing, Saving...)
   * @return The id of the role in the database, or -1 if role not found
   */
  private static int getAccountTypeIdGivenName(String typeName) {
    for (AccountTypes type : AccountTypes.values()) {
      if (typeName.equalsIgnoreCase(type.name())) {
        return AccountTypesMap.getId(type);
      }
    }
    return -1;
  }
}
