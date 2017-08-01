package com.b07.bankofjarm.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b07.bankofjarm.R;
import com.b07.bankofjarm.account.Account;

import java.util.ArrayList;

/**
 * Created by mebec on 2017-07-31.
 */

public class ViewAccountsAdapter extends ArrayAdapter<Account> {

    private final Context context;
    private final ArrayList<Account> accountArrayList;

    /**
     * Create an array adapter containing menu models for the menu screens.
     *
     * @param context The context of the application
     * @param accountArrayList The account whose info will be displayed
     */
    public ViewAccountsAdapter(Context context, ArrayList<Account> accountArrayList) {
        super(context, R.layout.account_item, accountArrayList);
        this.context = context;
        this.accountArrayList = accountArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = null;
        rowView = inflater.inflate(R.layout.account_item, parent, false);

        // 3. Get icon,title & counter views from the rowView
        TextView imgView = rowView.findViewById(R.id.account_id);
        TextView titleView = rowView.findViewById(R.id.account_details);

        // 4. Set the text for textView
        imgView.setText(accountArrayList.get(position).getId());
        titleView.setText(accountArrayList.get(position).toString());

        // 5. return rowView
        return rowView;
    }
}
