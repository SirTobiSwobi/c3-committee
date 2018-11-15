package org.SirTobiSwobi.c3.c3committee.core;

import org.SirTobiSwobi.c3.c3committee.db.Model;

public interface FeatureExtractor {
	public double[] getVector(long docId);
	public double[] getVector(String text);
	public Model getModel();
	public void setModel(Model model);

}
