package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.ui.util.LanguageReader;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.text.MessageFormat;import java.util.ResourceBundle;

class ObjectPanel extends JPanel {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	JLabel title;

	JCheckBox doImport;
	JLabel parentLabel;
	JLabel oldParentLabel;
	IterableListModel<BoneShell> parents;
	JList<BoneShell> parentsList;
	JScrollPane parentsPane;
	ModelHolderThing mht;

	ObjectShell selectedObject;

	protected ObjectPanel() {

	}

	public ObjectPanel(ModelHolderThing mht, BoneShellListCellRenderer bonePanelRenderer) {
		this.mht = mht;
		setLayout(new MigLayout("gap 0, ins 0", "[grow]", "[][][][][grow]"));

		title = new JLabel(resourceBundle.getString("object.title"));
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		doImport = new JCheckBox(resourceBundle.getString("import.this.object"));
		doImport.setSelected(true);
		doImport.addActionListener(e -> setImportStatus());
		add(doImport, "left, wrap");


		oldParentLabel = new JLabel(resourceBundle.getString("old.parent.no.parent"));
		add(oldParentLabel, "left, wrap");


		parentLabel = new JLabel(resourceBundle.getString("parent"));
		add(parentLabel, "left, wrap");

		parentsList = new JList<>();
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentsList.setCellRenderer(bonePanelRenderer);
		parentsList.addListSelectionListener(this::setParent);

		parentsPane = new JScrollPane(parentsList);
		add(parentsPane, "growx, growy 200");
	}

	public ObjectPanel setSelectedObject(ObjectShell selectedObject) {
		this.selectedObject = selectedObject;
		parentsList.setEnabled(selectedObject.getCamera() == null);
		setTitles();
		parents = mht.getFutureBoneListExtended(true);
		setParentListModel();
		setCheckboxStatus(selectedObject.getShouldImport());
		return this;
	}

	private void setParentListModel() {
		parentsList.setModel(parents);
	}

	private void setParent(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && parentsList.getSelectedValue() != null) {
			if (parentsList.getSelectedValue() == selectedObject.getNewParentBs()) {
				selectedObject.setNewParentBs(null);
			} else {
				selectedObject.setNewParentBs(parentsList.getSelectedValue());
			}
		}
	}


	private void setTitles() {
		title.setText(selectedObject.toString());
//		title.setText(object.getClass().getSimpleName() + " \"" + object.getName() + "\"");

		if (selectedObject.getOldParentBs() != null) {
			oldParentLabel.setText(MessageFormat.format(resourceBundle.getString("old.parent.0"), selectedObject.getOldParentBs().getName()));
		} else {
			oldParentLabel.setText(resourceBundle.getString("old.parent.no.parent"));
		}
	}

	private void setCheckboxStatus(boolean isChecked) {
		doImport.setSelected(isChecked);
	}

	private void setImportStatus() {
		selectedObject.setShouldImport(doImport.isSelected());
	}

}
