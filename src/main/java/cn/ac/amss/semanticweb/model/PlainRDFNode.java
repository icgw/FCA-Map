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
  private Owner owner      = Owner.UNKNOWN;

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
}
