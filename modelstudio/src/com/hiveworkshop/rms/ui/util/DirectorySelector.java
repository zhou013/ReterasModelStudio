package com.hiveworkshop.rms.ui.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;


public class DirectorySelector extends JPanel implements ActionListener {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	final static long serialVersionUID = 5L;
	JLabel text = new JLabel(resourceBundle.getString("select.valid.wariii.directory"));
	JTextField pathField;
	JButton browseButton = new JButton(resourceBundle.getString("browse"));
	public DirectorySelector(final String defaultDir, final String specialText)
	{
		text.setText(text.getText());
		pathField = new JTextField(defaultDir);
		browseButton.addActionListener(this);


		final GroupLayout layout = new GroupLayout(this);
		final GroupLayout.ParallelGroup horizGroup = layout.createParallelGroup();
		final GroupLayout.SequentialGroup vertGroup = layout.createSequentialGroup();

		for( int i = 0; i <= specialText.length()/70; i++)
		{
			final JLabel text2 = new JLabel(specialText.substring(i*70,Math.min(specialText.length(),(i+1)*70)));
			if( specialText == null || specialText.equals("") )
			{
				text2.setVisible(false);
			}
			horizGroup.addComponent(text2);
			vertGroup.addComponent(text2);
		}

		if( !(specialText == null || specialText.equals("")) )
		{
			vertGroup.addGap(12);
		}

		layout.setHorizontalGroup(horizGroup
				.addComponent(text)
				.addGroup(layout.createSequentialGroup()
						.addComponent(pathField)
						.addComponent(browseButton)
						)
					);
		layout.setVerticalGroup(vertGroup
				.addComponent(text)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(pathField)
						.addComponent(browseButton)
						)
					);
		setLayout(layout);
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		if( e.getSource() == browseButton )
		{
			final JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setCurrentDirectory(new File(pathField.getText()));
			final int x = jfc.showOpenDialog(this);
			if( x == JFileChooser.APPROVE_OPTION )
			{
				String wcDirectory = jfc.getSelectedFile().getAbsolutePath();
				if( !(wcDirectory.endsWith("/") || wcDirectory.endsWith("\\")) )
				{
					wcDirectory = wcDirectory + "\\";
				}
				if( !System.getProperty("os.name").contains("win") ) {
					wcDirectory = wcDirectory.replace('\\', '/');
				}
				final File temp = new File(wcDirectory+"mod_test_file.txt");
				boolean good = false;
				try {
					good = temp.createNewFile();
					temp.delete();
				} catch (final IOException exc) {
					exc.printStackTrace();
				}
				if( !good )
				{
					JOptionPane.showMessageDialog(null, resourceBundle.getString("no.permissions.to.access.folder"),resourceBundle.getString("texture.loader.wont.work"), JOptionPane.WARNING_MESSAGE);
				}

				pathField.setText(jfc.getSelectedFile().getAbsolutePath());
			}
		}
	}

	public String getDir()
	{
		return pathField.getText();
	}
}
