package com.bank.bank;

import com.bank.account.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by jr on 13/07/17.
 */
public class CustomerUI {
  private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  private static ATM atm;

  /**
   * Lists every account to which the customer has access.
   */
  private static void listAccounts() throws SQLException {
    // List accounts and balances
    List<Account> accounts = atm.listAccounts();
    if (accounts == null) {
      System.out.println("You have no accounts!\n");
    } else {
      // Print out all accounts and balances
      for (Account account : accounts) {
        if (account != null) {
          System.out.println("Account id: " + account.getId());
          System.out.println("Account type: " 
                  + DatabaseSelectHelper.getAccountTypeName(account.getType()));
          System.out.println("Account name: " + account.getName());
          // Check if the balance is negative
          if (account.getBalance().compareTo(new BigDecimal(0)) == -1) {
          	System.out.println("Account balance: -$" 
                    + account.getBalance().multiply(new BigDecimal(-1)));
          } else {
          	System.out.println("Account balance: $" + account.getBalance());
          }
          System.out.println();
        }
      }
    }
  }

  /**
   * allows the customer to add money to a desired account to which they have access.
   * @throws IOException
   */
  private static void makeDeposit() throws IOException, NumberFormatException, SQLException {
    // Make deposit
      System.out.print("Which account do you want to deposit into (id): ");
      int accountId = Integer.parseInt(br.readLine());
      System.out.print("How much money do you want to deposit: ");
      BigDecimal deposit = new BigDecimal(br.readLine());
      // Attempt to make deposit
      boolean successful = atm.makeDeposit(deposit, accountId);
      if (!successful) {
        System.out.println("ERROR: invalid deposit.\n");
      } else {
        System.out.println("Deposited $" + deposit + " into account " + accountId + "\n");
      }
  }

  /**
   * Checks the balance of an account to which the customer has access.
   * @throws SQLException
   * @throws IOException
   */
  private static void checkBalance() throws SQLException, IOException, NumberFormatException {
    // Check balance
    System.out.print("Which account do you want to check (id): ");
    int accountId = Integer.parseInt(br.readLine());
    BigDecimal balance = atm.checkBalance(accountId);
    if (balance == null) {
      System.out.println("ERROR: incorrect account");
    } else if (balance.compareTo(new BigDecimal(0)) == -1) {
      // If the balance is negative, put the "-" sign before the "$"
      System.out.println("Balance: -$" + balance.multiply(new BigDecimal(-1)));
    } else {
      System.out.println("Balance: $" + balance);
    }
  }

  /**
   * Attempts to make a withdraw from an account to wich the customer has access.
   * @throws IOException
   * @throws SQLException
   */
  private static void makeWithdrawal() throws IOException, SQLException, NumberFormatException {
    // Make withdrawal
    System.out.print("Which account do you want to withdraw from (id): ");
    int accountId = Integer.parseInt(br.readLine());
    System.out.print("What amount do you want to withdraw: ");
    BigDecimal amount = new BigDecimal(br.readLine());
    boolean successful = atm.makeWithdrawal(amount, accountId);
    if (successful) {
      System.out.println("Withdrew $" + amount + " from account " + accountId);
    }
  }

  /**
   * Lets the customer select which of their messages to view.
   */
  public static void viewMessage() throws IOException, NumberFormatException {
	// Get the message IDs
	String messageIds = atm.listMessageIds();
	// Check if there are any messages
    if (!messageIds.equalsIgnoreCase("")) {
      System.out.println(messageIds);
      System.out.println("Please enter the ID of the message you wish to view: ");
      int messageId;
      messageId = Integer.parseInt(br.readLine());
      System.out.println(atm.printMessage(messageId));
	} else {
	  System.out.println("You have no messages to view.");
	}
  }

  /**
   * Authenticates the current user for this session.
   */
  public static void login() throws SQLException, IOException, NumberFormatException {
    // Prompt user for id and password, attempt to authenticate
    boolean authenticated = false;
    int tries = 0;
    // Authenticate user to use ATM interface
    do {
      System.out.print("Enter user id: ");
      int id = Integer.parseInt(br.readLine());
      System.out.print("Enter user password: ");
      String password = br.readLine();
      System.out.println("");
      atm = new ATM(id);
      authenticated = atm.authenticate(id, password);
      if (!authenticated) {
        System.out.println("Incorrect id or password, please try again:");
      }
      tries++;
    } while (!authenticated && tries < 5);
    if (!authenticated) {
      System.out.println(
          "You have exceeded the maximum number of login attempts. Please try again later.");
    } else {
      System.out.println(atm.getCurrentCustomer().getName());
      System.out.println(atm.getCurrentCustomer().getAddress());
      System.out.println("");
      listAccounts();
      customerMenu();
    }
  }

  /**
   * Displays the options that customer has  when using the ATM.
   */
  private static void customerMenu() {
      int selection;
      try {
        do {
          // Prompt menu
          System.out.println("1. List accounts and balances");
          System.out.println("2. Make deposit");
          System.out.println("3. Check balance");
          System.out.println("4. Make withdrawal");
          System.out.println("5. View messages");
          System.out.println("6. Exit");
          System.out.print("Enter Selection: ");
          selection = Integer.parseInt(br.readLine());
          System.out.println("");
          if (selection == 1) {
            listAccounts();
          } else if (selection == 2) {
            makeDeposit();
          } else if (selection == 3) {
            checkBalance();
          } else if (selection == 4) {
            makeWithdrawal();
          } else if (selection == 5) {
            viewMessage();
          }
        } while (selection != 6);
    } catch (NumberFormatException e) {
      System.out.println("This is not a valid input.\n");
      customerMenu();
    } catch (SQLException e) {
      System.out.println("There was a problem connecting to the database.");
    } catch (IOException e) {
      System.out.println("There was a probem reading the input.");
    }
  }
}
