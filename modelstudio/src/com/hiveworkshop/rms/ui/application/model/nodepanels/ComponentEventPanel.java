package com.hiveworkshop.rms.ui.application.model.nodepanels;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.EventObject;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.application.actions.model.NameChangeAction;
import com.hiveworkshop.rms.ui.application.actions.model.ParentChangeAction;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.application.model.ComponentPanel;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ResourceBundle;

public class ComponentEventPanel extends JPanel implements ComponentPanel<EventObject> {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	private final ModelViewManager modelViewManager;
	private final UndoActionListener undoActionListener;
	private final ModelStructureChangeListener modelStructureChangeListener;
	JLabel title;
	JTextField nameField;
	JLabel parentName;
	ParentChooser parentChooser;
	private EventObject idObject;


	public ComponentEventPanel(final ModelViewManager modelViewManager,
	                           final UndoActionListener undoActionListener,
	                           final ModelStructureChangeListener modelStructureChangeListener) {
		this.undoActionListener = undoActionListener;
		this.modelViewManager = modelViewManager;
		this.modelStructureChangeListener = modelStructureChangeListener;

		parentChooser = new ParentChooser(modelViewManager);

		setLayout(new MigLayout("fill, gap 0", "[][][grow]", "[][][grow]"));
		title = new JLabel(resourceBundle.getString("select.a.eventobject"));
		add(title, "wrap");
		nameField = new JTextField(24);
		nameField.addFocusListener(changeName());
		add(nameField, "wrap");
		add(new JLabel(resourceBundle.getString("parent1")));
		parentName = new JLabel(resourceBundle.getString("parent2"));
		add(parentName);
//		JButton chooseParentButton = new JButton("change");
//		chooseParentButton.addActionListener(e -> chooseParent());
//		add(chooseParentButton, "wrap");;
	}

	@Override
	public void setSelectedItem(EventObject itemToSelect) {
		idObject = itemToSelect;
		title.setText(idObject.getName());
		nameField.setText(idObject.getName());
		IdObject parent = idObject.getParent();
		if (parent != null) {
			this.parentName.setText(parent.getName());
		} else {
			parentName.setText(resourceBundle.getString("no.parent"));
		}
		revalidate();
		repaint();

	}

	@Override
	public void save(EditableModel model, UndoActionListener undoListener, ModelStructureChangeListener changeListener) {

	}

	private void chooseParent() {
		IdObject newParent = parentChooser.chooseParent(idObject, this.getRootPane());
		ParentChangeAction action = new ParentChangeAction(idObject, newParent, modelStructureChangeListener);
		action.redo();
		repaint();
		undoActionListener.pushAction(action);
	}


	private FocusAdapter changeName() {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String newName = nameField.getText();
				if (!newName.equals("")) {
					NameChangeAction action = new NameChangeAction(idObject, newName, modelStructureChangeListener);
					action.redo();
					undoActionListener.pushAction(action);
				}
			}
		};
	}
}
