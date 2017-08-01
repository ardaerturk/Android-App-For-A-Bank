package com.b07.bankofjarm.menu;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.b07.bankofjarm.R;

import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.bank.AdminTerminal;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.databasehelper.DatabaseUpdateHelper;
import com.b07.bankofjarm.databasehelper.SerializableDatabase;
import com.b07.bankofjarm.generics.Roles;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.user.Admin;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.User;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class AdminMenuActivity extends MenuActivity {

  boolean hasCustomer = true;
  Admin currentAdmin = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Get username and pw
    Intent intent = getIntent();
    int id = intent.getIntExtra(getString(R.string.EXTRA_USER_ID), -1);
    String password = intent.getStringExtra(getString(R.string.EXTRA_USER_PW));

    // Attempt to log in through terminal
    if (!AdminTerminal.login(id, password)) {
      Toast.makeText(getApplicationContext(), getText(R.string.invalid_login_toast),
          Toast.LENGTH_SHORT).show();
      logout();
    } else {
      DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
      this.currentAdmin = (Admin) instance.getUserObject(id);
      TextView welcomeText = (TextView) findViewById(R.id.menu_customer_welcome);
      welcomeText.append(" " + currentAdmin.getName());
      instance.close();
    }
  }

  /**
   * Handles a click on the Admin menu.
   *
   * @param position the position, starting from 0, in the listview of the item clicked
   */
  @Override
  protected void handleClick(int position) {
    // Identifies each item by its icon id
    // TODO: remove toasts when actual actions are implemented
    int iconId = this.menuOptions.get(position).getIconId();

    switch (iconId) {
      case R.drawable.menu_view_messages:
        viewMessagesDialog();
        break;
      case R.drawable.menu_create_user:
        Intent intent = new Intent(AdminMenuActivity.this, AdminCreateUserActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "creating users", Toast.LENGTH_SHORT).show();
        break;
      case R.drawable.menu_view_users:
        startActivity(new Intent(AdminMenuActivity.this, AdminViewUsersActivity.class));
        Toast.makeText(getApplicationContext(), "viewing users", Toast.LENGTH_SHORT).show();
        break;
      case R.drawable.menu_promote_teller:
        promoteTellerDialog();
        break;
      case R.drawable.menu_check_balance:
        checkCustomerBalanceDialog();
        break;
      case R.drawable.menu_check_bank_balance:
        getTotalBalanceDialog();
        break;
      case R.drawable.menu_serialize:
        SerializableDatabase serializableDatabase = new SerializableDatabase();
        boolean done = serializableDatabase.exportDatabase(getFilesDir());
        if (!done) {
          Toast.makeText(getApplicationContext(), "Serialization failed. Rolling back to previous version...", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(getApplicationContext(), "Serialized successfully!", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.drawable.menu_deserialize:
        SerializableDatabase sd = new SerializableDatabase();
        boolean success = sd.importDatabase(getFilesDir());
        if (success) {
          Toast.makeText(getApplicationContext(), "Deserialized successfully. Please log back in.", Toast.LENGTH_SHORT).show();
          logout();
        } else {
          Toast.makeText(getApplicationContext(), "Deserialization failed. Rolling back to previous version...", Toast.LENGTH_SHORT).show();
        }
        break;
      case R.drawable.menu_send_message:
        sendMessageDialog();
        break;
      case R.drawable.menu_view_users_messages:
        viewUsersMessageDialog();
        break;
      default:
        Toast.makeText(getApplicationContext(), "No such action!", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  /**
   * Generate menu options for an Admin (AdminTerminal options).
   */
  @Override
  protected void generateMenuOptions() {
    super.generateMenuOptions();

    // Add options to create/view users, promote tellers, view balances and serialization
    menuOptions.add(new MenuModel(R.drawable.menu_view_users_messages,
        getString(R.string.menu_admin_view_users_messages)));
    menuOptions.add(new MenuModel(R.drawable.menu_send_message,
        getString(R.string.menu_admin_send_message)));
    menuOptions.add(
        new MenuModel(R.drawable.menu_create_user, getString(R.string.menu_admin_create_user)));
    menuOptions
        .add(new MenuModel(R.drawable.menu_view_users, getString(R.string.menu_admin_view_users)));
    menuOptions.add(new MenuModel(R.drawable.menu_promote_teller,
        getString(R.string.menu_admin_promote_teller)));
    menuOptions.add(new MenuModel(R.drawable.menu_check_balance,
        getString(R.string.menu_admin_view_customer_balance)));
    menuOptions.add(new MenuModel(R.drawable.menu_check_bank_balance,
        getString(R.string.menu_admin_view_bank_balance)));
    menuOptions
        .add(new MenuModel(R.drawable.menu_serialize, getString(R.string.menu_admin_serialize)));
    menuOptions.add(
        new MenuModel(R.drawable.menu_deserialize, getString(R.string.menu_admin_deserialize)));
  }


  private void getTotalBalanceDialog() {
    try {
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
      super.makeDialog(null, "Total amount of money in the bank: $" + accountBalance.toString(),
          null, null);
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    }
  }

  private void checkCustomerBalanceDialog() {
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    DialogInterface.OnClickListener callback = new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        try {
          checkCustomerBalanceOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super
        .makeDialog("Checking balance...", "Please enter an id:", Arrays.asList(idInput), callback);
  }

  private void checkCustomerBalanceOnOK(int customerId) {
    try {
      DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
      if (instance.getUserRole(customerId) == 3) {
        List<Integer> allAccountIds = instance.getAccountIdsList(customerId);

        int i = 0;
        int size = allAccountIds.size();
        BigDecimal accountBalance = new BigDecimal("0.00");

        while (size != 0) {
          Account account = instance.getAccountObject(allAccountIds.get(i));

          if (account != null) {
            accountBalance = accountBalance.add(account.getBalance());
          }
          size--;
          i++;

        }
        super.makeDialog(null,
            "Total amount of money in the accounts: $" + accountBalance.toString(), null, null);
        currentAdmin.sendMessage(customerId, "An admin has viewed the total balance "
            + "of your accounts.");
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
      System.out.println("The input was not valid :-( Try again!");
    }
  }

  private void promoteTellerDialog() {
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    DialogInterface.OnClickListener callback = new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        try {
          promoteTellerOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog("Promoting teller...", "Please enter a teller id:", Arrays.asList(idInput),
        callback);
  }

  private void promoteTellerOnOK(int tellerId) {
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    int role = instance.getUserRole(tellerId);
    instance.close();
    if (role == RolesMap.getInstance(null).getId(Roles.TELLER)) {
      DatabaseUpdateHelper instance2 = DatabaseUpdateHelper.getInstance(null);
      instance2.updateUserRole(RolesMap.getInstance(null).getId(Roles.ADMIN), tellerId);
    } else {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    }
    Toast.makeText(getApplicationContext(), "Promoted teller to admin!",
        Toast.LENGTH_SHORT).show();
  }

  /**
   * Prompts user to select a message to view.
   */
  protected void viewMessagesDialog() {
    String messageIdString = currentAdmin.PrintMessageIds();
    if (messageIdString.equalsIgnoreCase("")) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_no_messages),
          Toast.LENGTH_SHORT).show();
      return;
    }

    String[] temp = messageIdString.split("\n");
    // Temporary list:
    List<String> messageIds = Arrays.asList(temp);

    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          viewMessagesDialogOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };
    super.viewMessagesDialog(idInput, messageIds, callback);
  }

  /**
   * Outputs a dialog that prints the message of the
   *
   * @param messageId The id of the message to output
   */
  private void viewMessagesDialogOnOK(int messageId) {
    String message = AdminTerminal.viewMyMessages(currentAdmin, messageId);
    if (message.isEmpty()) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    } else {
      super.makeDialog(getString(R.string.dialog_view_messages_success_title),
          message, null, null);
    }
  }

  /**
   * Outputs a dialog where the teller can send a message to another customer.
   */
  private void sendMessageDialog() {
    // Inputs
    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    ;
    idInput.setHint(getString(R.string.hint_id));

    final EditText messageInput = new EditText(getApplicationContext());
    messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
    messageInput.setHint(getString(R.string.hint_message));

    // Callback method when they press send
    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          sendMessageToCustomer(Integer.parseInt(idInput.getText().toString()),
              messageInput.getText().toString());
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.sendMessageDialog(messageInput, idInput, callback);
  }

  /**
   * Attempts to send a message to a customer.
   *
   * @param customerId The id of the customer.
   * @param message The message to send.
   */
  private void sendMessageToCustomer(int customerId, String message) {
    AdminTerminal.sendMessage(currentAdmin, customerId, message);
    super.makeDialog(null, getString(R.string.dialog_send_message_success), null, null);
  }

  private void viewUsersMessageDialog() {
    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    // Callback method when they press send
    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          chooseUserMessageDialog(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };
    super.makeDialog("Viewing user messages...",
        "Please enter the id of the user whose messages you wish to view:", Arrays.asList(idInput),
        callback);
  }

  private void chooseUserMessageDialog(int userId) {
    // Get user messages
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    final User user = instance.getUserObject(userId);

    String messageIdString = user.PrintMessageIds();
    if (messageIdString.equalsIgnoreCase("")) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_no_messages),
          Toast.LENGTH_SHORT).show();
      return;
    }

    String[] temp = messageIdString.split("\n");
    // Temporary list:
    List<String> messageIds = Arrays.asList(temp);

    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          chooseUserMessageOnOK(user, Integer.parseInt(idInput.getText().toString()));
        } catch(NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };
    super.viewMessagesDialog(idInput, messageIds, callback);
  }
  private void chooseUserMessageOnOK(User user, int messageId) {
    String message = AdminTerminal.viewOtherMessages(user, messageId);
    super.makeDialog(getString(R.string.dialog_view_messages_success_title),
        message, null, null);

  }
}
