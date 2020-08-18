package com.etheller.warsmash.parsers.mdlx;

import java.io.IOException;

import com.google.common.io.LittleEndianDataOutputStream;
import com.hiveworkshop.util.BinaryReader;
import com.etheller.warsmash.parsers.mdlx.mdl.MdlTokenInputStream;
import com.etheller.warsmash.parsers.mdlx.mdl.MdlTokenOutputStream;

public interface MdlxBlock {
	void readMdx(final BinaryReader reader, final int version) throws IOException;

	void writeMdx(final LittleEndianDataOutputStream stream, final int version) throws IOException;

	void readMdl(final MdlTokenInputStream stream, final int version) throws IOException;

	void writeMdl(final MdlTokenOutputStream stream, final int version) throws IOException;
}
