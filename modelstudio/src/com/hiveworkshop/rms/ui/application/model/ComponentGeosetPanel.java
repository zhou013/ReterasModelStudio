package com.hiveworkshop.rms.ui.application.model;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ComponentGeosetPanel extends JPanel implements ComponentPanel<Geoset> {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	private final ModelViewManager modelViewManager;
	private final UndoActionListener undoActionListener;
	private final ModelStructureChangeListener modelStructureChangeListener;
	private ComponentGeosetMaterialPanel materialPanel;
	private final Map<Geoset, ComponentGeosetMaterialPanel> materialPanels;
	private final JLabel trisLabel;
	private final JLabel vertLabel;
	JPanel hdPanel;
	JSpinner lodSpinner;
	JTextField nameTextField;
	JButton toggleSdHd;
	//	private final JLabel selectionGroupLabel;
	private JSpinner selectionGroupSpinner;
	private Geoset geoset;

	private final boolean listenersEnabled = true;
	private final JPanel materialPanelHolder;


	public ComponentGeosetPanel(final ModelViewManager modelViewManager,
	                            final UndoActionListener undoActionListener,
	                            final ModelStructureChangeListener modelStructureChangeListener) {
		this.undoActionListener = undoActionListener;
		this.modelViewManager = modelViewManager;
		this.modelStructureChangeListener = modelStructureChangeListener;
		setLayout(new MigLayout("fill", "[][grow][grow]", "[][][grow]"));

		materialPanels = new HashMap<>();

		materialPanelHolder = new JPanel(new MigLayout("hidemode 1"));
		add(materialPanelHolder, "wrap, growx, spanx");

		materialPanelHolder.add(new JLabel(resourceBundle.getString("material1")), "wrap");
		materialPanel = new ComponentGeosetMaterialPanel();
		materialPanelHolder.add(materialPanel);

		JPanel geosetInfoPanel = new JPanel(new MigLayout("fill, hidemode 1", "[][][grow][grow]"));
		add(geosetInfoPanel, "wrap, growx, spanx");

		createHDPanel(modelViewManager);
		geosetInfoPanel.add(hdPanel, "growx, spanx, wrap");

		geosetInfoPanel.add(new JLabel(resourceBundle.getString("triangles")));
		trisLabel = new JLabel("0");
		geosetInfoPanel.add(trisLabel, "wrap");

		geosetInfoPanel.add(new JLabel(resourceBundle.getString("vertices")));
		vertLabel = new JLabel("0");
		geosetInfoPanel.add(vertLabel, "wrap");

		geosetInfoPanel.add(new JLabel(resourceBundle.getString("selectiongroup")));
		selectionGroupSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
		selectionGroupSpinner.addChangeListener(e -> setSelectionGroup());
		geosetInfoPanel.add(selectionGroupSpinner, "wrap");
//		selectionGroupLabel = new JLabel("0");

		JButton editUvButton = new JButton(resourceBundle.getString("edit.geoset.uvs"));
		toggleSdHd = new JButton(resourceBundle.getString("make.geoset.hd"));
		toggleSdHd.addActionListener(e -> toggleSdHd());
		add(toggleSdHd, "wrap");
	}

	private void createHDPanel(ModelViewManager modelViewManager) {
		hdPanel = new JPanel(new MigLayout("fill, ins 0", "[]16[][grow][grow]"));

		hdPanel.add(new JLabel(resourceBundle.getString("name1")));
		nameTextField = new JTextField(26);
		nameTextField.addFocusListener(setLoDName());
		hdPanel.add(nameTextField, "spanx 2, wrap");

		hdPanel.add(new JLabel(resourceBundle.getString("levelofdetail")));
		lodSpinner = new JSpinner(new SpinnerNumberModel(0, -1, 10000, 1));
		hdPanel.add(lodSpinner, "wrap");
		lodSpinner.addChangeListener(e -> setLoD());

		hdPanel.setVisible(modelViewManager.getModel().getFormatVersion() == 1000);
	}


	@Override
	public void setSelectedItem(final Geoset geoset) {
		this.geoset = geoset;
		materialPanelHolder.remove(materialPanel);
		setToggleButtonText();

		materialPanels.putIfAbsent(geoset, new ComponentGeosetMaterialPanel());
		materialPanel = materialPanels.get(geoset);

		materialPanel.setMaterialChooser(geoset, modelViewManager, undoActionListener, modelStructureChangeListener);
		materialPanelHolder.add(materialPanel);
		materialPanelHolder.revalidate();
		materialPanelHolder.repaint();

		trisLabel.setText("" + geoset.getTriangles().size());
		vertLabel.setText("" + geoset.getVertices().size());

		selectionGroupSpinner.setValue(geoset.getSelectionGroup());
		lodSpinner.setValue(geoset.getLevelOfDetail());
		nameTextField.setText(geoset.getLevelOfDetailName());

		hdPanel.setVisible(modelViewManager.getModel().getFormatVersion() == 1000);

		revalidate();
		repaint();
	}


	@Override
	public void save(final EditableModel model, final UndoActionListener undoListener,
	                 final ModelStructureChangeListener changeListener) {
	}


	private void setSelectionGroup() {
		geoset.setSelectionGroup((Integer) selectionGroupSpinner.getValue());
	}

	private void setLoD() {
		geoset.setLevelOfDetail((Integer) lodSpinner.getValue());
	}

	private void toggleSdHd() {
		if (geoset != null) {
			if (geoset.isHD()) {
				geoset.makeSd();
			} else {
				geoset.makeHd();
			}
			setToggleButtonText();
		}
	}

	private void setToggleButtonText() {
		toggleSdHd.setVisible(modelViewManager.getModel().getFormatVersion() >= 900);
		if (geoset.isHD()) {
			toggleSdHd.setText(resourceBundle.getString("make.geoset.sd"));
		} else {
			toggleSdHd.setText(resourceBundle.getString("make.geoset.hd"));
		}
	}

	private FocusAdapter setLoDName() {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				geoset.setLevelOfDetailName(nameTextField.getText());
			}
		};
	}

	private void editUVs() {

	}

}
