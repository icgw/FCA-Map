/*
 * Pair.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

public class Pair <K, V>
{
  private final K key;
  private final V value;

  public Pair(K key, V value) {
    this.key   = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "<" + key + ", " + value + ">";
  }

  @Override
  public int hashCode() {
    return (key == null ? 0 : key.hashCode() * 31) +
           (value == null ? 0 : value.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) o;
      if (key != null ? !key.equals(p.key) : p.key != null) return false;
      if (value != null ? !value.equals(p.value) : p.value != null) return false;
      return true;
    }
    return false;
  }
}
