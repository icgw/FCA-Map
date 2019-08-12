/*
 * OAEIAlignmentOutput.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.io;

import cn.amss.semanticweb.util.Element;
import cn.amss.semanticweb.alignment.Mapping;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class OAEIAlignmentOutput extends Element
{
  private Mapping m_alignment  = null;
  private String m_output_path = "";

  private OAEIAlignmentOutput() {
  }

  public OAEIAlignmentOutput(Mapping alignment, String path) {
    m_alignment   = alignment;
    m_output_path = path;
  }

  private final String printXmlHead(String version, String encoding) {
    return String.format("<?xml version=\"%s\" encoding=\"%s\"?>%n", version, encoding);
  }

  private final String printRDFHeader() {
    return String.format("<rdf:RDF xmlns=\"%s\"%n  xmlns:rdf=\"%s\"%n  xmlns:xsd=\"%s\">%n",
        "http://knowledgeweb.semanticweb.org/heterogeneity/alignment",
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        "http://www.w3.org/2001/XMLSchema#");
  }

  private final String printRDFTail() {
    return endElement("rdf:RDF");
  }

  private final String getAlignmentContent(int indent) {
    StringBuilder content = new StringBuilder();

    content.append(getElementWithIndentln(startElement("Alignment"), indent));

    content.append(getElementWithIndentln(getElementFormat("xml", "", "yes"), indent + 1));
    content.append(getElementWithIndentln(getElementFormat("level", "", "0"), indent + 1));
    content.append(getElementWithIndentln(getElementFormat("type", "", "??"), indent + 1));

    content.append(m_alignment.getContent(indent + 1));
    content.append(getElementWithIndentln(endElement("Alignment"), indent));

    return content.toString();
  }

  public final String getContent() {
    StringBuilder content = new StringBuilder();
    content.append(printXmlHead("1.0", "utf-8"));
    content.append(printRDFHeader());
    content.append(getAlignmentContent(0));
    content.append(printRDFTail());
    return content.toString();
  }

  public final void write() throws Exception {
    Files.write(Paths.get(m_output_path), getContent().getBytes());
  }
}
