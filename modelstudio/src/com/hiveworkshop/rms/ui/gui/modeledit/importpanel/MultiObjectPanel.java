package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.ui.util.LanguageReader;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ResourceBundle;

class MultiObjectPanel extends ObjectPanel {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	ModelHolderThing mht;

	public MultiObjectPanel(ModelHolderThing mht, final IterableListModel<BoneShell> possibleParents) {
		this.mht = mht;
		setLayout(new MigLayout("gap 0", "[grow]", "[][][][][grow]"));
		title = new JLabel(resourceBundle.getString("multiple.selected"));
		title.setFont(new Font("Arial", Font.BOLD, 26));
		add(title, "align center, wrap");

		doImport = new JCheckBox(resourceBundle.getString("import.these.objects.click.to.apply.to.all"));
		doImport.setSelected(true);
		doImport.addActionListener(e -> doImportPressed());
		add(doImport, "left, wrap");

		oldParentLabel = new JLabel(resourceBundle.getString("old.parent.can.only.be.displayed.for.a.single.object"));
		add(oldParentLabel, "left, wrap");

		parentLabel = new JLabel(resourceBundle.getString("parent"));
		add(parentLabel, "left, wrap");

		parents = possibleParents;
		parentsList = new JList<>(parents);
		parentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		parentsPane = new JScrollPane(parentsList);
		parentsPane.setEnabled(false);
		add(parentsPane, "growx, growy 200");
	}

	public void updateMultiObjectPanel(){
		List<ObjectShell> selectedValuesList = mht.donModObjectJList.getSelectedValuesList();

		boolean firstShouldImport = selectedValuesList.get(0).getShouldImport();

		if (selectedValuesList.stream().anyMatch(objectShell -> objectShell.getShouldImport() != firstShouldImport)){
			doImport.setSelected(false);
			doImport.setBackground(Color.ORANGE);
		} else {
			doImport.setSelected(firstShouldImport);
			doImport.setBackground(this.getBackground());
		}
	}

	private void doImportPressed() {
		for (ObjectShell op : mht.donModObjectJList.getSelectedValuesList()) {
			op.setShouldImport(doImport.isSelected());
		}
	}
}
