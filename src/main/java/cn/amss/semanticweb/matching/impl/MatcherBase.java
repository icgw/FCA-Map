/*
 * MatcherBase.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import java.io.InputStream;

import cn.amss.semanticweb.model.ModelWrapper;
import cn.amss.semanticweb.model.ResourceWrapper;

public abstract class MatcherBase
{
  protected int m_source_id = 0;
  protected int m_target_id = 1;

  protected ModelWrapper m_source = null;
  protected ModelWrapper m_target = null;

  public MatcherBase() {
    m_source_id = 0;
    m_target_id = 1;
  }

  protected final boolean isFromSource(ResourceWrapper rw) {
    return rw.getFromId() == m_source_id;
  }

  protected final boolean isFromTarget(ResourceWrapper rw) {
    return rw.getFromId() == m_target_id;
  }

  public void init(InputStream source, InputStream target) {
    m_source = new ModelWrapper(source);
    m_target = new ModelWrapper(target);
  }

  public void init(String source, String target) {
    m_source = new ModelWrapper(source);
    m_target = new ModelWrapper(target);
  }

  public void setSourceId(int source_id) {
    m_source_id = source_id;
  }

  public void setTargetId(int target_id) {
    m_target_id = target_id;
  }

  public void close() {
    if (m_source != null) {
      m_source.close();
    }

    if (m_target != null) {
      m_target.close();
    }
  }
}
