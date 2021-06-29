package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditorManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoActionListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.graphics2d.FaceCreationException;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixPopup;
import com.hiveworkshop.rms.ui.gui.modeledit.SkinPopup;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.GenericScaleAction;
import com.hiveworkshop.rms.ui.util.InfoPopup;
import com.hiveworkshop.rms.ui.util.LanguageReader;
import com.hiveworkshop.rms.util.Vec3;
import com.hiveworkshop.rms.util.Vec3SpinnerArray;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ResourceBundle;

public class ViewportPopupMenu extends JPopupMenu {

	private static final ResourceBundle resourceBundle = LanguageReader.getRb();

	ViewportAxis[] axises = {
			new ViewportAxis("Front", (byte) 1, (byte) 2),
			new ViewportAxis("Left", (byte) -1, (byte) 2),
			new ViewportAxis("Back", (byte) -2, (byte) 2),
			new ViewportAxis("Right", (byte) 0, (byte) 2),
			new ViewportAxis("Top", (byte) 1, (byte) -1),
			new ViewportAxis("Bottom", (byte) 1, (byte) 0),
			new ViewportAxis("Top90", (byte) 0, (byte) 1),
			new ViewportAxis("Top180", (byte) -2, (byte) 0),
			new ViewportAxis("Top270", (byte) -1, (byte) -2),
			new ViewportAxis("Bottom90", (byte) -1, (byte) 1),
			new ViewportAxis("Bottom180", (byte) -2, (byte) -1),
			new ViewportAxis("Bottom270", (byte) 0, (byte) -2),
	};
	Viewport viewport;
	UndoActionListener undoListener;
	ModelEditorManager modelEditorManager;
	ModelView modelView;

	public ViewportPopupMenu(Viewport viewport, UndoActionListener undoListener, ModelEditorManager modelEditorManager, ModelView modelView) {
		this.viewport = viewport;
		this.undoListener = undoListener;
		this.modelEditorManager = modelEditorManager;
		this.modelView = modelView;

		JMenu viewMenu = new JMenu(resourceBundle.getString("view"));
		add(viewMenu);

		addMenuItem(resourceBundle.getString("front"), e -> changeViewportAxis(axises[0]), viewMenu);
		addMenuItem(resourceBundle.getString("back"), e -> changeViewportAxis(axises[2]), viewMenu);
		addMenuItem(resourceBundle.getString("top"), e -> changeViewportAxis(axises[4]), viewMenu);
		addMenuItem(resourceBundle.getString("bottom"), e -> changeViewportAxis(axises[5]), viewMenu);
		addMenuItem(resourceBundle.getString("left"), e -> changeViewportAxis(axises[1]), viewMenu);
		addMenuItem(resourceBundle.getString("right"), e -> changeViewportAxis(axises[3]), viewMenu);
//		addMenuItem("Front" , new ChangeViewportAxisAction("Front" , (byte)  1, (byte)  2), viewMenu);
//		addMenuItem("Back"  , new ChangeViewportAxisAction("Back"  , (byte) -2, (byte)  2), viewMenu);
//		addMenuItem("Top"   , new ChangeViewportAxisAction("Top"   , (byte)  1, (byte) -1), viewMenu);
//		addMenuItem("Bottom", new ChangeViewportAxisAction("Bottom", (byte)  1, (byte)  0), viewMenu);
//		addMenuItem("Left"  , new ChangeViewportAxisAction("Left"  , (byte) -1, (byte)  2), viewMenu);
//		addMenuItem("Right" , new ChangeViewportAxisAction("Right" , (byte)  0, (byte)  2), viewMenu);

		JMenu meshMenu = new JMenu(resourceBundle.getString("mesh"));
		add(meshMenu);

		JMenuItem createFace = new JMenuItem(resourceBundle.getString("create.face"));
		createFace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
		createFace.addActionListener(e -> createFace(viewport));
		meshMenu.add(createFace);

		addMenuItem(resourceBundle.getString("split.geoset.and.add.team.color"), e -> undoListener.pushAction(modelEditorManager.getModelEditor().addTeamColor()), meshMenu);
		addMenuItem(resourceBundle.getString("split.geoset"), e -> undoListener.pushAction(modelEditorManager.getModelEditor().splitGeoset()), meshMenu);

		JMenu editMenu = new JMenu(resourceBundle.getString("edit"));
		add(editMenu);

		addMenuItem(resourceBundle.getString("translation.type.in"), e -> manualMove(viewport), editMenu);
		addMenuItem(resourceBundle.getString("rotate.type.in"), e -> manualRotate(viewport), editMenu);
		addMenuItem(resourceBundle.getString("position.type.in"), e -> manualSet(viewport), editMenu);
		addMenuItem(resourceBundle.getString("scale.type.in"), e -> manualScale(viewport), editMenu);

		JMenu matrixMenu = new JMenu(resourceBundle.getString("rig"));
		add(matrixMenu);

		addMenuItem(resourceBundle.getString("selected.mesh.to.selected.nodes"), e -> undoListener.pushAction(modelEditorManager.getModelEditor().rig()), matrixMenu);
		addMenuItem(resourceBundle.getString("re.assign.matrix"), e -> reAssignMatrix(viewport), matrixMenu);
		addMenuItem(resourceBundle.getString("view.matrix"), e -> InfoPopup.show(viewport, modelEditorManager.getModelEditor().getSelectedMatricesDescription()), matrixMenu);
		addMenuItem(resourceBundle.getString("re.assign.hd.skin"), e -> reAssignSkinning(viewport), matrixMenu);
		addMenuItem(resourceBundle.getString("view.hd.skin"), e -> InfoPopup.show(viewport, modelEditorManager.getModelEditor().getSelectedHDSkinningDescription()), matrixMenu);

		JMenu nodeMenu = new JMenu(resourceBundle.getString("node"));
		add(nodeMenu);

		addMenuItem(resourceBundle.getString("set.parent"), e -> setParent(viewport), nodeMenu);
		addMenuItem(resourceBundle.getString("auto.center.bone.s"), e -> undoListener.pushAction(modelEditorManager.getModelEditor().autoCenterSelectedBones()), nodeMenu);
		addMenuItem(resourceBundle.getString("rename.bone"), e -> renameBone(viewport), nodeMenu);
		addMenuItem(resourceBundle.getString("append.bone.suffix"), e -> appendBoneBone(viewport), nodeMenu);
	}


	static void addMenuItem(String itemText, ActionListener actionListener, JMenu menu) {
		JMenuItem menuItem = new JMenuItem(itemText);
		menuItem.addActionListener(actionListener);
		menu.add(menuItem);
	}

	void createFace(Viewport viewport) {
		try {
			undoListener.pushAction(modelEditorManager.getModelEditor().createFaceFromSelection(viewport.getFacingVector()));
		} catch (final FaceCreationException exc) {
			JOptionPane.showMessageDialog(viewport, exc.getMessage(), resourceBundle.getString("error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	void reAssignMatrix(Viewport viewport) {
		MatrixPopup matrixPopup = new MatrixPopup(modelView.getModel());
		String[] words = {resourceBundle.getString("accept"), resourceBundle.getString("cancel")};
		int i = JOptionPane.showOptionDialog(viewport, matrixPopup, resourceBundle.getString("rebuild.matrix"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, words, words[1]);
		if (i == 0) {
			UndoAction reassignMatrixAction = modelEditorManager.getModelEditor().setMatrix(matrixPopup.getNewBoneList());
			undoListener.pushAction(reassignMatrixAction);
		}
	}

	void reAssignSkinning(Viewport viewport) {
		SkinPopup skinPopup = new SkinPopup(modelView);
		String[] words = {resourceBundle.getString("accept"), resourceBundle.getString("cancel")};
		int i = JOptionPane.showOptionDialog(viewport, skinPopup, resourceBundle.getString("rebuild.skin"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, words, words[1]);
		if (i == 0) {
			undoListener.pushAction(modelEditorManager.getModelEditor().setHDSkinning(skinPopup.getBones(), skinPopup.getSkinWeights()));
		}
	}

	void appendBoneBone(Viewport viewport) {
		String name = JOptionPane.showInputDialog(viewport, resourceBundle.getString("enter.bone.suffix"));
		if (name != null) {
			modelEditorManager.getModelEditor().addSelectedBoneSuffix(name);
		}
	}

	void renameBone(Viewport viewport) {
		String name = JOptionPane.showInputDialog(viewport, resourceBundle.getString("enter.bone.name"));
		if (name != null) {
			modelEditorManager.getModelEditor().setSelectedBoneName(name);
		}
	}

	void setParent(Viewport viewport) {
		class NodeShell {
			IdObject node;

			public NodeShell(IdObject node) {
				this.node = node;
			}

			public IdObject getNode() {
				return node;
			}

			@Override
			public String toString() {
				if (node == null) {
					return "(No parent)";
				}
				return node.getName();
			}
		}

		List<IdObject> idObjects = modelView.getModel().getIdObjects();
		NodeShell[] nodeOptions = new NodeShell[idObjects.size() + 1];
		nodeOptions[0] = new NodeShell(null);
		NodeShell defaultChoice = nodeOptions[0];
		for (int i = 0; i < idObjects.size(); i++) {
			IdObject node = idObjects.get(i);
			nodeOptions[i + 1] = new NodeShell(node);
		}
		NodeShell result = (NodeShell) JOptionPane.showInputDialog(viewport,
				resourceBundle.getString("choose.a.parent.node"),
				resourceBundle.getString("set.parent.node"),
				JOptionPane.PLAIN_MESSAGE, null, nodeOptions, defaultChoice);
//		MatrixPopup matrixPopup = new MatrixPopup(modelView.getModel());
		if (result != null) {
			// JOptionPane.showMessageDialog(null,"action approved");
			modelEditorManager.getModelEditor().setParent(result.getNode());
		}
	}

	void manualMove(Viewport viewport) {
		JPanel inputPanel = new JPanel(new MigLayout("gap 0"));
		Vec3SpinnerArray spinners = new Vec3SpinnerArray(new Vec3(0, 0, 0),
				resourceBundle.getString("move.x"),
				resourceBundle.getString("move.y"),
				resourceBundle.getString("move.z"));
		inputPanel.add(spinners.setSpinnerWrap(true).spinnerPanel());
		int x = JOptionPane.showConfirmDialog(viewport.getRootPane(), inputPanel,
				resourceBundle.getString("manual.translation"), JOptionPane.OK_CANCEL_OPTION);
		if (x != JOptionPane.OK_OPTION) {
			return;
		}
		UndoAction translate = modelEditorManager.getModelEditor().translate(spinners.getValue());
		undoListener.pushAction(translate);
	}

	void manualRotate(Viewport viewport) {
		JPanel inputPanel = new JPanel(new MigLayout("gap 0"));
		Vec3SpinnerArray spinners = new Vec3SpinnerArray(new Vec3(0, 0, 0),
				resourceBundle.getString("rotate.x.degrees"),
				resourceBundle.getString("rotate.y.degrees"),
				resourceBundle.getString("rotate.z.degrees"));
		inputPanel.add(spinners.setSpinnerWrap(true).spinnerPanel());
		int x = JOptionPane.showConfirmDialog(viewport.getRootPane(), inputPanel,
				resourceBundle.getString("manual.rotation"), JOptionPane.OK_CANCEL_OPTION);
		if (x != JOptionPane.OK_OPTION) {
			return;
		}

		UndoAction rotate = modelEditorManager.getModelEditor().rotate(modelEditorManager.getModelEditor().getSelectionCenter(), spinners.getValue());
		undoListener.pushAction(rotate);

	}

	void manualSet(Viewport viewport) {
		JPanel inputPanel = new JPanel(new MigLayout("gap 0"));
		Vec3SpinnerArray spinners = new Vec3SpinnerArray(new Vec3(0, 0, 0),
				resourceBundle.getString("new.position.x"),
				resourceBundle.getString("new.position.y"),
				resourceBundle.getString("new.position.z"));
		inputPanel.add(spinners.setSpinnerWrap(true).spinnerPanel());
		int x = JOptionPane.showConfirmDialog(viewport.getRootPane(), inputPanel,
				resourceBundle.getString("manual.position"), JOptionPane.OK_CANCEL_OPTION);
		if (x != JOptionPane.OK_OPTION) {
			return;
		}
		UndoAction setPosition = modelEditorManager.getModelEditor().setPosition(modelEditorManager.getModelEditor().getSelectionCenter(), spinners.getValue());
		undoListener.pushAction(setPosition);
	}

	void manualScale(Viewport viewport) {
		JPanel inputPanel = new JPanel(new MigLayout("gap 0"));
		Vec3SpinnerArray spinners = new Vec3SpinnerArray(new Vec3(1, 1, 1),
				resourceBundle.getString("scale.x"),
				resourceBundle.getString("scale.y"),
				resourceBundle.getString("scale.z"));
		inputPanel.add(spinners.spinnerPanel(), "wrap");
		JCheckBox customOrigin = new JCheckBox(resourceBundle.getString("custom.scaling.origin"));
		inputPanel.add(customOrigin, "wrap");

		Vec3 selectionCenter = modelEditorManager.getModelEditor().getSelectionCenter();
		if (Double.isNaN(selectionCenter.x)) {
			selectionCenter = new Vec3(0, 0, 0);
		}
		Vec3SpinnerArray centerSpinners = new Vec3SpinnerArray(selectionCenter,
				resourceBundle.getString("center.x"),
				resourceBundle.getString("center.y"),
				resourceBundle.getString("center.z"));
		inputPanel.add(centerSpinners.spinnerPanel());
		centerSpinners.setEnabled(false);
		customOrigin.addActionListener(e -> centerSpinners.setEnabled(customOrigin.isSelected()));

		int x = JOptionPane.showConfirmDialog(
				viewport.getRootPane(), inputPanel,
				resourceBundle.getString("manual.scaling"), JOptionPane.OK_CANCEL_OPTION);
		if (x != JOptionPane.OK_OPTION) {
			return;
		}
		Vec3 center = selectionCenter;
		if (customOrigin.isSelected()) {
			center = centerSpinners.getValue();
		}
		GenericScaleAction scalingAction = modelEditorManager.getModelEditor().beginScaling(center);
		scalingAction.updateScale(spinners.getValue());
		undoListener.pushAction(scalingAction);
	}

	private void changeViewportAxis(ViewportAxis axis) {
		viewport.setViewportAxises(axis.name, axis.dim1, axis.dim2);
	}

	private class ViewportAxis {
		private final String name;
		private final byte dim1;
		private final byte dim2;

		public ViewportAxis(String name, byte dim1, byte dim2) {
			this.name = name;
			this.dim1 = dim1;
			this.dim2 = dim2;
		}
	}
}
