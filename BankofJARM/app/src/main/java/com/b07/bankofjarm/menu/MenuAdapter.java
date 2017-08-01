package com.b07.bankofjarm.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b07.bankofjarm.R;

import java.util.ArrayList;

/**
 * Created by jr on 27/07/17.
 */

public class MenuAdapter extends ArrayAdapter<MenuModel> {

  private final Context context;
  private final ArrayList<MenuModel> modelsArrayList;

  /**
   * Create an array adapter containing menu models for the menu screens.
   *
   * @param context The context of the application
   * @param modelsArrayList The list of menu options that will be displayed
   */
  public MenuAdapter(Context context, ArrayList<MenuModel> modelsArrayList) {
    super(context, R.layout.menu_item, modelsArrayList);
    this.context = context;
    this.modelsArrayList = modelsArrayList;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    // 1. Create inflater
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    // 2. Get rowView from inflater
    View rowView = null;
    rowView = inflater.inflate(R.layout.menu_item, parent, false);

    // 3. Get icon,title & counter views from the rowView
    ImageView imgView = rowView.findViewById(R.id.menu_item_icon);
    TextView titleView = rowView.findViewById(R.id.menu_item_text);

    // 4. Set the text for textView
    imgView.setImageResource(modelsArrayList.get(position).getIconId());
    titleView.setText(modelsArrayList.get(position).getMenuText());

    // 5. retrn rowView
    return rowView;
  }
}
