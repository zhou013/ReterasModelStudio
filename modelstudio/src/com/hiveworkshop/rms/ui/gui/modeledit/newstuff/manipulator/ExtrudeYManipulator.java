package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.manipulator;

import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;

import java.awt.geom.Point2D.Double;

public final class ExtrudeYManipulator extends AbstractExtrudeManipulator {

	public ExtrudeYManipulator(final ModelEditor modelEditor) {
		super(modelEditor);
	}

	@Override
	protected void buildMoveVector(final Double mouseStart, final Double mouseEnd, final byte dim1, final byte dim2) {
		moveVector.setCoord(dim2, mouseEnd.y - mouseStart.y);
	}

}