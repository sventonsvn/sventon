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
