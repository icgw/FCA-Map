/*
 * StructuralMatcher.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching;

import cn.ac.amss.semanticweb.alignment.Mapping;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;

public interface StructuralMatcher extends FcaMatcher
{
  public void mapInstances(Mapping mappings);

  public void mapCategories(Mapping mappings);

  public void mapOntProperties(Mapping mappings);

  public void mapDataTypeProperties(Mapping mappings);

  public void mapObjectProperties(Mapping mappings);

  public void mapOntClasses(Mapping mappings);

  public boolean addCommonSubject(Resource subject);

  public boolean addSubjectAnchor(String s1, String s2);

  public boolean addAllSubjectAnchors(Mapping anchors);

  public boolean addCommonPredicate(Property predicate);

  public boolean addPredicateAnchor(String s1, String s2);

  public boolean addAllPredicateAnchors(Mapping anchors);

  public boolean addCommonObject(String objectLexicalForm);

  public boolean addObjectAnchor(String s1, String s2);

  public boolean addAllObjectAnchors(Mapping anchors);
}
