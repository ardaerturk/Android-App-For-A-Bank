package com.b07.bankofjarm.genericsmap;

import android.content.Context;

/**
 * Created by jr on 30/07/17.
 */

public interface GenericsMap <E extends Enum<E>>{

  /**
   * Returns the id of a role in the database.
   *
   * @param element The enum constant role
   * @return the roleId
   */
  int getId(E element);

  /**
   * Query the database and update roles map values.
   */
  void update(Context context);
}
