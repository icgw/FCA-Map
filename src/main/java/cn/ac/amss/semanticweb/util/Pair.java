/*
 * Pair.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

public class Pair <K, V>
{
  private final K m_key;
  private final V m_value;

  public Pair(K key, V value) {
    m_key   = key;
    m_value = value;
  }

  public K getKey() {
    return m_key;
  }

  public V getValue() {
    return m_value;
  }

  @Override
  public String toString() {
    return "<" + m_key + ", " + m_value + ">";
  }

  @Override
  public int hashCode() {
    return (m_key == null ? 0 : m_key.hashCode() * 31) +
           (m_value == null ? 0 : m_value.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) o;
      if (m_key != null ? !m_key.equals(p.m_key) : p.m_key != null) return false;
      if (m_value != null ? !m_value.equals(p.m_value) : p.m_value != null) return false;
      return true;
    }
    return false;
  }
}
