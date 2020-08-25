package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Matrix4;
import com.hiveworkshop.rms.editor.model.QuaternionRotation;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.model.Vertex;
import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;

import org.lwjgl.util.vector.Vector4f;

public class ViewportRenderableCamera {
	private final EditableModel cameraModel;
	private double cameraLength;
	private final Matrix4 rotationMatrix = new Matrix4();
	private final Matrix4 scaleTranslateMatrix = new Matrix4();
	private final Matrix4 translateMatrix = new Matrix4();
	private static final Vertex f = new Vertex();
	private static final Vertex u = new Vertex();
	private static final Vertex s = new Vertex();
	private static final Vertex start = new Vertex(0, 0, 0);
	private static final Vertex end = new Vertex(0, 0, 0);
	private static final Vertex startVector = new Vertex(0, 0, 0);
	private static final Vertex endVector = new Vertex(0, 0, 0);
	private static final Vertex delta = new Vertex(0, 0, 0);
	private static final Vertex vector3heap = new Vertex(0, 0, 0);
	private static final Vertex Z_DIMENSION = new Vertex(0, 0, 1);
	private static final QuaternionRotation quatHeap = new QuaternionRotation();
	private static final QuaternionRotation quatRotHeap = new QuaternionRotation(0, 0, 0, 0);
	private static final Vector4f vectorHeap = new Vector4f();
	private static final Vertex ZEROES = new Vertex(0, 0, 0);
	private static final Vertex ONES = new Vertex(1, 1, 1);
	private static final Vertex quatRotAxisHeap = new Vertex(0, 0, 0);

	public ViewportRenderableCamera() {
		EditableModel camera;
		try {
			camera = new EditableModel(MdxUtils.loadMdlx(GameDataFileSystem.getDefault().getResourceAsStream("Objects\\CameraHelper\\CameraHelper.mdx")));
			cameraLength = Math.abs(camera.getIdObject(0).getPivotPoint().x);
		} catch (final IOException e) {
			camera = null;
			e.printStackTrace();
			ExceptionPopup.display(e);
		}
		cameraModel = camera;
	}

	private void lookAt(final Vertex eye, final Vertex center, final Vertex up) {
		Vertex.sub(center, eye, f);
		f.normalize();
		u.set(up);
		u.normalize();
		Vertex.cross(f, u, s);
		s.normalize();
		Vertex.cross(s, f, u);

		rotationMatrix.setIdentity();
		rotationMatrix.m00 = f.x;
		rotationMatrix.m01 = f.y;
		rotationMatrix.m02 = f.z;
		rotationMatrix.m10 = s.x;
		rotationMatrix.m11 = s.y;
		rotationMatrix.m12 = s.z;
		rotationMatrix.m20 = u.x;
		rotationMatrix.m21 = u.y;
		rotationMatrix.m22 = u.z;
	}

	public void render(final Graphics2D graphics, final CoordinateSystem coordinateSystem, final double startX,
			final double startY, final double startZ, final double endX, final double endY, final float endZ,
			final double rotation) {
		start.set(startX, startY, startZ);
		end.set(endX, endY, endZ);

		startVector.x = (float) start.x;
		startVector.y = (float) start.y;
		startVector.z = (float) start.z;
		endVector.x = (float) end.x;
		endVector.y = (float) end.y;
		endVector.z = (float) end.z;

		delta.set((float) end.x, (float) end.y, (float) end.z);
		delta.x -= (float) start.x;
		delta.y -= (float) start.y;
		delta.z -= (float) start.z;
		final float length = delta.length();
		final double cameraModelScale = length / cameraLength;

		lookAt(startVector, endVector, Z_DIMENSION);
		scaleTranslateMatrix.setIdentity();
		vector3heap.set((float) (end.x), (float) (end.y), (float) (end.z));
		scaleTranslateMatrix.translate(vector3heap);
		Matrix4.mul(scaleTranslateMatrix, rotationMatrix, scaleTranslateMatrix);
		vector3heap.set((float) cameraModelScale, (float) cameraModelScale, (float) cameraModelScale);
		scaleTranslateMatrix.scale(vector3heap);

		final Point[] points = new Point[3];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Point(0, 0);
		}
		for (final Geoset geoset : cameraModel.getGeosets()) {
			for (final Triangle triangle : geoset.getTriangles()) {
				for (int i = 0; i < 3; i++) {
					final GeosetVertex vertex = triangle.getVerts()[i];
					vectorHeap.x = (float) (vertex.x);
					vectorHeap.y = (float) (vertex.y);
					vectorHeap.z = (float) (vertex.z);
					vectorHeap.w = 1;
					Matrix4.transform(scaleTranslateMatrix, vectorHeap, vectorHeap);
					points[i].x = (int) coordinateSystem
							.convertX(Vertex.getCoord(vectorHeap, coordinateSystem.getPortFirstXYZ()));
					points[i].y = (int) coordinateSystem
							.convertY(Vertex.getCoord(vectorHeap, coordinateSystem.getPortSecondXYZ()));
				}
				graphics.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
				graphics.drawLine(points[2].x, points[2].y, points[1].x, points[1].y);
				graphics.drawLine(points[2].x, points[2].y, points[0].x, points[0].y);
			}
		}
	}

	public void render(final double startX, final double startY, final double startZ, final double endX,
			final double endY, final double endZ, final double rotation) {
		start.set(startX, startY, startZ);
		end.set(endX, endY, endZ);

		startVector.x = (float) start.x;
		startVector.y = (float) start.y;
		startVector.z = (float) start.z;
		endVector.x = (float) end.x;
		endVector.y = (float) end.y;
		endVector.z = (float) end.z;

		delta.set((float) end.x, (float) end.y, (float) end.z);
		delta.x -= (float) start.x;
		delta.y -= (float) start.y;
		delta.z -= (float) start.z;
		final float length = delta.length();
		final double cameraModelScale = length / cameraLength;

		lookAt(startVector, endVector, Z_DIMENSION);
		scaleTranslateMatrix.setIdentity();
		vector3heap.set((float) (end.x), (float) (end.y), (float) (end.z));
		scaleTranslateMatrix.translate(vector3heap);
		Matrix4.mul(rotationMatrix, scaleTranslateMatrix, scaleTranslateMatrix);
		vector3heap.set((float) cameraModelScale, (float) cameraModelScale, (float) cameraModelScale);
		scaleTranslateMatrix.scale(vector3heap);

		final Point[] points = new Point[3];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Point(0, 0);
		}
		for (final Geoset geoset : cameraModel.getGeosets()) {
			for (final Triangle triangle : geoset.getTriangles()) {
				for (int i = 0; i < 3; i++) {
					final GeosetVertex vertex = triangle.getVerts()[i];
					vectorHeap.x = (float) (vertex.x);
					vectorHeap.y = (float) (vertex.y);
					vectorHeap.z = (float) (vertex.z);
					vectorHeap.w = 1;
					Matrix4.transform(scaleTranslateMatrix, vectorHeap, vectorHeap);
				}
			}
		}
	}
}
