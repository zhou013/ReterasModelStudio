package com.hiveworkshop.rms.ui.application.edit;

import java.awt.Dimension;
import java.text.MessageFormat;import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.application.MainPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.listener.ClonedNodeNamePicker;
import com.hiveworkshop.rms.ui.util.LanguageReader;

public final class ClonedNodeNamePickerImplementation implements ClonedNodeNamePicker {
    private final MainPanel mainPanel;
    private static final ResourceBundle resourceBundle = LanguageReader.getRb();

    public ClonedNodeNamePickerImplementation(final MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public Map<IdObject, String> pickNames(final Collection<IdObject> clonedNodes) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        final Map<JTextField, IdObject> textFieldToObject = new HashMap<>();
        for (final IdObject object : clonedNodes) {
            final JTextField textField = new JTextField(MessageFormat.format(resourceBundle.getString("0.copy"), object.getName()));
            final JLabel oldNameLabel = new JLabel(MessageFormat.format(resourceBundle.getString("enter.name.for.clone.of.0"), object.getName()));
            panel.add(oldNameLabel);
            panel.add(textField);
            textFieldToObject.put(textField, object);
        }
        final JPanel dumbPanel = new JPanel();
        dumbPanel.add(panel);
        final JScrollPane scrollPane = new JScrollPane(dumbPanel);
        scrollPane.setPreferredSize(new Dimension(450, 300));
        final int x = JOptionPane.showConfirmDialog(mainPanel, scrollPane, resourceBundle.getString("choose.node.names"),
                JOptionPane.OK_CANCEL_OPTION);
        if (x != JOptionPane.OK_OPTION) {
            return null;
        }
        final Map<IdObject, String> objectToName = new HashMap<>();
        for (final JTextField field : textFieldToObject.keySet()) {
            final IdObject idObject = textFieldToObject.get(field);
            objectToName.put(idObject, field.getText());
        }
        return objectToName;
    }
}