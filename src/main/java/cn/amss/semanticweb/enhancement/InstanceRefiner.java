/*
 * InstanceRefiner.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.amss.semanticweb.enhancement;

import cn.amss.semanticweb.alignment.Mapping;

public interface InstanceRefiner extends RefinerSetting
{
  public void addPropertyAnchors(Mapping property_anchors);

  public void addClassAnchors(Mapping class_anchors);

  public void addInstanceAnchors(Mapping instance_anchors);

  public void addAllAnchors(Mapping all_anchors);

  public void validateInstanceAnchors(Mapping instance_anchors, Mapping enhanced_instance_mappings);
}
