package com.b07.bankofjarm.login;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.databasehelper.DatabaseInsertHelper;
import com.b07.bankofjarm.databasehelper.DatabaseSelectHelper;
import com.b07.bankofjarm.databasehelper.DatabaseUpdateHelper;
import com.b07.bankofjarm.databasehelper.InitializeDatabase;
import com.b07.bankofjarm.genericsmap.AccountTypesMap;
import com.b07.bankofjarm.genericsmap.RolesMap;
import com.b07.bankofjarm.menu.MenuActivityFactory;

public class LoginActivity extends AppCompatActivity {

  /**
   * Login to the bank.
   */
  protected void login(View view) {
    // Retrieve id and password
    EditText idInput = (EditText) findViewById(R.id.main_login_input_id);
    EditText passwordInput = (EditText) findViewById(R.id.main_login_input_password);

    int userId = -1;
    String userPassword = null;
    try {
      userId = Integer.parseInt(idInput.getText().toString());
      userPassword = passwordInput.getText().toString();
    } catch (Exception e) {
      invalidLoginNotification(passwordInput);
    }

    // Attempt login in MenuActivityFactory
    Class menuActivity = MenuActivityFactory.getActivity(userId, getApplicationContext());

    // Invalid login, notify and clear pw field
    if (menuActivity == null) {
      invalidLoginNotification(passwordInput);
    } else {
      Intent intent = new Intent(this, menuActivity);
      // Pass in user id and password
      intent.putExtra(getString(R.string.EXTRA_USER_ID), userId);
      intent.putExtra(getString(R.string.EXTRA_USER_PW), userPassword);
      finish();
      startActivity(intent);
      // Simply just gets rid of annoying transition animation
      this.overridePendingTransition(0, 0);
    }
  }

  /**
   * Displays a popup message when a login attempt fails.
   *
   * @param passwordInput The edittext of the password to clear after an invalid login
   */
  private void invalidLoginNotification(EditText passwordInput) {
    Toast.makeText(getApplicationContext(), getText(R.string.invalid_login_toast),
        Toast.LENGTH_SHORT).show();
    passwordInput.setText("");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Sets up all singletons needing contexts
    DatabaseSelectHelper.getInstance(getApplicationContext());
    DatabaseInsertHelper.getInstance(getApplicationContext());
    DatabaseUpdateHelper.getInstance(getApplicationContext());
    RolesMap.getInstance(getApplicationContext());
    AccountTypesMap.getInstance(getApplicationContext());

    // Set up the database connections on first run
    InitializeDatabase initializeDatabase = InitializeDatabase.getInstance(getApplicationContext());
    //initializeDatabase.reInitialize();
    //initializeDatabase.initialize();
    //initializeDatabase.close();

    // Animate the hints for login inputs
    final TextInputLayout idWrapper = (TextInputLayout) findViewById(
        R.id.main_login_input_id_wrapper);
    final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(
        R.id.main_login_input_password_wrapper);

    idWrapper.setHint(getString(R.string.hint_id));
    passwordWrapper.setHint(getString(R.string.hint_password));
  }
}
