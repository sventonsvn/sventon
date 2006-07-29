/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package de.berlios.sventon.web.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract file represenation class.
 *
 * @author jesper@users.berlios.de
 */
public class AbstractFile {

  final Map<String, Object> model = new HashMap<String, Object>();

  /**
   * Gets the model.
   *
   * @return The model.
   */
  public Map<String, Object> getModel() {
    return model;
  }

}
