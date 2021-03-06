package com.hiveworkshop.rms.ui.application.edit;

import java.awt.event.ActionEvent;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.hiveworkshop.rms.ui.application.MainPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.ui.util.LanguageReader;

public final class RedoActionImplementation extends AbstractAction {
    private final MainPanel mainPanel;
    private static final ResourceBundle resourceBundle = LanguageReader.getRb();

    public RedoActionImplementation(final String name, final MainPanel mainPanel) {
        super(name);
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final ModelPanel mpanel = mainPanel.currentModelPanel();
        if (mpanel != null) {
            try {
                mpanel.getUndoManager().redo();
            } catch (final NoSuchElementException exc) {
                JOptionPane.showMessageDialog(mainPanel, resourceBundle.getString("nothing.to.redo"));
            } catch (final Exception exc) {
                ExceptionPopup.display(exc);
            }
        }
        mainPanel.refreshUndo();
        MainPanel.repaintSelfAndChildren(mainPanel);
        mpanel.repaintSelfAndRelatedChildren();
    }
}