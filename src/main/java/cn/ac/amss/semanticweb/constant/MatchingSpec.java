/*
 * MatchingSpec.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.constant;

public class MatchingSpec
{
  public enum Owner {
    SOURCE,
    TARGET,
    DUMMY,
    _UNKNOWN_;
  }

  public enum MatchType {
    INSTANCE,
    CATEGORY,
    ONT_PROPERTY,
    DATA_TYPE_PROPERTY,
    OBJECT_PROPERTY,
    ONT_CLASS,
    _UNKNOWN_;
  }

  public enum SPOPart {
    AS_SUBJECT,
    AS_PREDICATE,
    AS_OBJECT,
    _UNKNOWN_;
  }

  private MatchingSpec() {}
}
