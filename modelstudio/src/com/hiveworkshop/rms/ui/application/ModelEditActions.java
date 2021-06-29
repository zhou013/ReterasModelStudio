package com.hiveworkshop.rms.ui.application;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class ModelEditActions {
    static double lastNormalMaxAngle = 90;
    static boolean useTris = false;
    private static final ResourceBundle resourceBundle = LanguageReader.getRb();

    static void viewMatrices(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            modelPanel.viewMatrices();
        }
        mainPanel.repaint();
    }

    static void inverseAllUVs(MainPanel mainPanel) {
        for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
            for (final GeosetVertex vertex : geo.getVertices()) {
                for (final Vec2 tvert : vertex.getTverts()) {
                    final float temp = tvert.x;
                    tvert.x = tvert.y;
                    tvert.y = temp;
                }
            }
        }
        mainPanel.repaint();
    }

    static void flipAllUVsV(MainPanel mainPanel) {
        for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
            for (final GeosetVertex vertex : geo.getVertices()) {
                for (final Vec2 tvert : vertex.getTverts()) {
                    tvert.y = 1.0f - tvert.y;
                }
            }
        }
        mainPanel.repaint();
    }

    static void flipAllUVsU(MainPanel mainPanel) {
        for (final Geoset geo : mainPanel.currentMDL().getGeosets()) {
            for (final GeosetVertex vertex : geo.getVertices()) {
                for (final Vec2 tvert : vertex.getTverts()) {
                    tvert.x = 1.0f - tvert.x;
                }
            }
        }
        mainPanel.repaint();
    }

    static void insideOutNormals(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor().flipSelectedNormals());
        }
        mainPanel.repaint();
    }

    static void insideOut(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor().flipSelectedFaces());
        }
        mainPanel.repaint();
    }

    static void snapVertices(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor().snapSelectedVertices());
        }
        mainPanel.repaint();
    }

    static void snapNormals(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor().snapNormals());
        }
        mainPanel.repaint();
    }

    static void recalculateNormals(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            JPanel panel = new JPanel(new MigLayout());
            panel.add(new JLabel(resourceBundle.getString("limiting.angle")));
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(lastNormalMaxAngle, -180.0, 180.0, 1));
            panel.add(spinner, "wrap");
            panel.add(new JLabel(resourceBundle.getString("use.triangles.instead.of.vertices")));
            JCheckBox useTries = new JCheckBox();
            useTries.setSelected(useTris);
            panel.add(useTries);
            int option = JOptionPane.showConfirmDialog(mainPanel, panel, resourceBundle.getString("recalculate.normals"), JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                lastNormalMaxAngle = (double) spinner.getValue();
                useTris = useTries.isSelected();
                modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor().recalcNormals(lastNormalMaxAngle, useTris));
            }
        }
        mainPanel.repaint();
    }

    static void recalculateExtents(MainPanel mainPanel) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            final JPanel messagePanel = new JPanel(new MigLayout());
            messagePanel.add(new JLabel(resourceBundle.getString("recalculate.extents.confirmation")),
                    "wrap");
            messagePanel.add(new JLabel(resourceBundle.getString("it.may.destroy.existing.extents")), "wrap");
            final JRadioButton considerAllBtn = new JRadioButton(resourceBundle.getString("consider.all.geosets.for.calculation"));
            final JRadioButton considerCurrentBtn = new JRadioButton(
                    resourceBundle.getString("consider.current.editable.geosets.for.calculation"));
            final ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(considerAllBtn);
            buttonGroup.add(considerCurrentBtn);
            considerAllBtn.setSelected(true);
            messagePanel.add(considerAllBtn, "wrap");
            messagePanel.add(considerCurrentBtn, "wrap");
            final int userChoice = JOptionPane.showConfirmDialog(mainPanel, messagePanel, resourceBundle.getString("message"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (userChoice == JOptionPane.YES_OPTION) {
                modelPanel.getUndoManager().pushAction(modelPanel.getModelEditorManager().getModelEditor()
                        .recalcExtents(considerCurrentBtn.isSelected()));
            }
        }
        mainPanel.repaint();
    }

    static void mirrorAxis(MainPanel mainPanel, byte i, boolean mirrorFlip) {
        final ModelPanel modelPanel = mainPanel.currentModelPanel();
        if (modelPanel != null) {
            final Vec3 selectionCenter = modelPanel.getModelEditorManager().getModelEditor().getSelectionCenter();
            modelPanel.getUndoManager().pushAction(
                    modelPanel.getModelEditorManager().getModelEditor()
                            .mirror(i, mirrorFlip, selectionCenter.x, selectionCenter.y, selectionCenter.z));
        }
        mainPanel.repaint();
    }

    static void linearizeAnimations(MainPanel mainPanel) {
        final int x = JOptionPane.showConfirmDialog(mainPanel,
                resourceBundle.getString("linearize.animations.confirmation"),
                resourceBundle.getString("warning.linearize.animations"), JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            final List<AnimFlag<?>> allAnimFlags = mainPanel.currentMDL().getAllAnimFlags();
            for (final AnimFlag<?> flag : allAnimFlags) {
                flag.linearize();
            }
        }
    }

    static void simplifyKeyframes(MainPanel mainPanel) {
        final int x = JOptionPane.showConfirmDialog(mainPanel,
                resourceBundle.getString("simplify.keyframes.confirmation"),
                resourceBundle.getString("warning.simplify.keyframes"), JOptionPane.OK_CANCEL_OPTION);
        if (x == JOptionPane.OK_OPTION) {
            final EditableModel currentMDL = mainPanel.currentMDL();
            currentMDL.simplifyKeyframes();
        }
    }
}
