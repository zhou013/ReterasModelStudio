package com.hiveworkshop.rms.ui.application.model.material;

import com.hiveworkshop.rms.editor.model.Layer;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ResourceBundle;

public class LayerFlagsPanel extends JPanel {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	private final JCheckBox unshaded;
	private final JCheckBox sphereEnvMap;
	private final JCheckBox twoSided;
	private final JCheckBox unfogged;
	private final JCheckBox noDepthTest;
	private final JCheckBox noDepthSet;
	private final JCheckBox unlit;

	public LayerFlagsPanel() {
		setLayout(new MigLayout());
		unshaded = new JCheckBox(resourceBundle.getString("unshaded"));
		add(unshaded, "wrap");

		sphereEnvMap = new JCheckBox(resourceBundle.getString("sphereenvmap"));
		add(sphereEnvMap, "wrap");

		twoSided = new JCheckBox(resourceBundle.getString("twosided"));
		add(twoSided, "wrap");

		unfogged = new JCheckBox(resourceBundle.getString("unfogged"));
		add(unfogged, "wrap");

		noDepthTest = new JCheckBox(resourceBundle.getString("nodepthtest"));
		add(noDepthTest, "wrap");

		noDepthSet = new JCheckBox(resourceBundle.getString("nodepthset"));
		add(noDepthSet, "wrap");

		unlit = new JCheckBox(resourceBundle.getString("unlit"));
		add(unlit, "wrap");
	}

	public void setLayer(final Layer layer) {
		unshaded.setSelected(layer.getUnshaded());
		sphereEnvMap.setSelected(layer.getSphereEnvMap());
		twoSided.setSelected(layer.getTwoSided());
		unfogged.setSelected(layer.getUnfogged());
		noDepthTest.setSelected(layer.getNoDepthTest());
		noDepthSet.setSelected(layer.getNoDepthSet());
		unlit.setSelected(layer.getUnlit());
	}
}
