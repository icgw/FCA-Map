/*
 * DataWrapper.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.model;

import java.util.Objects;

public class DataWrapper <T>
{
  private T m_data;
  private int m_from_id;

  private DataWrapper() { }

  public DataWrapper(T data, int id) {
    m_data    = data;
    m_from_id = id;
  }

  public T getData() {
    return m_data;
  }

  public int getFromId() {
    return m_from_id;
  }

  @Override
  public String toString() {
    return m_data.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DataWrapper<?> that = (DataWrapper<?>) o;
    return m_from_id == that.m_from_id && Objects.equals(m_data, that.m_data);
  }

  @Override
  public int hashCode() {
    return m_data.hashCode();
  }
}
