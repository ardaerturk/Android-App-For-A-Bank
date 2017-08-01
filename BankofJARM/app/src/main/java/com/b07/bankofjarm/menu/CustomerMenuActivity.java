package com.b07.bankofjarm.menu;

import android.accounts.Account;
import android.content.Intent;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.b07.bankofjarm.R;

import com.b07.bankofjarm.bank.ATM;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerMenuActivity extends MenuActivity {

  private ATM atm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Get user and pw
    Intent intent = getIntent();
    int id = intent.getIntExtra(getString(R.string.EXTRA_USER_ID), -1);
    String password = intent.getStringExtra(getString(R.string.EXTRA_USER_PW));

    // Create instance of ATM; authenticate
    try {
        this.atm = new ATM(id, password);
        if (!this.atm.authenticate(id, password)) {
            Toast.makeText(getApplicationContext(), getText(R.string.invalid_login_toast), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            // Set welcome text
            TextView welcomeText = (TextView) findViewById(R.id.menu_customer_welcome);
            welcomeText.setText(getString(R.string.menu_welcome_text) + " " + atm.getCurrentCustomer().getName() + "!");
        }
    } catch (SQLException e) {
        Toast.makeText(getApplicationContext(), getText(R.string.error_message), Toast.LENGTH_SHORT).show();
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
      case R.drawable.menu_check_balance:
        checkBalanceDialog();
        break;
      case R.drawable.menu_view_accounts:
        // Check if there are any accounts
        if (atm.listAccounts().size() == 0) {
          Toast.makeText(getApplicationContext(), "You have no accounts to view", Toast.LENGTH_SHORT).show();
        } else {
          Intent intent = new Intent(this, CustomerViewAccountsActivity.class);
          intent.putExtra("currentCustomer", atm.getCurrentCustomer());
          startActivity(intent);
        }
        break;
      case R.drawable.menu_make_withdrawal:
        makeWithdrawalDialog();
        break;
      case R.drawable.menu_make_deposit:
        makeDepositDialog();
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

    // Add options to check balance, view accounts, make withdrawal and make deposit
    menuOptions.add(new MenuModel(R.drawable.menu_check_balance,
        getString(R.string.menu_customer_check_balance)));
    menuOptions.add(new MenuModel(R.drawable.menu_view_accounts,
        getString(R.string.menu_customer_view_accounts)));
    menuOptions.add(new MenuModel(R.drawable.menu_make_withdrawal,
        getString(R.string.menu_customer_make_withdrawal)));
    menuOptions.add(
        new MenuModel(R.drawable.menu_make_deposit, getString(R.string.menu_customer_deposit)));
  }












//    // UNCOMMENT WHEN THE FILES ARE MERGED
//    // List<Account> accounts = atm.listAccounts();
//
//
//    if (accounts == null) {m
//      Toast.makeText(getApplicationContext(), getString(R.string.error_no_accounts),
//              Toast.LENGTH_SHORT).show();
//      return;
//    }
//    int size = 0;
//    while (accounts != null);
//    // Otherwise setup dialog to output balance
//    this.makeDialog(getString(R.string.dialog_view_account),
//            getString(R.string.dialog_account_id) + accounts[i].id +
//                    getString(R.string.dialog_account_type) + accounts[i].type.toString(), getString(R.string.dialog_account_name) + accounts[i].name.toString(),
//            getString(R.string.dialog_account_balance) + accounts[i].balance.toString());
//            size++;
//  }
//
//






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
      balance = atm.checkBalance(accountId);
    } catch (SQLException e) {
      // TODO: catch
      e.printStackTrace();
    }
    // Temporary balance showing just accountId; DELETE WHEN JAVA IS MERGED
    //BigDecimal balance = new BigDecimal(accountId);

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
      if (!this.atm.makeWithdrawal(amount, accountId)) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
        return;
      }
    } catch (SQLException e) {
      // TODO: catch
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
      if (!this.atm.makeDeposit(amount, accountId)) {
        Toast.makeText(getApplicationContext(), getString(R.string.error_operation_unsuccessful),
            Toast.LENGTH_SHORT).show();
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Successful deposit
    super.makeDialog(null, getString(R.string.dialog_make_deposit_success1) + amount.toString() +
        " " + getString(R.string.dialog_make_deposit_success2) + " " + accountId, null, null);
  }

  /**
   * Prompts user to select a message to view.
   */
  private void viewMessagesDialog() {
    String messageIdString = atm.listMessageIds();
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
    super.makeDialog(getString(R.string.dialog_view_messages_success_title),
        atm.printMessage(messageId), null, null);
  }
}
