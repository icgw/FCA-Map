/*
 * DBkWik.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.vocabulary;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Property;

public class DBkWik
{
  private static final Model m = ModelFactory.createDefaultModel();

  private static final String uri = "http://dbkwik.webdatacommons.org/";
  private static final String ontology = uri + "ontology/";

  public static String getURI() {
    return uri;
  }

  public static boolean own(Resource r) {
    return r.getURI().startsWith(uri);
  }

  // NOTE: Define DBkWik Classes
  public static final Resource _Image = m.createResource( uri + "Image" );

  // NOTE: Define DBkWik Properties
  public static final Property _wikiPageDisambiguates = m.createProperty( ontology + "wikiPageDisambiguates" );
  public static final Property _abstract              = m.createProperty( ontology + "abstract" );
  public static final Property _thumbnail             = m.createProperty( ontology + "thumbnail" );
  public static final Property _wikiPageExternalLink  = m.createProperty( ontology + "wikiPageExternalLink" );
  public static final Property _wikiPageID            = m.createProperty( ontology + "wikiPageID" );
  public static final Property _wikiPageLength        = m.createProperty( ontology + "wikiPageLength" );
  public static final Property _wikiPageOutDegree     = m.createProperty( ontology + "wikiPageOutDegree" );
  public static final Property _wikiPageRedirects     = m.createProperty( ontology + "wikiPageRedirects" );
  public static final Property _wikiPageUsesTemplate  = m.createProperty( ontology + "wikiPageUsesTemplate" );
  public static final Property _wikiPageWikiLink      = m.createProperty( ontology + "wikiPageWikiLink" );
  public static final Property _wikiPageWikiLinkText  = m.createProperty( ontology + "wikiPageWikiLinkText" );
}
