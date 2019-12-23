/*
 * Table.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

public abstract class Table <K, V> {
  protected Map<K, Set<V>> map;

  public Map<K, Set<V>> getMap() {
    return map;
  }

  public boolean put(K k, V v) {
    Set<V> s = map.get(k);
    if (null == s) {
      map.put(k, s = new HashSet<V>());
    }
    return s.add(v);
  }

  public boolean put(K k, Set<V> sv) {
    Set<V> s = map.get(k);
    if (null != s) return s.addAll(sv);

    map.put(k, sv);
    return true;
  }

  public Set<V> get(K k) {
    return map.get(k);
  }

  public Set<K> keySet() {
    return map.keySet();
  }

  public Set<Entry<K, Set<V>>> entrySet() {
    return map.entrySet();
  }

  public void clear() {
    map.clear();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }
}
