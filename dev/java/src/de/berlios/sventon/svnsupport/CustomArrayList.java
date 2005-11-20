/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.svnsupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Customized array list that allows easy update of elements.
 *
 * @author jesper@users.berlios.de
 */
public class CustomArrayList<E> extends ArrayList<E> implements List<E> {

  private static final long serialVersionUID = -5294175612138594741L;

  public void update(final int index, final E element) {
    super.remove(index);
    super.add(index, element);
  }

}
