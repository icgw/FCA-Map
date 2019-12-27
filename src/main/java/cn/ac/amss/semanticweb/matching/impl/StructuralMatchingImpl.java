/*
 * StructuralMatchingImpl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.alignment.MappingCell;
import cn.ac.amss.semanticweb.util.Pair;
import cn.ac.amss.semanticweb.util.Table;
import cn.ac.amss.semanticweb.constant.MatchingSpec.Owner;
import cn.ac.amss.semanticweb.constant.MatchingSpec.MatchType;
import cn.ac.amss.semanticweb.constant.MatchingSpec.SPOPart;
import cn.ac.amss.semanticweb.model.PlainRDFNode;
import cn.ac.amss.semanticweb.model.ModelStorage;
import cn.ac.amss.semanticweb.fca.Context;
import cn.ac.amss.semanticweb.fca.Hermes;

import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.ontology.OntModel;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

public class StructuralMatchingImpl extends AbstractMatcherByFCA
{
  private class LookupTable extends Table<String, Integer> {
    public LookupTable() { super(); }
  }

  private class Anchor extends Pair<String, String> {
    public Anchor(String key, String value) {
      super(key, value);
    }
  }

  private class AnchorIdPair extends Pair<Integer, Integer> {
    public AnchorIdPair(int key, int value) {
      super(key, value);
    }
  }

  private Map<Anchor, Integer> subjectAnchors   = null;
  private Map<Anchor, Integer> predicateAnchors = null;
  private Map<Anchor, Integer> objectAnchors    = null;

  public StructuralMatchingImpl() {
    super();
    isEnabledGSH     = true;
    isEnabledLattice = true;

    lowerBoundOfGSHObjectsSize = 2;
    upperBoundOfGSHObjectsSize = 2;

    lowerBoundOfGSHAttributesSize = 0;
    upperBoundOfGSHAttributesSize = -1;

    lowerBoundOfLatticeObjectsSize = 2;
    upperBoundOfLatticeObjectsSize = 2;

    lowerBoundOfLatticeAttributesSize = 1;
    upperBoundOfLatticeAttributesSize = -1;

    subjectAnchors   = new HashMap<>();
    predicateAnchors = new HashMap<>();
    objectAnchors    = new HashMap<>();
  }

  public boolean addCommonSubject(Resource subject) {
    return addSubjectAnchor(subject.getURI(), subject.getURI());
  }

  public boolean addSubjectAnchor(String s1, String s2) {
    return addAnchor(SPOPart.AS_SUBJECT, s1, s2);
  }

  public boolean addAllSubjectAnchors(Mapping anchors) {
    return addAllAnchors(SPOPart.AS_SUBJECT, anchors);
  }

  public boolean addCommonPredicate(Property predicate) {
    return addPredicateAnchor(predicate.getURI(), predicate.getURI());
  }

  public boolean addPredicateAnchor(String s1, String s2) {
    return addAnchor(SPOPart.AS_PREDICATE, s1, s2);
  }

  public boolean addAllPredicateAnchors(Mapping anchors) {
    return addAllAnchors(SPOPart.AS_PREDICATE, anchors);
  }

  public boolean addCommonObject(String objectLexicalForm) {
    return addObjectAnchor(objectLexicalForm, objectLexicalForm);
  }

  public boolean addObjectAnchor(String s1, String s2) {
    return addAnchor(SPOPart.AS_OBJECT, s1, s2);
  }

  public boolean addAllObjectAnchors(Mapping anchors) {
    return addAllAnchors(SPOPart.AS_OBJECT, anchors);
  }

  public void mapInstances(Mapping mappings) {
    mapResources(MatchType.INSTANCE, mappings);
  }

  public void mapCategories(Mapping mappings) {
    mapResources(MatchType.CATEGORY, mappings);
  }

  public void mapOntProperties(Mapping mappings) {
    mapResources(MatchType.ONT_PROPERTY, mappings);
  }

  public void mapDataTypeProperties(Mapping mappings) {
    mapResources(MatchType.DATA_TYPE_PROPERTY, mappings);
  }

  public void mapObjectProperties(Mapping mappings) {
    mapResources(MatchType.ONT_PROPERTY, mappings);
  }

  public void mapOntClasses(Mapping mappings) {
    mapResources(MatchType.ONT_CLASS, mappings);
  }

  private boolean addAnchor(SPOPart part, String s1, String s2) {
    boolean modified = false;
    Map<Anchor, Integer> temp = null;
    switch (part) {
      case AS_SUBJECT:
        temp = subjectAnchors;
        break;
      case AS_PREDICATE:
        temp = predicateAnchors;
        break;
      case AS_OBJECT:
        temp = objectAnchors;
        break;
      default:
        return false;
    }

    if (null == temp) return false;
    Anchor a = new Anchor(s1, s2);

    int i = temp.size();
    if (SPOPart.AS_PREDICATE == part) {
      i *= (-1);
      i -= 1;
    }

    if (!temp.containsKey(a)) {
      temp.put(a, i);
      modified = true;
    }
    return modified;
  }

  private boolean addAnchor(SPOPart part, MappingCell anchor) {
    return addAnchor(part, anchor.getEntity1(), anchor.getEntity2());
  }

  private boolean addAllAnchors(SPOPart part, Mapping anchors) {
    boolean modified = false;
    for (MappingCell anchor : anchors) {
      if (addAnchor(part, anchor)) modified = true;
    }
    return modified;
  }

  private <T extends RDFNode> String getRepresent(T r) {
    if (r.isURIResource() || r.isResource() || r.isAnon()) {
      return r.asNode().getURI();
    }
    else if (r.isLiteral()) {
      return r.asLiteral().getLexicalForm();
    }
    return "null";
  }

  private <X extends Model> Set<AnchorIdPair> getAnchorIdPairs(PlainRDFNode plainRDFNode, SPOPart part, X model,
                                                               LookupTable subject2AnchorIds,
                                                               LookupTable predicate2AnchorsIds,
                                                               LookupTable object2AnchorsIds) {
    Set<AnchorIdPair> anchorIdPairs = new HashSet<>();
    switch (part) {
      case AS_SUBJECT:
        if (0 == predicate2AnchorsIds.size() || 0 == object2AnchorsIds.size()) break;
        Resource sub = model.getResource(plainRDFNode.getRepresent());
        for (StmtIterator it = model.listStatements(sub, null, (RDFNode) null); it.hasNext(); ) {
          Statement stmt = it.nextStatement();

          String p = getRepresent(stmt.getPredicate());
          if (!predicate2AnchorsIds.containsKey(p)) continue;

          String o = getRepresent(stmt.getObject());
          if (!object2AnchorsIds.containsKey(o)) continue;

          putAll(anchorIdPairs, predicate2AnchorsIds.get(p), object2AnchorsIds.get(o));
        }
        break;
      case AS_PREDICATE:
        if (0 == subject2AnchorIds.size() || 0 == object2AnchorsIds.size()) break;
        Property pre = model.getProperty(plainRDFNode.getRepresent());
        for (StmtIterator it = model.listStatements(null, pre, (RDFNode) null); it.hasNext(); ) {
          Statement stmt = it.nextStatement();

          String s = getRepresent(stmt.getSubject());
          if (!subject2AnchorIds.containsKey(s)) continue;

          String o = getRepresent(stmt.getObject());
          if (!object2AnchorsIds.containsKey(o)) continue;

          putAll(anchorIdPairs, subject2AnchorIds.get(s), object2AnchorsIds.get(o));
        }
        break;
      case AS_OBJECT:
        if (0 == subject2AnchorIds.size() || 0 == predicate2AnchorsIds.size()) break;
        RDFNode obj = (RDFNode) model.getResource(plainRDFNode.getRepresent());
        for (StmtIterator it = model.listStatements(null, null, obj); it.hasNext(); ) {
          Statement stmt = it.nextStatement();

          String s = getRepresent(stmt.getSubject());
          if (!subject2AnchorIds.containsKey(s)) continue;

          String p = getRepresent(stmt.getPredicate());
          if (!predicate2AnchorsIds.containsKey(p)) continue;

          putAll(anchorIdPairs, subject2AnchorIds.get(s), predicate2AnchorsIds.get(p));
        }
        break;
      default:
        break;
    }
    return anchorIdPairs;
  }

  private void mapResources(MatchType type, Mapping mappings) {
    Context<PlainRDFNode, AnchorIdPair> context = new Context<>();

    LookupTable subject2AnchorIds    = new LookupTable();
    constructLookupTable(subjectAnchors, subject2AnchorIds);

    LookupTable predicate2AnchorsIds = new LookupTable();
    constructLookupTable(predicateAnchors, predicate2AnchorsIds);

    LookupTable object2AnchorsIds    = new LookupTable();
    constructLookupTable(objectAnchors, object2AnchorsIds);

    constructAnchorPairBasedContext(type, context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);

    Hermes<PlainRDFNode, AnchorIdPair> hermes = new Hermes<>();
    hermes.init(context);

    if (isEnabledGSH) {
      Set<Set<PlainRDFNode>> simplifiedExtents = hermes.listSimplifiedExtentsLeastMost(lowerBoundOfGSHObjectsSize,
                                                                                       upperBoundOfGSHObjectsSize,
                                                                                       lowerBoundOfGSHAttributesSize,
                                                                                       upperBoundOfGSHAttributesSize);
      for (Set<PlainRDFNode> candidatePool : simplifiedExtents) {
        matchPlainRDFNodes(candidatePool, mappings);
      }
    }

    if (isEnabledLattice) {
      Set<Set<PlainRDFNode>> extents = hermes.listExtentsLeastMost(lowerBoundOfLatticeObjectsSize,
                                                                   upperBoundOfLatticeObjectsSize,
                                                                   lowerBoundOfLatticeAttributesSize,
                                                                   upperBoundOfLatticeAttributesSize);
      for (Set<PlainRDFNode> candidatePool : extents) {
        matchPlainRDFNodes(candidatePool, mappings);
      }
    }

    hermes.close();
  }

  private void putAll(Set<AnchorIdPair> anchorIdPairs, Set<Integer> leftHandAnchorIds, Set<Integer> rightHandAnchorIds) {
    if (null == anchorIdPairs || null == leftHandAnchorIds || null == rightHandAnchorIds) return;
    for (int lhi : leftHandAnchorIds) {
      for (int rhi : rightHandAnchorIds) {
        anchorIdPairs.add(new AnchorIdPair(lhi, rhi));
      }
    }
  }

  private void constructAnchorPairBasedContext(MatchType type, Context<PlainRDFNode, AnchorIdPair> context,
                                               LookupTable subject2AnchorIds,
                                               LookupTable predicate2AnchorsIds,
                                               LookupTable object2AnchorsIds) {
    switch (type) {
      case INSTANCE:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getIndividuals(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getIndividuals(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getIndividuals(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      case CATEGORY:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getCategories(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getCategories(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getCategories(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      case ONT_PROPERTY:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getOntProperties(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getOntProperties(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getOntProperties(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      case DATA_TYPE_PROPERTY:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getDataTypeProperties(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getDataTypeProperties(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getDataTypeProperties(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      case OBJECT_PROPERTY:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getObjectProperties(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getObjectProperties(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getObjectProperties(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      case ONT_CLASS:
        constructAnchorPairBasedContext(Owner.SOURCE, this.source.getOntClasses(), this.source.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        constructAnchorPairBasedContext(Owner.TARGET, this.target.getOntClasses(), this.target.getOntModel(),
                                        context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        for (ModelStorage other : this.otherModels) {
          constructAnchorPairBasedContext(Owner.DUMMY, other.getOntClasses(), other.getOntModel(),
                                          context, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds);
        }
        break;
      default:
        break;
    }
  }

  private <T extends Resource, X extends Model> void
  constructAnchorPairBasedContext(Owner owner, Set<T> resources, X model,
                                  Context<PlainRDFNode, AnchorIdPair> context,
                                  LookupTable subject2AnchorIds,
                                  LookupTable predicate2AnchorsIds,
                                  LookupTable object2AnchorsIds) {
    for (T r : resources) {
      PlainRDFNode prn = new PlainRDFNode(r.getURI(), owner);

      Set<AnchorIdPair> attrs = new HashSet<>();

      attrs.addAll(getAnchorIdPairs(prn, SPOPart.AS_SUBJECT, model, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds));

      attrs.addAll(getAnchorIdPairs(prn, SPOPart.AS_PREDICATE, model, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds));

      attrs.addAll(getAnchorIdPairs(prn, SPOPart.AS_OBJECT, model, subject2AnchorIds, predicate2AnchorsIds, object2AnchorsIds));

      context.put(prn, attrs);
    }
  }

  private void constructLookupTable(Map<Anchor, Integer> anchors, LookupTable string2AnchorIds) {
    for (Entry<Anchor, Integer> a : anchors.entrySet()) {
      Anchor anchor = a.getKey();
      int i = a.getValue();
      string2AnchorIds.put(anchor.getKey(), i);
      string2AnchorIds.put(anchor.getValue(), i);
    }
  }
}
