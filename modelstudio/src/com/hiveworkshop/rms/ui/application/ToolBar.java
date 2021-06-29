package com.hiveworkshop.rms.ui.application;

import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditorManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ModelEditorMultiManipulatorActivity;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ModelEditorViewportActivity;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.ModelEditorActionType;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.builder.model.*;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionMode;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ToolbarActionButtonType;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ToolbarButtonGroup;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.ui.util.LanguageReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

public class ToolBar {

    private static final ResourceBundle resourceBundle = LanguageReader.getRb();

    public static JToolBar createJToolBar(final MainPanel mainPanel) {
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        toolbar.setFloatable(false);
        FileDialog fileDialog = new FileDialog(mainPanel);

        addToolbarIcon(toolbar, resourceBundle.getString("new"), "new.png", () -> MenuBarActions.newModel(mainPanel));

//        addToolbarIcon(toolbar, "Open", "open.png", () -> MenuBarActions.onClickOpen(mainPanel));
        addToolbarIcon(toolbar, resourceBundle.getString("open"), "open.png", fileDialog::onClickOpen);

//        addToolbarIcon(toolbar, "Save", "save.png", () -> MenuBarActions.onClickSave(mainPanel));
        addToolbarIcon(toolbar, resourceBundle.getString("save"), "save.png", fileDialog::onClickSave);

        toolbar.addSeparator();


        addToolbarIcon(toolbar, resourceBundle.getString("undo"), "undo.png", mainPanel.undoAction);

        addToolbarIcon(toolbar, resourceBundle.getString("redo"), "redo.png", mainPanel.redoAction);

        toolbar.addSeparator();
        mainPanel.selectionModeGroup = new ToolbarButtonGroup<>(toolbar, SelectionMode.values());

        toolbar.addSeparator();

        mainPanel.selectionItemTypeGroup = new ToolbarButtonGroup<>(toolbar, SelectionItemTypes.values());

        toolbar.addSeparator();

        mainPanel.actionTypeGroup = new ToolbarButtonGroup<>(toolbar,
                new ToolbarActionButtonType[] {
                        getMoverWid(mainPanel),
                        getRotatorWid(mainPanel),
                        getScaleWid(mainPanel),
                        getExtrudeWid(mainPanel),
                        getExtendWid(mainPanel),});
        mainPanel.currentActivity = mainPanel.actionTypeGroup.getActiveButtonType();

        toolbar.addSeparator();

        mainPanel.snapButton = addToolbarIcon(toolbar, resourceBundle.getString("snap"), "snap.png", () -> ModelEditActions.snapVertices(mainPanel));

        toolbar.setMaximumSize(new Dimension(80000, 48));
        return toolbar;
    }

    private static ToolbarActionButtonType getExtendWid(MainPanel mainPanel) {
        return new ToolbarActionButtonType(
                RMSIcons.loadToolBarImageIcon("extend.png"), resourceBundle.getString("select.and.extend")) {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView,
                                                              final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                ExtendWidgetManipulatorBuilder ewmb = new ExtendWidgetManipulatorBuilder(
                        modelEditorManager.getModelEditor(), modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView);
                return new ModelEditorMultiManipulatorActivity(ewmb, undoActionListener, modelEditorManager.getSelectionView());
            }
        };
    }

    private static ToolbarActionButtonType getExtrudeWid(MainPanel mainPanel) {
        return new ToolbarActionButtonType(
                RMSIcons.loadToolBarImageIcon("extrude.png"), resourceBundle.getString("select.and.extrude")) {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView,
                                                              final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                ExtrudeWidgetManipulatorBuilder ewmb = new ExtrudeWidgetManipulatorBuilder(
                        modelEditorManager.getModelEditor(), modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView);
                return new ModelEditorMultiManipulatorActivity(ewmb, undoActionListener, modelEditorManager.getSelectionView());
            }
        };
    }

    private static ToolbarActionButtonType getScaleWid(MainPanel mainPanel) {
        return new ToolbarActionButtonType(
                RMSIcons.loadToolBarImageIcon("scale.png"), resourceBundle.getString("select.and.scale")) {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView,
                                                              final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.SCALING;
                ScaleWidgetManipulatorBuilder swmb = new ScaleWidgetManipulatorBuilder(
                        modelEditorManager.getModelEditor(), modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView);
                return new ModelEditorMultiManipulatorActivity(swmb, undoActionListener, modelEditorManager.getSelectionView());
            }
        };
    }

    private static ToolbarActionButtonType getRotatorWid(MainPanel mainPanel) {
        return new ToolbarActionButtonType(
                RMSIcons.loadToolBarImageIcon("rotate.png"), resourceBundle.getString("select.and.rotate")) {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView,
                                                              final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.ROTATION;
                RotatorWidgetManipulatorBuilder rwmb = new RotatorWidgetManipulatorBuilder(
                        modelEditorManager.getModelEditor(), modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView);
                return new ModelEditorMultiManipulatorActivity(rwmb, undoActionListener, modelEditorManager.getSelectionView());
            }
        };
    }

    private static ToolbarActionButtonType getMoverWid(MainPanel mainPanel) {
        return new ToolbarActionButtonType(
                RMSIcons.loadToolBarImageIcon("move2.png"), resourceBundle.getString("select.and.move")) {
            @Override
            public ModelEditorViewportActivity createActivity(final ModelEditorManager modelEditorManager,
                                                              final ModelView modelView,
                                                              final UndoActionListener undoActionListener) {
                mainPanel.actionType = ModelEditorActionType.TRANSLATION;
                MoverWidgetManipulatorBuilder mwmb = new MoverWidgetManipulatorBuilder(
                        modelEditorManager.getModelEditor(), modelEditorManager.getViewportSelectionHandler(), mainPanel.prefs, modelView);
                return new ModelEditorMultiManipulatorActivity(mwmb, undoActionListener, modelEditorManager.getSelectionView());
            }
        };
    }

    static JButton addToolbarIcon(JToolBar toolbar, String hooverText, String icon, AbstractAction action) {
        JButton button = new JButton(RMSIcons.loadToolBarImageIcon(icon));
        button.setToolTipText(hooverText);
        button.addActionListener(action);
        toolbar.add(button);
        return button;
    }

    static JButton addToolbarIcon(JToolBar toolbar, String hooverText, String icon, Runnable function) {
        AbstractAction action = new AbstractAction(hooverText) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                try {
                    function.run();
                } catch (final Exception exc) {
                    exc.printStackTrace();
                    ExceptionPopup.display(exc);
                }
            }
        };

        JButton button = new JButton(RMSIcons.loadToolBarImageIcon(icon));
        button.setToolTipText(hooverText);
        button.addActionListener(action);
        toolbar.add(button);
        return button;
    }

}
