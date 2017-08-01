package com.b07.bankofjarm.menu;

/**
 * Created by jr on 27/07/17.
 */

public class MenuModel {

  private int iconId;
  private String menuText;

  public String getMenuText() {
    return menuText;
  }

  public int getIconId() {
    return iconId;
  }

  public MenuModel(int iconId, String menuText) {
    this.iconId = iconId;
    this.menuText = menuText;
  }
}
