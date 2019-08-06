/*
 * RefinerFactory.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement;

import cn.amss.semanticweb.enhancement.impl.ClassRefinerImpl;

public class RefinerFactory
{
  private RefinerFactory() {
  }

  public static ClassRefiner createClassRefiner() {
    return new ClassRefinerImpl();
  }

  // TODO: property, instance
}
