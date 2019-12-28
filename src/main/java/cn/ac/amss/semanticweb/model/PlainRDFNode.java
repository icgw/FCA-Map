/*
 * PlainRDFNode.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.model;

import cn.ac.amss.semanticweb.constant.MatchingSpec.Owner;

public class PlainRDFNode
{
  private String represent = "";
  private Owner owner      = Owner._UNKNOWN_;

  private PlainRDFNode() {}

  public PlainRDFNode(String represent, Owner owner) {
    this.represent = represent;
    this.owner     = owner;
  }

  public String getRepresent() {
    return represent;
  }

  public Owner getOwner() {
    return owner;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (null == o || getClass() != o.getClass()) return false;
    PlainRDFNode that = (PlainRDFNode) o;
    return that.represent.equals(this.represent) && that.owner == this.owner;
  }

  @Override
  public int hashCode() {
    return represent.hashCode();
  }

  @Override
  public String toString() {
    return this.represent;
  }
}
