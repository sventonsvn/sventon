/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package org.sventon.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Properties.
 *
 * @author jorgen@sventon.org
 */
public class Properties implements Serializable {

  private static final long serialVersionUID = 5223103683951983199L;

  private final Map<Property, PropertyValue> properties = new HashMap<Property, PropertyValue>();

  /**
   * @param key           Key
   * @param propertyValue Value
   */
  public void put(Property key, PropertyValue propertyValue) {
    properties.put(key, propertyValue);
  }

  /**
   * @param key Key of property to get
   * @return Property
   */
  public PropertyValue get(final Property key) {
    return properties.get(key);
  }

  /**
   * @param key Key of property to get
   * @return Property as a string value
   */
  public String getStringValue(final Property key) {
    final PropertyValue value = this.get(key);
    return (value == null) ? null : value.getValue();
  }

  /**
   * @return Entries
   */
  public Set<Map.Entry<Property, PropertyValue>> entrySet() {
    return properties.entrySet();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.reflectionToString(this);
  }
}
