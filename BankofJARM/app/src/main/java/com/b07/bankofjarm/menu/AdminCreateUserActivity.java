package com.b07.bankofjarm.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.bank.AdminTerminal;
import com.b07.bankofjarm.user.Admin;
import com.b07.bankofjarm.user.Teller;

import java.sql.SQLException;

public class AdminCreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    Button createUserButton;
    EditText nameInput;
    EditText passwordInput;
    EditText addressInput;
    EditText ageInput;
    int ageInputInt;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_user);

        nameInput = (EditText) findViewById(R.id.userName);
        passwordInput = (EditText) findViewById(R.id.userPassword);
        addressInput = (EditText) findViewById(R.id.userAddress);
        ageInput = (EditText) findViewById(R.id.userAge);
        try{
            ageInputInt = Integer.parseInt(ageInput.getText().toString());
        } catch(NumberFormatException ex) {
            ex.printStackTrace();
        }
        createUserButton = (Button) findViewById(R.id.createUserButton);

        createUserButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.v("EditText", nameInput.getText().toString());
                        Log.v("EditText", passwordInput.getText().toString());
                        Log.v("EditText", addressInput.getText().toString());
                        Log.v("EditText", ageInput.getText().toString());
                    }
                });





        Spinner mySpinner = (Spinner) findViewById(R.id.adminOrTellerSpinner);

        text = mySpinner.getSelectedItem().toString();


        createUserButton.setOnClickListener(AdminCreateUserActivity.this);
    }


    @Override
    public void onClick(View v)
    {
        if (v == createUserButton)
            try {
                if (text.equals("Teller")) {
                    AdminTerminal.makeNewTeller(nameInput.toString(), ageInputInt, addressInput.toString(), passwordInput.toString());

                } else if (text.equals("Admin")) {
                    AdminTerminal.makeNewAdmin(nameInput.toString(), ageInputInt, addressInput.toString(), passwordInput.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return;

    }

}
