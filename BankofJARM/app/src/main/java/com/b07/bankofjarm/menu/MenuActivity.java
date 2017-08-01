package com.b07.bankofjarm.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;
import com.b07.bankofjarm.login.LoginActivity;
import com.b07.bankofjarm.R;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuActivity extends AppCompatActivity {

  protected MenuAdapter adapter;
  protected ListView menu;
  protected ArrayList<MenuModel> menuOptions;

  /**
   * Create an instance of a generic menu. Every type of user will have their own menu.
   *
   * @param savedInstanceState the saved instance state.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu);

    // Sets the menu adapter and sets up the list view
    this.generateMenuOptions();
    this.adapter = new MenuAdapter(this, this.menuOptions);
    this.menu = (ListView) findViewById(R.id.menu_items);
    this.menu.setAdapter(this.adapter);

    // Here we handle what happens for each menu item
    menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        handleClick(position);
      }
    });
  }

  /**
   * Given the position of a menu item clicked in the ListView, perform the appropriate action
   * (e.g. start a new activity or make a toast).
   *
   * @param position the position, starting from 0, in the listview of the item clicked
   */
  protected abstract void handleClick(int position);

  /**
   * Returns a list of the menu options, detailed by the menu icon id and menu text, of the current
   * menu. This generic class only has the view messages menu option since every type of user
   * can currently view messages.
   */
  protected void generateMenuOptions() {
    // Every menu will have the ability to view messages
    menuOptions = new ArrayList<>();
    menuOptions
        .add(new MenuModel(R.drawable.menu_view_messages, getString(R.string.menu_view_messages)));
    this.adapter = new MenuAdapter(this, this.menuOptions);
    this.menu = (ListView) findViewById(R.id.menu_items);
    this.menu.setAdapter(this.adapter);
  }

  /**
   * Adds a logout button to the action bar.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
  }

  /**
   * Handles the action for the logout button.
   *
   * @param item The item in the action bar being pressed; logout.
   * @return true
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.logout:
        logout();
        break;
    }
    return true;
  }


  /**
   * Log outs the current user.
   */
  protected void logout() {
    finish();
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
    // Simply just gets rid of annoying transition animation
    this.overridePendingTransition(0, 0);
  }

  /**
   * Generic method to create a dialog.
   *
   * @param title The title of the dialog
   * @param message The message of the dialog
   * @param inputs The list of the edittexts inside the dialog.
   * @param callback The method to call when OK is pressed
   */
  protected void makeDialog(String title, String message, List<EditText> inputs,
      DialogInterface.OnClickListener callback) {
    // Create dialog box
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(title);
    dialog.setMessage(message);

    // Create view to store inputs, if there are some
    if (inputs != null) {
      LinearLayout inputLayout = new LinearLayout(this);
      inputLayout.setOrientation(LinearLayout.VERTICAL);

      // Set padding for the dialog
      int padding = getResources().getDimensionPixelSize(R.dimen.activity_padding);
      inputLayout.setPadding(padding, 0, padding, 0);
      for (EditText input : inputs) {
        inputLayout.addView(input);
      }

      dialog.setView(inputLayout);
    }

    // Set OK button and show
    dialog.setPositiveButton(R.string.dialog_ok, callback);
    dialog.show();
  }

  /**
   * Views message ids and prompts user to enter the id of the message they want to view.
   * @param messageIds The list of message ids the user can view
   * @param callback The method to execute when OK is pressed
   */
  protected void viewMessagesDialog(EditText idInput, List<String> messageIds,
      DialogInterface.OnClickListener callback) {
    // Toast if there are no messages
    if (messageIds == null || messageIds.size() == 0) {
      Toast.makeText(getApplicationContext(), getString(R.string.error_no_messages),
          Toast.LENGTH_SHORT).show();
      return;
    }
    // Create dialog box
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(getString(R.string.dialog_view_messages_title));
    dialog.setMessage(getString(R.string.dialog_view_messages_message));

    // Create view to store message id list and inputs
    LinearLayout inputLayout = new LinearLayout(this);
    inputLayout.setOrientation(LinearLayout.VERTICAL);
    // Set padding for the dialog
    int padding = getResources().getDimensionPixelSize(R.dimen.activity_padding);
    inputLayout.setPadding(padding, 0, padding, 0);

    // Add id input box
    inputLayout.addView(idInput);

    // Add message view
    ListView messageList = new ListView(getApplicationContext());
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, messageIds);
    messageList.setAdapter(adapter);
    inputLayout.addView(messageList);


    dialog.setView(inputLayout);

    // Set OK button and show
    dialog.setPositiveButton(R.string.dialog_ok, callback);
    dialog.show();
  }

  /**
   * Displays a message dialog where you can type in a message to send to a user.
   * @param messageInput
   * @param idInput
   * @param callback
   */
  protected void sendMessageDialog(EditText messageInput, EditText idInput, DialogInterface.OnClickListener callback) {
    // Create dialog box
    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
    dialog.setTitle(getString(R.string.dialog_send_message_title));
    dialog.setMessage(getString(R.string.dialog_send_message_message));

    if (messageInput != null && idInput != null) {
      LinearLayout inputLayout = new LinearLayout(this);
      inputLayout.setOrientation(LinearLayout.VERTICAL);

      // Set padding for the dialog
      int padding = getResources().getDimensionPixelSize(R.dimen.activity_padding);
      inputLayout.setPadding(padding, 0, padding, 0);
      inputLayout.addView(idInput);
      inputLayout.addView(messageInput);

      dialog.setView(inputLayout);
    }

    // Set OK button and show
    dialog.setPositiveButton(R.string.dialog_send, callback);
    dialog.show();
  }
}
