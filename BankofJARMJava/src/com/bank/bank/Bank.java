package com.bank.bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import com.bank.database.InitializeDatabase;

/**
 * Acts as the interface between the user of the bank, and the machines in the bank.
 */
public class Bank {
  private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  
  /**
   * Provides a list of actions that the user can perform.
   * @throws IOException
   * @throws SQLException if there is a problem with the database.
   */
  private static void menu() throws IOException, SQLException {
    // Prompt menu
    int selection;
    try {
      do {
        System.out.println("1 - TELLER Interface");
        System.out.println("2 - ATM Interface");
        System.out.println("3 - ADMIN Interface");
        System.out.println("0 - Exit");
        System.out.print("Enter Selection: ");
        selection = Integer.parseInt(br.readLine());
        if (selection == 1) {
          // Context menu for Teller Interface
          TellerUI.login();
        } else if (selection == 2) {
          // Context menu for ATM Interface
          CustomerUI.login();
        } else if (selection == 3) {
          // Context menu for Admin Interface
          AdminUI.adminLogin();
        }
      } while(selection != 0);
    } catch (NumberFormatException e) {
      System.out.println("Invalid Input\n");
      menu();
    }
  }
  
  /**
   * Connects to database and allows the user to interact with it through different bank machines.
   * @param argv
   */
  public static void main(String[] argv) {
    //Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
    try {
      //ONLY UNCOMMENT THIS ON FIRST RUN!
      //DatabaseDriverExtender.initialize(connection);
      //InitializeDatabase.initialize();

      // Create admin and go to main menu
      if (argv.length > 0 && argv[0].equalsIgnoreCase("-1")) {
        //DatabaseDriverExtender.initialize(connection);
        AdminUI.createAdmin();
        menu();
      }

      // Login to admin account to make new tellers
      else if (argv.length > 0 && argv[0].equalsIgnoreCase("1")) {
        // Login to a valid admin account
        AdminUI.adminLogin();
      }

      // Go to main menu otherwise
      else {
        menu();
      }
    } catch (SQLException e) {
      //TODO Improve this!
      System.out.println("Something went wrong with the database :(");
    } catch (IOException e) {
      System.out.println("IOException occurred");
    }
    catch (Exception e) {
      System.out.println("Something went wrong :(");
    }
    finally {
      try {
        //connection.close();
      } catch (Exception e) {
        System.out.println("Looks like it was closed already :)");
      }
    }
  }
}
