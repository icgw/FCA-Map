/*
 * AbstractMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.model.ModelStorage;

import java.util.Set;
import java.util.HashSet;

public abstract class AbstractMatcher
{
  protected ModelStorage source = null;
  protected ModelStorage target = null;

  protected Set<ModelStorage> dummyModels = null;

  protected AbstractMatcher() {
    dummyModels = new HashSet<>();
  }

  public void setSourceTarget(ModelStorage source, ModelStorage target) {
    this.source = source;
    this.target = target;
  }

  public boolean addDummyModel(ModelStorage dummy) {
    if (null == dummy) return false;
    return dummyModels.add(dummy);
  }

  public void clearDummyModels() {
    dummyModels.clear();
  }

  public void close() {
    if (null != source) source.clear();

    if (null != target) target.clear();

    for (ModelStorage m : dummyModels) m.clear();

    dummyModels.clear();
  }
}
