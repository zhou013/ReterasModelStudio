package com.hiveworkshop.rms.ui.browsers.model;

import com.hiveworkshop.rms.ui.util.LanguageReader;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class ModelOptionPane {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	public static String show(final Component what) {
		final ModelOptionPanel uop = new ModelOptionPanel();
		final int x = JOptionPane.showConfirmDialog(what, uop, resourceBundle.getString("choose.model"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (x == JOptionPane.OK_OPTION) {
			return uop.getSelection();
		}
		return null;
	}

	public static ModelElement showAndLogIcon(final Component what) {
		final ModelOptionPanel uop = new ModelOptionPanel();
		final int x = JOptionPane.showConfirmDialog(what, uop, resourceBundle.getString("choose.model"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (x == JOptionPane.OK_OPTION) {
			return new ModelElement(uop.getSelection(), uop.getCachedIconPath());
		}
		return null;
	}

	public static String show(final Component what, final String startingFile) {
		final ModelOptionPanel uop = new ModelOptionPanel();
		uop.setSelection(startingFile);
		final int x = JOptionPane.showConfirmDialog(what, uop, resourceBundle.getString("choose.model"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (x == JOptionPane.OK_OPTION) {
			return uop.getSelection();
		}
		return null;
	}

	public static final class ModelElement {
		private final String filepath;
		private final String cachedIconPath;

		public ModelElement(final String filepath, final String cachedIconPath) {
			this.filepath = filepath;
			this.cachedIconPath = cachedIconPath;
		}

		public String getFilepath() {
			return filepath;
		}

		public String getCachedIconPath() {
			return cachedIconPath;
		}

		public boolean hasCachedIconPath() {
			return cachedIconPath != null && cachedIconPath.length() > 0;
		}
	}
}
