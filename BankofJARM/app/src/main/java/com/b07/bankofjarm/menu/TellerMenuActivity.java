package com.b07.bankofjarm.menu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.bank.TellerTerminal;
import com.b07.bankofjarm.databasehelper.DatabaseInsertHelper;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.databasehelper.DatabaseUpdateHelper;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.user.Customer;
import com.b07.bankofjarm.user.User;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TellerMenuActivity extends MenuActivity {

  boolean hasCustomer = false;
  private TellerTerminal tellerTerminal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Get user and pw
    Intent intent = getIntent();
    int id = intent.getIntExtra(getString(R.string.EXTRA_USER_ID), -1);
    String password = intent.getStringExtra(getString(R.string.EXTRA_USER_PW));

    // Create instance of TT; authenticate
    try {
        this.tellerTerminal = new TellerTerminal(id, password);
        if (!this.tellerTerminal.isCurrentUserAuthenticated()) {
            Toast.makeText(getApplicationContext(), getText(R.string.invalid_login_toast), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            // Set welcome text
            TextView welcomeText = (TextView) findViewById(R.id.menu_customer_welcome);
            welcomeText.setText(getString(R.string.menu_welcome_text) + " " + tellerTerminal.getCurrentUser().getName() + "!");
        }
    } catch (SQLException e) {
      Toast.makeText(getApplicationContext(), getText(R.string.error_message), Toast.LENGTH_SHORT)
          .show();
      logout();
    }
  }

  /**
   * Handles a click on the Customer menu.
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
      case R.drawable.menu_send_message:
        sendMessageDialog();
        break;
      case R.drawable.menu_view_users_messages:
        viewCustomerMessagesDialog();
        break;
      case R.drawable.menu_authenticate_customer:
        authenticateDialog();
        break;
      case R.drawable.menu_create_user:
        Toast.makeText(getApplicationContext(), "Creaing user", Toast.LENGTH_SHORT).show();
        break;
      case R.drawable.menu_add_interest:
        addInterestDialog();
        break;
      case R.drawable.menu_create_account:
        Toast.makeText(getApplicationContext(), "Creating account", Toast.LENGTH_SHORT).show();
        break;
      case R.drawable.menu_link_user_account:
        linkAccountToUserDialog();
        break;
      case R.drawable.menu_check_balance:
        checkBalanceDialog();
        break;
      case R.drawable.menu_view_accounts:
        if (tellerTerminal.listAccounts().size() == 0) {
          Toast.makeText(getApplicationContext(), "This customer has no accounts", Toast.LENGTH_SHORT).show();
        } else {
          Intent intent = new Intent(this, CustomerViewAccountsActivity.class);
          DatabaseSelectHelper databaseSelectHelper = DatabaseSelectHelper.getInstance(getApplicationContext());
          Customer currentCustomer = (Customer) databaseSelectHelper.getUserObject(tellerTerminal.getCurrentCustomerId());
          intent.putExtra("currentCustomer", currentCustomer);
          startActivity(intent);
        }
        break;
      case R.drawable.menu_make_withdrawal:
        makeWithdrawalDialog();
        break;
      case R.drawable.menu_make_deposit:
        makeDepositDialog();
        break;
      case R.drawable.menu_deauthenticate_customer:
        this.hasCustomer = false;
        this.generateMenuOptions();
        break;
      default:
        Toast.makeText(getApplicationContext(), "No such action!", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  /**
   * Generate menu options for a Customer (ATM options).
   */
  @Override
  protected void generateMenuOptions() {
    super.generateMenuOptions();
    // Add option to send customers messages
    menuOptions.add(
        new MenuModel(R.drawable.menu_send_message, getString(R.string.menu_teller_send_message)));

    // Add options to authenticate, create user, add interest
    if (!this.hasCustomer) {
      menuOptions.add(new MenuModel(R.drawable.menu_authenticate_customer,
          getString(R.string.menu_teller_authenticate_customer)));
    } else {
      menuOptions.add(new MenuModel(R.drawable.menu_deauthenticate_customer,
          getString(R.string.menu_teller_close_customer_session)));
    }

    menuOptions.add(
        new MenuModel(R.drawable.menu_create_user, getString(R.string.menu_teller_create_user)));
    menuOptions.add(new MenuModel(R.drawable.menu_add_interest,
        getString(R.string.menu_teller_add_account_interest)));

    // Only if they have a customer, have the option to check balance/view accounts etc. and
    // deauth customer
    if (this.hasCustomer) {
      menuOptions.add(
          new MenuModel(R.drawable.menu_create_account,
              getString(R.string.menu_teller_create_account)));
      menuOptions.add(
          new MenuModel(R.drawable.menu_link_user_account,
              getString(R.string.menu_teller_link_user_account)));
      menuOptions.add(new MenuModel(R.drawable.menu_check_balance,
          getString(R.string.menu_customer_check_balance)));
      menuOptions.add(new MenuModel(R.drawable.menu_view_accounts,
          getString(R.string.menu_teller_view_customer_accounts)));
      menuOptions.add(new MenuModel(R.drawable.menu_make_withdrawal,
          getString(R.string.menu_customer_make_withdrawal)));
      menuOptions.add(
          new MenuModel(R.drawable.menu_make_deposit, getString(R.string.menu_customer_deposit)));
      menuOptions.add(
          new MenuModel(R.drawable.menu_view_users_messages,
              getString(R.string.menu_teller_view_customer_messages)));
    }
  }

  /**
   * Sets up a dialog to choose what account to check and then shows a dialog showing the
   * account balance.
   */
  private void checkBalanceDialog() {
    // Create input for the id field
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    // Show account balance on OK method
    DialogInterface.OnClickListener showBalanceOnOK = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // Show balance or output error
        try {
          checkBalanceDialogOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_choose_account_title),
        getString(R.string.dialog_choose_account_message),
        Arrays.asList(idInput),
        showBalanceOnOK);
  }

  /**
   * Output a dialog containing the given account balance, or an error dialog if account
   * is not reachable.
   *
   * @param accountId The id of the account
   */
  private void checkBalanceDialogOnOK(int accountId) {
    BigDecimal balance = null;
    try {
      balance = tellerTerminal.checkBalance(accountId);
    } catch (SQLException e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    }

    // Check balance is not null
    if (balance == null) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_unreachable_account),
          Toast.LENGTH_SHORT).show();
      return;
    }

    // Otherwise setup dialog to output balance
    this.makeDialog(getString(R.string.dialog_balance_title),
        getString(R.string.dialog_balance_message) + balance.toString(),
        null, null);
  }

  /**
   * Shows a dialog for make withdrawal; prompts user to enter an account id and amount to withdraw.
   */
  private void makeWithdrawalDialog() {
    // Makes inputs for inputting id and amount to withdraw
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    final EditText amountInput = new EditText(this);
    amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    amountInput.setHint(getString(R.string.hint_amount_input));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          int accountId = Integer.parseInt(idInput.getText().toString());
          BigDecimal amount = new BigDecimal(amountInput.getText().toString());
          makeWithdrawalOnOK(accountId, amount);
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_make_withdrawal_title),
        getString(R.string.dialog_make_withdrawal_message),
        Arrays.asList(idInput, amountInput),
        callback);
  }

  /**
   * Attempts to make a withdrawal for the customer.
   * Outputs a success dialog to the screen for a make withdrawal if successful; error toast
   * otherwise.
   * @param accountId The id to withdraw from
   * @param amount The amount to withdraw
   */
  private void makeWithdrawalOnOK(int accountId, BigDecimal amount) {
    try {
      if (!this.tellerTerminal.makeWithdrawal(amount, accountId)) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
        return;
      }
    } catch (SQLException e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    }

    // Successful withdrawl
    super.makeDialog(null, getString(R.string.dialog_make_withdrawal_success1) + amount.toString() +
        " " + getString(R.string.dialog_make_withdrawal_success2) + " " + accountId, null, null);
  }

  private void makeDepositDialog() {
    // Makes inputs for inputting id and amount to withdraw
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    final EditText amountInput = new EditText(this);
    amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    amountInput.setHint(getString(R.string.hint_amount_input));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          int accountId = Integer.parseInt(idInput.getText().toString());
          BigDecimal amount = new BigDecimal(amountInput.getText().toString());
          makeDepositOnOK(accountId, amount);
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_make_deposit_title),
        getString(R.string.dialog_make_deposit_message),
        Arrays.asList(idInput, amountInput),
        callback);
  }

  /**
   * Attempts to make a deposit for the customer.
   * Outputs a success dialog to the screen for a make deposit if successful; error toast
   * otherwise.
   * @param accountId The id to deposit into
   * @param amount The amount to deposit
   */
  private void makeDepositOnOK(int accountId, BigDecimal amount) {
    try {
      if (!this.tellerTerminal.makeDeposit(amount, accountId)) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
        return;
      }
    } catch (SQLException e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    }

    // Successful deposit
    super.makeDialog(null, getString(R.string.dialog_make_deposit_success1) + amount.toString() +
        " " + getString(R.string.dialog_make_deposit_success2) + " " + accountId, null, null);
  }

  /**
   * Prompts user to select a message to view.
   */
  protected void viewMessagesDialog() {
    String messageIdString = tellerTerminal.listTellerMessageIds();
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
        } catch(NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };
    super.viewMessagesDialog(idInput, messageIds, callback);
  }


  /**
   * Outputs a dialog that prints the message of the
   * @param messageId The id of the message to output
   */
  private void viewMessagesDialogOnOK(int messageId) {
    String message = tellerTerminal.viewSpecificMessage(messageId);
    if (message.isEmpty()) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    } else {
      super.makeDialog(getString(R.string.dialog_view_messages_success_title),
          tellerTerminal.viewSpecificMessage(messageId), null, null);
    }
  }
  /**
   * Outputs a dialog that prints the message of the customer.
   * @param messageId The id of the message to output
   */
  private void viewCustomersMessagesDialogOnOK(int messageId) {
    String message = tellerTerminal.viewCustomerMessage(messageId);
    if (message.isEmpty()) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
    } else {
      super.makeDialog(getString(R.string.dialog_view_messages_success_title),
          tellerTerminal.viewSpecificMessage(messageId), null, null);
    }
  }


  /**
   * Outputs a dialog where the teller can send a message to another customer.
   */
  private void sendMessageDialog() {
    // Inputs
    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);;
    idInput.setHint(getString(R.string.hint_id));

    final EditText messageInput = new EditText(getApplicationContext());
    messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
    messageInput.setHint(getString(R.string.hint_message));

    // Callback method when they press send
    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          sendMessageToCustomer(Integer.parseInt(idInput.getText().toString()), messageInput.getText().toString());
        } catch(NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.sendMessageDialog(messageInput, idInput, callback);
  }

  /**
   * Attempts to send a message to a customer.
   * @param customerId The id of the customer.
   * @param message The message to send.
   */
  private void sendMessageToCustomer(int customerId, String message) {
    this.tellerTerminal.sendMessageToCustomer(customerId, message);
    super.makeDialog(null, getString(R.string.dialog_send_message_success), null, null);
  }

  private void viewCustomerMessagesDialog() {
    String messageIdString = tellerTerminal.listCustomerMessageIds();
    if (messageIdString.equalsIgnoreCase("")) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_no_messages),
          Toast.LENGTH_SHORT).show();
      return;
    }

    String[] temp = messageIdString.split("\n");
    // Temporary list:
    List<String> messageIds = Arrays.asList(temp);

    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);;
    idInput.setHint(getString(R.string.hint_id));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          viewCustomersMessagesDialogOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch(NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.viewMessagesDialog(idInput, messageIds, callback);
  }

  /**
   * Prompts user to enter an account id to add interest to.
   */
  private void addInterestDialog() {
    // Makes inputs for inputting id
    final EditText idInput = new EditText(this);
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);
    idInput.setHint(getString(R.string.hint_id));

    // Call add
    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // Show balance or output error
        try {
          addInterestOnOK(Integer.parseInt(idInput.getText().toString()));
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_add_interest_title),
        getString(R.string.dialog_add_interest_message),
        Arrays.asList(idInput),
        callback);
  }

  /**
   * Add interest to an account with the given id.
   * @param accountId The id of the account
   */
  private void addInterestOnOK(int accountId) {
    try {
      this.tellerTerminal.giveInterestToAccount(accountId);
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
          Toast.LENGTH_SHORT).show();
      return;
    }
    // Run this when adding interest is successful:
    super.makeDialog(null, getString(R.string.dialog_add_interest_success), null, null);
  }

  private void linkAccountToUserDialog() {
    // Inputs
    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);;
    idInput.setHint(getString(R.string.hint_user_id));

    final EditText accountInput = new EditText(getApplicationContext());
    accountInput.setInputType(InputType.TYPE_CLASS_TEXT);
    accountInput.setHint(getString(R.string.hint_account_id));

    // Callback method when they press send
    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          linkAccountToUserOnOK(Integer.parseInt(idInput.getText().toString()),
              Integer.parseInt(accountInput.getText().toString()));
        } catch(NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_link_account_user_title),
        getString(R.string.dialog_link_account_user_message),
        Arrays.asList(idInput, accountInput),
        callback);
  }

  private void linkAccountToUserOnOK(int userId, int accountId) {
    this.tellerTerminal.linkUserAccount(userId, accountId);
    // Run this when adding interest is successful:
    super.makeDialog(null, getString(R.string.dialog_link_account_user_success), null, null);
  }

  /**
   * Authenticate dialog.
   */
  private void authenticateDialog() {
    final EditText idInput = new EditText(getApplicationContext());
    idInput.setInputType(InputType.TYPE_CLASS_NUMBER);;
    idInput.setHint(getString(R.string.hint_id));

    final EditText passwordInput = new EditText(getApplicationContext());
    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    passwordInput.setHint(getString(R.string.hint_password));

    DialogInterface.OnClickListener callback = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        try {
          authenticateOnOK(Integer.parseInt(idInput.getText().toString()),
              passwordInput.getText().toString());
        } catch (NumberFormatException e) {
          Toast.makeText(getApplicationContext(), getString(R.string.error_incomplete_inputs),
              Toast.LENGTH_SHORT).show();
        }
      }
    };

    super.makeDialog(getString(R.string.dialog_authenticate_customer_title),
        getString(R.string.dialog_authenticate_customer_message),
        Arrays.asList(idInput, passwordInput),
        callback);
  }

  /**
   * Attempts to authenticate a new customer.
   * @param userId
   * @param userPassword
   */
  private void authenticateOnOK(int userId, String userPassword) {
    // Get user object
    DatabaseSelectHelper instance = DatabaseSelectHelper.getInstance(null);
    User user = instance.getUserObject(userId);
    instance.close();

    if (user instanceof Customer) {
      tellerTerminal.setCurrentCustomer((Customer)user);
      try {
        if (tellerTerminal.authenticateCurrentCustomer(userPassword)) {
          hasCustomer = true;
          this.generateMenuOptions();
        } else {
          Toast.makeText(getApplicationContext(), getString(R.string.invalid_login_toast),
              Toast.LENGTH_SHORT).show();
          hasCustomer = false;
          tellerTerminal.deAuthenticateCustomer();
        }
      } catch (SQLException e1) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(getApplicationContext(), getString(R.string.dialog_authenticate_customer_fail),
          Toast.LENGTH_SHORT).show();
      hasCustomer = false;
    }
  }
}
