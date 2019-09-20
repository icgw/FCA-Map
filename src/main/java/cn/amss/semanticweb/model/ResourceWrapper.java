/*
 * ResourceWrapper.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.model;

import org.apache.jena.rdf.model.Resource;

import java.util.Objects;

public class ResourceWrapper <T extends Resource>
{
  private T   m_resource;
  private int m_from_id;

  public ResourceWrapper(T resource, int id) {
    m_resource = resource;
    m_from_id  = id;
  }

  public T getResource() {
    return m_resource;
  }

  public String getURI() {
    return m_resource.getURI();
  }

  public int getFromId() {
    return m_from_id;
  }

  @Override
  public String toString() {
    return m_resource.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResourceWrapper<?> that = (ResourceWrapper<?>) o;
    return m_from_id == that.m_from_id && Objects.equals(m_resource, that.m_resource);
  }

  @Override
  public int hashCode() {
    return m_resource.hashCode();
  }
}
