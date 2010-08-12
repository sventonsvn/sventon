/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class Properties {
  private Map<Property, PropertyValue> properties = new HashMap<Property, PropertyValue>();


  public void put(Property key, PropertyValue propertyValue) {
    properties.put(key, propertyValue);
  }

  public PropertyValue get(final Property key){
    return properties.get(key);
  }

  public String getStringValue(final Property key){
    final PropertyValue value = this.get(key);
    return (value == null) ? null : value.getValue();
  }

  public Set<Map.Entry<Property,PropertyValue>> entrySet(){
    return properties.entrySet();
  }
}
