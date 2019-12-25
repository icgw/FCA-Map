/*
 * AbstractMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.model.ModelStorage;
import cn.ac.amss.semanticweb.model.PlainRDFNode;
import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.constant.MatchingSpec.Owner;

import java.util.Set;
import java.util.HashSet;

public abstract class AbstractMatcher
{
  protected ModelStorage source = null;
  protected ModelStorage target = null;

  protected Set<ModelStorage> otherModels = null;

  protected AbstractMatcher() {
    otherModels = new HashSet<>();
  }

  public void setSourceTarget(ModelStorage source, ModelStorage target) {
    this.source = source;
    this.target = target;
  }

  public boolean addOtherModel(ModelStorage other) {
    if (null == other) return false;
    return otherModels.add(other);
  }

  public void clearOtherModels() {
    otherModels.clear();
  }

  protected void matchPlainRDFNodes(Set<PlainRDFNode> candidatePool, Mapping mappings) {
    if (null == candidatePool || null == mappings) return;
    Set<PlainRDFNode> sources = new HashSet<>();
    Set<PlainRDFNode> targets = new HashSet<>();
    splitPlainRDFNodes(candidatePool, sources, targets);
    for (PlainRDFNode s : sources) {
      for (PlainRDFNode t : targets) {
        mappings.add(s.getRepresent(), t.getRepresent());
      }
    }
  }

  protected void splitPlainRDFNodes(Set<PlainRDFNode> candidatePool,
                                    Set<PlainRDFNode> sources, Set<PlainRDFNode> targets) {
    for (PlainRDFNode c : candidatePool) {
      Owner o = c.getOwner();
      if (Owner.SOURCE == o) {
        sources.add(c);
      }
      else if (Owner.TARGET == o) {
        targets.add(c);
      }
    }
  }
}
