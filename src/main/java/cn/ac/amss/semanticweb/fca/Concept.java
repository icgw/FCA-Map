/*
 * Concept.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.fca;

import java.util.Set;
import java.util.HashSet;

/**
 * A (formal) concept is a pair (A, B), where A is the extent and B is the intent.
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class Concept <O, A>
{
  private final Set<O> extent;
  private final Set<A> intent;

  /**
   * Concept constructor.
   *
   * @param extent the extent of this concept
   * @param intent the intent of this concept
   */
  public Concept(Set<O> extent, Set<A> intent) {
    this.extent = new HashSet<>(extent);
    this.intent = new HashSet<>(intent);
  }

  /**
   * @return the extent of this concept
   */
  public final Set<O> getExtent() {
    return extent;
  }

  /**
   * @return the intent of this concept
   */
  public final Set<A> getIntent() {
    return intent;
  }

  @Override
  public String toString() {
    return String.format("%n>>>>>>>%nExtent: %s%nIntent: %s%n<<<<<<<", extent, intent);
  }

  @Override
  public int hashCode() {
    return (null == extent ? 0 : extent.hashCode() * 31) + (null == intent ? 0 : intent.hashCode());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Concept) {
      Concept<?, ?> that = (Concept<?, ?>) o;
      if (null != extent ? !extent.equals(that.extent) : null != that.extent) return false;
      if (null != intent ? !intent.equals(that.intent) : null != that.intent) return false;
      return true;
    }
    return false;
  }
}
