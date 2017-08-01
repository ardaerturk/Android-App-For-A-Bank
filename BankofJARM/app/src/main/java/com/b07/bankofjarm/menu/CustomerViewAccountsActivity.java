package com.b07.bankofjarm.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.account.Account;
import com.b07.bankofjarm.bank.ATM;
import com.b07.bankofjarm.user.Customer;

public class CustomerViewAccountsActivity extends AppCompatActivity {

    private Customer currentCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_view_accounts);
        Customer currentCustomer = (Customer) this.getIntent().getSerializableExtra("currentCustomer");
        ArrayList<Account> accounts = (ArrayList<Account>) currentCustomer.getAccounts();
        ViewAccountsAdapter adapter = new ViewAccountsAdapter(this, accounts);
        ListView listAccounts = (ListView) findViewById(R.id.account_items);
        listAccounts.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
