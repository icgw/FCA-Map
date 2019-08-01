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
  private static final Model m       = ModelFactory.createDefaultModel();

  private static final String uri    = "http://dbkwik.webdatacommons.org/";
  private static final String prefix = uri + "ontology/";

  protected static final Resource resource(String local) {
    return m.createResource( prefix + local );
  }

  protected static final Property property(String local) {
    return m.createProperty( prefix + local );
  }

  public static boolean own(Resource r) {
    return r.getURI().startsWith(uri);
  }

  public static boolean ownAsResource(Resource r) {
    String that_uri = r.getURI();
    return that_uri.startsWith(uri) && that_uri.contains("/resource/");
  }

  // NOTE: Define DBkWik Classes
  public static final Resource Image                 = Init.Image();

  // NOTE: Define DBkWik Properties
  public static final Property wikiPageDisambiguates = Init.wikiPageDisambiguates();
  public static final Property _abstract             = Init._abstract();
  public static final Property thumbnail             = Init.thumbnail();
  public static final Property wikiPageExternalLink  = Init.wikiPageExternalLink();
  public static final Property wikiPageID            = Init.wikiPageID();
  public static final Property wikiPageLength        = Init.wikiPageLength();
  public static final Property wikiPageOutDegree     = Init.wikiPageOutDegree();
  public static final Property wikiPageRedirects     = Init.wikiPageRedirects();
  public static final Property wikiPageUsesTemplate  = Init.wikiPageUsesTemplate();
  public static final Property wikiPageWikiLink      = Init.wikiPageWikiLink();
  public static final Property wikiPageWikiLinkText  = Init.wikiPageWikiLinkText();

  private static class Init {
    private static Resource Image()                 { return resource( "Image" ); }

    private static Property wikiPageDisambiguates() { return property( "wikiPageDisambiguates" ); }
    private static Property _abstract()             { return property( "abstract" ); }
    private static Property thumbnail()             { return property( "thumbnail"); }
    private static Property wikiPageExternalLink()  { return property( "wikiPageExternalLink" ); }
    private static Property wikiPageID()            { return property( "wikiPageID" ); }
    private static Property wikiPageLength()        { return property( "wikiPageLength" ); }
    private static Property wikiPageOutDegree()     { return property( "wikiPageOutDegree" ); }
    private static Property wikiPageRedirects()     { return property( "wikiPageRedirects" ); }
    private static Property wikiPageUsesTemplate()  { return property( "wikiPageUsesTemplate" ); }
    private static Property wikiPageWikiLink()      { return property( "wikiPageWikiLink" ); }
    private static Property wikiPageWikiLinkText()  { return property( "wikiPageWikiLinkText" ); }
  }

  public static String getURI() {
    return uri;
  }
}
