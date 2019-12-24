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

  public void setSourceTarget(String sourceFilenameOrURI, String targetFilenameOrURI) {
    this.source = new ModelStorage(sourceFilenameOrURI);
    this.target = new ModelStorage(targetFilenameOrURI);
  }

  public boolean addOtherModel(String otherFilenameOrURI) {
    if (null == otherFilenameOrURI) return false;
    return otherModels.add(new ModelStorage(otherFilenameOrURI));
  }

  public void clearOtherModels() {
    otherModels.clear();
  }

  public void close() {
    if (null != source) source.clear();
    if (null != target) target.clear();
    for (ModelStorage m : otherModels) m.clear();
    otherModels.clear();
  }

  protected void matchPlainRDFNodes(Set<PlainRDFNode> candidatePool, Mapping mappings) {
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
