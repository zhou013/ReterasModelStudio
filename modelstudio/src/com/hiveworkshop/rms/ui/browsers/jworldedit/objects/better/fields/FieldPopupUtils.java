package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields;

import com.hiveworkshop.rms.ui.util.LanguageReader;

import java.awt.Component;
import java.text.MessageFormat;import java.text.ParseException;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public enum FieldPopupUtils {
	;
	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	public static int showPopup(final Component parentComponent, final JPanel message, final String title,
			final int optionType, final int messageType, final JComponent componentToFocus) {
//		componentToFocus.addAncestorListener(new AncestorListener() {
//			@Override
//			public void ancestorRemoved(final AncestorEvent event) {
//			}
//
//			@Override
//			public void ancestorMoved(final AncestorEvent event) {
//			}
//
//			@Override
//			public void ancestorAdded(final AncestorEvent event) {
//				SwingUtilities.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						SwingUtilities.invokeLater(new Runnable() {
//							@Override
//							public void run() {
//								if (componentToFocus instanceof JSpinner) {
//									final JFormattedTextField textField = ((JSpinner.DefaultEditor) ((JSpinner) componentToFocus)
//											.getEditor()).getTextField();
//									// textField.requestFocus();
//									// textField.setText(textField.getText());
//									textField.selectAll();
//								} else {
//									componentToFocus.requestFocus();
//									if (componentToFocus instanceof JTextComponent) {
//										((JTextComponent) componentToFocus).selectAll();
//									}
//								}
//							}
//						});
//					}
//				});
//			}
//		});
		return showConfirmDialog(parentComponent, message, title, optionType, messageType, componentToFocus);
	}

	/**
	 * This method shows a confirmation dialog with the given message, title,
	 * messageType and optionType. The frame owner will be the same frame as the one
	 * that holds the given parentComponent. This method returns the option selected
	 * by the user.
	 *
	 * @param parentComponent The component to find a frame in.
	 * @param message         The message displayed.
	 * @param title           The title of the dialog.
	 * @param optionType      The optionType.
	 * @param messageType     The messageType.
	 *
	 * @return The selected option.
	 */
	public static int showConfirmDialog(final Component parentComponent, final Object message, final String title,
			final int optionType, final int messageType, final JComponent componentWantingFocus) {
		final PopupContext popupContext = new PopupContext();
		if (componentWantingFocus instanceof JSpinner) {
			final JSpinner spinner = (JSpinner) componentWantingFocus;
			final JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
			final JFormattedTextField textField = editor.getTextField();
//			((NumberFormatter) textField.getFormatter()).setAllowsInvalid(false);
			spinner.addChangeListener(e -> {
                try {
                    spinner.commitEdit();
                } catch (final ParseException e1) {
                    JOptionPane.showMessageDialog(parentComponent,
                            MessageFormat.format(resourceBundle.getString("unable.to.commit.edit.because.0.1"), e1.getClass(),e1.getMessage()));
                    e1.printStackTrace();
                }
            });
			textField.addActionListener(e -> {
                if (popupContext.dialog != null) {
                    popupContext.optionPane.setValue(JOptionPane.OK_OPTION);
                    popupContext.dialog.dispose();
                }
            });
		}
		final JOptionPane pane = new JOptionPane(message, messageType, optionType) {
			@Override
			public void selectInitialValue() {
				SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> {
//								componentWantingFocus.requestFocus();
                    if (componentWantingFocus instanceof JSpinner) {
                        final JFormattedTextField textField = ((JSpinner.DefaultEditor) ((JSpinner) componentWantingFocus)
                                .getEditor()).getTextField();
//									textField.requestFocus();
                        // textField.setText(textField.getText());
                        textField.selectAll();
                    } else {
//									componentWantingFocus.requestFocus();
                        if (componentWantingFocus instanceof JTextComponent) {
                            ((JTextComponent) componentWantingFocus).selectAll();
                        }
                    }
                }));
			}
		};
		final JDialog dialog = pane.createDialog(parentComponent, title);
		popupContext.dialog = dialog;
		popupContext.optionPane = pane;
		dialog.show();

		if (pane.getValue() instanceof Integer) {
			return (Integer) pane.getValue();
		}
		return -1;
	}

	private static final class PopupContext {
		private JOptionPane optionPane;
		private JDialog dialog;
	}
}
