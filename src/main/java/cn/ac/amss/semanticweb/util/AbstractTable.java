/*
 * AbstractTable.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.util;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

public abstract class AbstractTable <K, V> {
  protected Map<K, Set<V>> map;

  public AbstractTable() {
    map = new HashMap<>();
  }

  public Map<K, Set<V>> getMap() {
    return new HashMap<>(map);
  }

  public int size() {
    return map.size();
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

  public Collection<Set<V>> values() {
    return map.values();
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

  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  @Override
  public String toString() {
    return map.toString();
  }
}
