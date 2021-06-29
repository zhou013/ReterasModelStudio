package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.filesystem.sources.DataSourceDescriptor;
import com.hiveworkshop.rms.ui.preferences.DataSourceChooserPanel;
import com.hiveworkshop.rms.ui.preferences.GUITheme;
import com.hiveworkshop.rms.ui.preferences.MouseButtonPreference;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ColorChooserIcon;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import com.hiveworkshop.rms.util.SmartButtonGroup;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

public final class ProgramPreferencesPanel extends JTabbedPane {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	private final ProgramPreferences programPreferences;
	private final DataSourceChooserPanel dataSourceChooserPanel;

	public ProgramPreferencesPanel(final ProgramPreferences programPreferences,
	                               final List<DataSourceDescriptor> dataSources) {
		this.programPreferences = programPreferences;

		createAndAddGeneralPrefsPanel(programPreferences);

		createAndAddModelEditorPanel(programPreferences);

		createAndAddHotkeysPanel(programPreferences);

		dataSourceChooserPanel = new DataSourceChooserPanel(dataSources);
		addTab(resourceBundle.getString("warcraft.data"), dataSourceChooserPanel);
	}

	private void createAndAddGeneralPrefsPanel(ProgramPreferences pref) {
		final JPanel generalPrefsPanel = new JPanel(new MigLayout());
		generalPrefsPanel.add(new JLabel(resourceBundle.getString("3d.view.mode")), "wrap");

		SmartButtonGroup viewModeGroup = new SmartButtonGroup();
		viewModeGroup.addJRadioButton(resourceBundle.getString("wireframe"), e -> pref.setViewMode(0));
		viewModeGroup.addJRadioButton(resourceBundle.getString("solid"), e -> pref.setViewMode(1));
		viewModeGroup.setSelectedIndex(pref.viewMode());
		generalPrefsPanel.add(viewModeGroup.getButtonPanel(), "wrap");

		final JCheckBox grid2d = new JCheckBox();
		grid2d.addActionListener(e -> pref.setShow2dGrid(grid2d.isSelected()));
		grid2d.setSelected(pref.show2dGrid());
		generalPrefsPanel.add(new JLabel(resourceBundle.getString("show.2d.viewport.gridlines")));
		generalPrefsPanel.add(grid2d, "wrap");

		final JCheckBox useBoxesForNodes = new JCheckBox();
		useBoxesForNodes.addActionListener(e -> pref.setUseBoxesForPivotPoints(useBoxesForNodes.isSelected()));
		useBoxesForNodes.setSelected(pref.getUseBoxesForPivotPoints());
		generalPrefsPanel.add(new JLabel(resourceBundle.getString("use.boxes.for.nodes")));
		generalPrefsPanel.add(useBoxesForNodes, "wrap");

		final JCheckBox quickBrowse = new JCheckBox();
		quickBrowse.addActionListener(e -> pref.setQuickBrowse(quickBrowse.isSelected()));
		quickBrowse.setSelected(pref.getQuickBrowse());
		generalPrefsPanel.add(new JLabel(resourceBundle.getString("quick.browse")));
		quickBrowse.setToolTipText(resourceBundle.getString("quick.browse.description"));
		generalPrefsPanel.add(quickBrowse, "wrap");

		final JCheckBox allowLoadingNonBlpTextures = new JCheckBox();
		allowLoadingNonBlpTextures.addActionListener(e -> pref.setAllowLoadingNonBlpTextures(allowLoadingNonBlpTextures.isSelected()));
		allowLoadingNonBlpTextures.setSelected(pref.getAllowLoadingNonBlpTextures());
		generalPrefsPanel.add(new JLabel(resourceBundle.getString("allow.loading.non.blp.textures")));
		allowLoadingNonBlpTextures.setToolTipText(resourceBundle.getString("loading.non.blp.textures.description"));
		generalPrefsPanel.add(allowLoadingNonBlpTextures, "wrap");

//		generalPrefsPanel.add(new JLabel("Render Particle Emitters:"), "cell 0 7");
		// final BoxLayout boxLayout = new BoxLayout(generalPrefsPanel,
		// BoxLayout.PAGE_AXIS);

		addTab("General", generalPrefsPanel);
	}


	private void createAndAddModelEditorPanel(ProgramPreferences pref) {
		final JPanel modelEditorPanel = new JPanel();
		modelEditorPanel.setLayout(new MigLayout("gap 0"));

		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getBackgroundColor(), pref::setBackgroundColor), resourceBundle.getString("background.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getPerspectiveBackgroundColor(), pref::setPerspectiveBackgroundColor), resourceBundle.getString("perspective.background.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getVertexColor(), pref::setVertexColor), resourceBundle.getString("vertex.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getHighlighVertexColor(), pref::setHighlighVertexColor), resourceBundle.getString("vertex.highlight.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getTriangleColor(), pref::setTriangleColor), resourceBundle.getString("triangle.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getHighlighTriangleColor(), pref::setHighlighTriangleColor), resourceBundle.getString("triangle.highlight.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getSelectColor(), pref::setSelectColor), resourceBundle.getString("select.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getVisibleUneditableColor(), pref::setVisibleUneditableColor), resourceBundle.getString("visible.uneditable.mesh.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getAnimatedBoneUnselectedColor(), pref::setAnimatedBoneUnselectedColor), resourceBundle.getString("animation.editor.bone.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getAnimatedBoneSelectedColor(), pref::setAnimatedBoneSelectedColor), resourceBundle.getString("animation.editor.selected.bone.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getAnimatedBoneSelectedUpstreamColor(), pref::setAnimatedBoneSelectedUpstreamColor), resourceBundle.getString("animration.editor.selected.upstream.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getPivotPointsColor(), pref::setPivotPointsColor), resourceBundle.getString("pivot.point.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getPivotPointsSelectedColor(), pref::setPivotPointsSelectedColor), resourceBundle.getString("pivot.point.selected.color"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveBColor1(), pref::setActiveBColor1), resourceBundle.getString("button.b.color.1"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveBColor2(), pref::setActiveBColor2), resourceBundle.getString("button.b.color.2"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveColor1(), pref::setActiveColor1), resourceBundle.getString("button.color.1"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveColor2(), pref::setActiveColor2), resourceBundle.getString("button.color.2"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveRColor1(), pref::setActiveRColor1), resourceBundle.getString("button.r.color.1"));
		addAtRow(modelEditorPanel, new ColorChooserIcon(pref.getActiveRColor2(), pref::setActiveRColor2), resourceBundle.getString("button.r.color.2"));

		modelEditorPanel.add(new JLabel(resourceBundle.getString("window.borders.theme")));

		final JComboBox<GUITheme> themeCheckBox = new JComboBox<>(GUITheme.values());
		themeCheckBox.setSelectedItem(pref.getTheme());
		themeCheckBox.addActionListener(getSettingsChanged(pref, themeCheckBox));
		modelEditorPanel.add(themeCheckBox, "wrap");

		addTab("Colors/Theme", new JScrollPane(modelEditorPanel));
	}

	public void addAtRow(JPanel modelEditorPanel, ColorChooserIcon colorIcon, String s) {
		modelEditorPanel.add(new JLabel(s));
		modelEditorPanel.add(colorIcon, "wrap");
	}

	private void createAndAddHotkeysPanel(ProgramPreferences pref) {
		final JPanel hotkeysPanel = new JPanel();
		hotkeysPanel.setLayout(new MigLayout());

		hotkeysPanel.add(new JLabel(resourceBundle.getString("3d.camera.spin")));
		final JComboBox<MouseButtonPreference> cameraSpinBox = new JComboBox<>(MouseButtonPreference.values());
		cameraSpinBox.setSelectedItem(pref.getThreeDCameraSpinButton());
		cameraSpinBox.addActionListener(e -> pref.setThreeDCameraSpinButton((MouseButtonPreference) cameraSpinBox.getSelectedItem()));
		hotkeysPanel.add(cameraSpinBox, "wrap");


		hotkeysPanel.add(new JLabel(resourceBundle.getString("3d.camera.pan")));
		final JComboBox<MouseButtonPreference> cameraPanBox = new JComboBox<>(MouseButtonPreference.values());
		cameraPanBox.setSelectedItem(pref.getThreeDCameraPanButton());
		cameraPanBox.addActionListener(e -> pref.setThreeDCameraPanButton((MouseButtonPreference) cameraPanBox.getSelectedItem()));
		hotkeysPanel.add(cameraPanBox, "wrap");

		addTab("Hotkeys", hotkeysPanel);
	}

	private ActionListener getSettingsChanged(ProgramPreferences pref, JComboBox<GUITheme> themeCheckBox) {
		return new ActionListener() {
			boolean hasWarned = false;

			@Override
			public void actionPerformed(final ActionEvent e) {
				pref.setTheme((GUITheme) themeCheckBox.getSelectedItem());
				if (!hasWarned) {
					hasWarned = true;
					JOptionPane.showMessageDialog(ProgramPreferencesPanel.this,
							resourceBundle.getString("settings.need.restart"), resourceBundle.getString("warning"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		};
	}

	public List<DataSourceDescriptor> getDataSources() {
		return dataSourceChooserPanel.getDataSourceDescriptors();
	}
}
