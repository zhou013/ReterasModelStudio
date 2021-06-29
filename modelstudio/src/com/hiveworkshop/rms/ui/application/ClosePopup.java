package com.hiveworkshop.rms.ui.application;

import com.hiveworkshop.rms.ui.util.LanguageReader;

import javax.swing.*;
import java.util.ResourceBundle;

public class ClosePopup {
	static JPopupMenu contextMenu;
	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	static void createContextMenuPopup(MainPanel mainPanel) {
		contextMenu = new JPopupMenu();
		JMenuItem contextClose = new JMenuItem(resourceBundle.getString("close"));
		contextClose.addActionListener(mainPanel);
		contextMenu.add(contextClose);

		JMenuItem contextCloseOthers = new JMenuItem(resourceBundle.getString("close.others"));
		contextCloseOthers.addActionListener(e -> MenuBarActions.closeOthers(mainPanel, mainPanel.currentModelPanel));
		contextMenu.add(contextCloseOthers);

		JMenuItem contextCloseAll = new JMenuItem(resourceBundle.getString("close.all"));
		contextCloseAll.addActionListener(e -> MenuBar.closeAll(mainPanel));
		contextMenu.add(contextCloseAll);
	}
}
