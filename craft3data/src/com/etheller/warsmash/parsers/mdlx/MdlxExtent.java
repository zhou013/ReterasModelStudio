package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.etheller.warsmash.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.etheller.warsmash.util.MdlUtils;
import com.etheller.warsmash.util.ParseUtils;
import com.google.common.io.LittleEndianDataOutputStream;
import com.hiveworkshop.util.BinaryReader;

public class MdlxExtent {
	public float boundsRadius = 0;
	public float[] min = new float[3];
	public float[] max = new float[3];

	public void readMdx(final BinaryReader reader) throws IOException {
		this.boundsRadius = reader.readFloat32();
		reader.readFloat32Array(this.min);
		reader.readFloat32Array(this.max);
	}

	public void writeMdx(final LittleEndianDataOutputStream stream) throws IOException {
		stream.writeFloat(this.boundsRadius);
		ParseUtils.writeFloatArray(stream, this.min);
		ParseUtils.writeFloatArray(stream, this.max);
	}

	public void writeMdl(final MdlTokenOutputStream stream) {
		if ((this.min[0] != 0) || (this.min[1] != 0) || (this.min[2] != 0)) {
			stream.writeFloatArrayAttrib(MdlUtils.TOKEN_MINIMUM_EXTENT, this.min);
		}
		if ((this.max[0] != 0) || (this.max[1] != 0) || (this.max[2] != 0)) {
			stream.writeFloatArrayAttrib(MdlUtils.TOKEN_MAXIMUM_EXTENT, this.max);
		}

		if (this.boundsRadius != 0) {
			stream.writeFloatAttrib(MdlUtils.TOKEN_BOUNDSRADIUS, this.boundsRadius);
		}
	}

	public void setBoundsRadius(final float boundsRadius) {
		this.boundsRadius = boundsRadius;
	}
}
