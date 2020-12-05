package com.hiveworkshop.rms.parsers.mdlx;

import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenInputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlTokenOutputStream;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.util.BinaryReader;
import com.hiveworkshop.rms.util.BinaryWriter;
import com.hiveworkshop.rms.util.War3ID;

import java.util.ArrayList;
import java.util.List;

public class MdlxMaterial implements MdlxBlock, MdlxChunk {
	public static final War3ID LAYS = War3ID.fromString("LAYS");
	public int priorityPlane = 0;
	public int flags;
	/**
	 * @since 900
	 */
	public String shader = "";
	public final List<MdlxLayer> layers = new ArrayList<>();

	@Override
	public void readMdx(final BinaryReader reader, final int version) {
		long throwawaySize = reader.readUInt32(); // Don't care about the size
		System.out.println("MdlxMaterial throwawaySize: " + throwawaySize);

		priorityPlane = reader.readInt32();
		System.out.println("MdlxMaterial priorityPlane: " + priorityPlane);
		if (version != 1300) {
			flags = reader.readInt32();
			System.out.println("MdlxMaterial flags: " + flags);
		}

		if (version > 800 && version != 1300) {
			shader = reader.read(80);
		}

		if (version != 1300) {
			reader.readInt32(); // skip LAYS
		}

		final long layerCount = reader.readUInt32();
		System.out.println("MdlxMaterial layerCount: " + layerCount);
		for (int i = 0; i < layerCount; i++) {
			final MdlxLayer layer = new MdlxLayer();
			layer.readMdx(reader, version);
			layers.add(layer);
		}
	}

	@Override
	public void writeMdx(final BinaryWriter writer, final int version) {
		writer.writeUInt32(getByteLength(version));
		writer.writeInt32(priorityPlane);
		writer.writeInt32(flags);

		if (version > 800) {
			writer.writeWithNulls(shader, 80);
		}

		writer.writeTag(LAYS.getValue());
		writer.writeUInt32(layers.size());

		for (final MdlxLayer layer : layers) {
			layer.writeMdx(writer, version);
		}
	}

	@Override
	public void readMdl(final MdlTokenInputStream stream, final int version) {
		for (final String token : stream.readBlock()) {
			switch (token) {
				case MdlUtils.TOKEN_CONSTANT_COLOR:
					flags |= 0x1;
					break;
				case MdlUtils.TOKEN_SORT_PRIMS_NEAR_Z:
					flags |= 0x8;
					break;
				case MdlUtils.TOKEN_SORT_PRIMS_FAR_Z:
					flags |= 0x10;
					break;
				case MdlUtils.TOKEN_FULL_RESOLUTION:
					flags |= 0x20;
					break;
				case MdlUtils.TOKEN_PRIORITY_PLANE:
					priorityPlane = stream.readInt();
					break;
				case "Shader":
					shader = stream.read();
					break;
				case MdlUtils.TOKEN_LAYER: {
					final MdlxLayer layer = new MdlxLayer();
					layer.readMdl(stream, version);
					layers.add(layer);
					break;
				}
				default:
					throw new RuntimeException("Unknown token in Material: " + token);
			}
		}
	}

	@Override
	public void writeMdl(final MdlTokenOutputStream stream, final int version) {
		stream.startBlock(MdlUtils.TOKEN_MATERIAL);

		if ((flags & 0x1) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_CONSTANT_COLOR);
		}

		if ((flags & 0x8) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SORT_PRIMS_NEAR_Z);
		}

		if ((flags & 0x10) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_SORT_PRIMS_FAR_Z);
		}

		if ((flags & 0x20) != 0) {
			stream.writeFlag(MdlUtils.TOKEN_FULL_RESOLUTION);
		}

		if (priorityPlane != 0) {
			stream.writeAttrib(MdlUtils.TOKEN_PRIORITY_PLANE, priorityPlane);
		}

		if (version > 800) {
			stream.writeStringAttrib("Shader", shader);
		}

		for (final MdlxLayer layer : layers) {
			layer.writeMdl(stream, version);
		}

		stream.endBlock();
	}

	@Override
	public long getByteLength(final int version) {
		long size = 20;

		if (version > 800) {
			size += 80;
		}

		for (final MdlxLayer layer : layers) {
			size += layer.getByteLength(version);
		}

		return size;
	}
}
