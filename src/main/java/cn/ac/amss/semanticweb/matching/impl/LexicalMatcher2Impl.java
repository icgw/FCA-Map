/*
 * LexicalMatcher2Impl.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.matching.impl;

import cn.ac.amss.semanticweb.alignment.Mapping;
import cn.ac.amss.semanticweb.constant.MatchingSpec.MatchType;
import cn.ac.amss.semanticweb.constant.MatchingSpec.Owner;
import cn.ac.amss.semanticweb.fca.Context;
import cn.ac.amss.semanticweb.fca.Hermes;
import cn.ac.amss.semanticweb.model.PlainRDFNode;
import cn.ac.amss.semanticweb.model.ModelStorage;
import cn.ac.amss.semanticweb.text.Preprocessing;
import cn.ac.amss.semanticweb.util.Table;
import cn.ac.amss.semanticweb.vocabulary.DBkWik;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class LexicalMatcher2Impl extends AbstractMatcherByFCA
{
  private class LookUpTable extends Table<String, PlainRDFNode> {
    public LookUpTable() { super(); }
  }

  public LexicalMatcher2Impl() {
    try {
      Preprocessing.setDefault();
    } catch (IOException e) {
      System.err.println("Caught IOException: " + e.getMessage());
    }
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

  public void mapDatatypeProperties(Mapping mappings) {
    mapResources(MatchType.DATA_TYPE_PROPERTY, mappings);
  }

  public void mapObjectProperties(Mapping mappings) {
    mapResources(MatchType.OBJECT_PROPERTY, mappings);
  }

  public void mapOntClasses(Mapping mappings) {
    mapResources(MatchType.ONT_CLASS, mappings);
  }

  private void mapResources(MatchType type, Mapping mappings) {
    if (null == this.source || null == this.target || null == mappings) return;

    LookUpTable labelOrName2PlainRDFNodes = new LookUpTable();
    constructLookUpTable(type, labelOrName2PlainRDFNodes);

    Context<String, String> labelOrName2tokensContext = constructTokenBasedContext(labelOrName2PlainRDFNodes.keySet());

    Hermes<String, String> hermes = new Hermes<>();
    hermes.init(labelOrName2tokensContext);
    hermes.compute();

    if (isEnabledGSH) {
      Set<Set<String>> simplifiedExtents
        = hermes.listSimplifiedExtentsLeastMost(lowerBoundOfGSHObjectsSize,
                                                upperBoundOfGSHObjectsSize,
                                                lowerBoundOfGSHAttributesSize,
                                                upperBoundOfGSHAttributesSize);
      for (Set<String> labelsOrNames : simplifiedExtents) {
        matchPlainRDFNodes(getPlainRDFNodes(labelsOrNames, labelOrName2PlainRDFNodes), mappings);
      }
    }

    if (isEnabledLattice) {
      Set<Set<String>> extents
        = hermes.listExtentsLeastMost(lowerBoundOfLatticeObjectsSize,
                                      upperBoundOfLatticeObjectsSize,
                                      lowerBoundOfLatticeAttributesSize,
                                      upperBoundOfLatticeAttributesSize);
      for (Set<String> labelsOrNames : extents) {
        matchPlainRDFNodes(getPlainRDFNodes(labelsOrNames, labelOrName2PlainRDFNodes), mappings);
      }
    }

    hermes.close();
  }

  private Context<String, String> constructTokenBasedContext(Set<String> labelsOrNames) {
    Context<String, String> labelOrName2tokensContext = new Context<>();
    if (null == labelsOrNames) return labelOrName2tokensContext;

    for (String ln : labelsOrNames) {
      labelOrName2tokensContext.put(ln, getAllTokens(ln));
    }

    return labelOrName2tokensContext;
  }

  private void constructLookUpTable(MatchType type, LookUpTable labelOrName2PlainRDFNodes) {
    switch (type) {
      case INSTANCE:
        constructLookUpTable(Owner.SOURCE, this.source.getIndividuals(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getIndividuals(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getIndividuals(), labelOrName2PlainRDFNodes);
        }
        break;
      case CATEGORY:
        constructLookUpTable(Owner.SOURCE, this.source.getCategories(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getCategories(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getCategories(), labelOrName2PlainRDFNodes);
        }
        break;
      case ONT_PROPERTY:
        constructLookUpTable(Owner.SOURCE, this.source.getOntProperties(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getOntProperties(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getOntProperties(), labelOrName2PlainRDFNodes);
        }
        break;
      case DATA_TYPE_PROPERTY:
        constructLookUpTable(Owner.SOURCE, this.source.getDataTypeProperties(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getDataTypeProperties(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getDataTypeProperties(), labelOrName2PlainRDFNodes);
        }
        break;
      case OBJECT_PROPERTY:
        constructLookUpTable(Owner.SOURCE, this.source.getObjectProperties(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getObjectProperties(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getObjectProperties(), labelOrName2PlainRDFNodes);
        }
        break;
      case ONT_CLASS:
        constructLookUpTable(Owner.SOURCE, this.source.getOntClasses(), labelOrName2PlainRDFNodes);
        constructLookUpTable(Owner.TARGET, this.target.getOntClasses(), labelOrName2PlainRDFNodes);
        for (ModelStorage other : this.otherModels) {
          constructLookUpTable(Owner.DUMMY, other.getOntClasses(), labelOrName2PlainRDFNodes);
        }
        break;
      default:
        break;
    }
  }

  private <T extends Resource> void constructLookUpTable(Owner owner,
                                                         Set<T> resources,
                                                         LookUpTable labelOrName2PlainRDFNodes) {
    for (T r : resources) {
      Set<String> labelsOrNames = getLabelsOrNames(r);
      PlainRDFNode prn = new PlainRDFNode(r.getURI(), owner);
      for (String ln : labelsOrNames) {
        labelOrName2PlainRDFNodes.put(ln, prn);
      }
    }
  }

  private <T extends Resource> Set<String> getLabelsOrNames(T resource) {
    Set<String> labelsOrNames = new HashSet<>();

    labelsOrNames.addAll(getAllLiteralString(resource, RDFS.label));

    labelsOrNames.addAll(getAllLiteralString(resource, SKOS.altLabel));

    labelsOrNames.addAll(getAllLiteralString(resource, SKOS.prefLabel));

    labelsOrNames.addAll(getAllLiteralString(resource, SKOS.hiddenLabel));

    if (labelsOrNames.isEmpty()) {
      String localName = resource.getLocalName();
      if (null != localName && !localName.equals("")) {
        labelsOrNames.add(localName);
      }
    }

    if (labelsOrNames.isEmpty()) {
      String name = DBkWik.getName(resource.getURI());
      if (null != name && !name.isEmpty()) {
        labelsOrNames.add(name);
      }
    }

    if (labelsOrNames.isEmpty()) {
      // XXX: check if labelsOrNames is still empty
    }

    if (Preprocessing.isNormalizationEnabled()) {
      Set<String> normalizedLabelsOrNames = new HashSet<>();

      for (String lb : labelsOrNames) {
        String nlb = Preprocessing.normalize(lb);
        normalizedLabelsOrNames.add(nlb);
      }

      labelsOrNames.clear();
      labelsOrNames.addAll(normalizedLabelsOrNames);
    }

    return labelsOrNames;
  }

  private <T extends Resource> Set<String> getAllLiteralString(T r, Property property) {
    Set<String> literalStrings = new HashSet<>();
    for (StmtIterator it = r.listProperties(property); it.hasNext(); ) {
      Statement stmt = it.nextStatement();
      RDFNode obj = stmt.getObject();
      if (!obj.isLiteral()) continue;
      String ls = obj.asLiteral().getString();
      if (null != ls && !ls.isEmpty()) {
        literalStrings.add(ls);
      }
    }
    return literalStrings;
  }

  private Set<String> getAllTokens(String labelOrName) {
    Set<String> tokens = new HashSet<>();
    Set<String> tokensBuffer = new HashSet<>(Arrays.asList(Preprocessing.stringTokenize(labelOrName)));

    if (Preprocessing.isStopWordsEnabled()) {
      Preprocessing.removeStopWords(tokensBuffer);
      tokens.clear();
      tokens.addAll(tokensBuffer);

      tokensBuffer.clear();
      tokensBuffer.addAll(tokens);
    }

    if (Preprocessing.isStemmerEnabled()) {
      tokens.clear();
      for (String t : tokensBuffer) {
        String st = Preprocessing.stem(t);
        if (null == st || st.isEmpty()) continue;
        tokens.add(st);
      }

      // XXX: if has another processing step then should add the following two line codes
      // tokensBuffer.clear();
      // tokensBuffer.addAll(tokens);
    }

    return tokens;
  }

  private Set<PlainRDFNode> getPlainRDFNodes(Set<String> labelsOrNames, LookUpTable labelOrName2PlainRDFNodes) {
    Set<PlainRDFNode> candidates = new HashSet<>();
    for (String ln : labelsOrNames) {
      candidates.addAll(labelOrName2PlainRDFNodes.get(ln));
    }
    return candidates;
  }
}
