/*
 * MatcherBase.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.matching.impl;

import cn.amss.semanticweb.model.ModelWrapper;

public abstract class MatcherBase
{
  protected int m_source_id = 0;
  protected int m_target_id = 1;

  protected ModelWrapper m_source = null;
  protected ModelWrapper m_target = null;

  public MatcherBase() {
    m_source_id = 0;
    m_target_id = 0;
  }

  protected ModelWrapper readModel(String path) {
    ModelWrapper m = new ModelWrapper();
    m.setFileName(path);
    m.read();
    return m;
  }

  public void init(String source_path, String target_path) {
    m_source = readModel(source_path);
    m_target = readModel(target_path);
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
