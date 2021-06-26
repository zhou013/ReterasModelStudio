package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.builders;

import java.util.HashMap;
import java.util.Map;

import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.factory.UpgradeSingleFieldFactory;
import com.hiveworkshop.rms.parsers.slk.GameObject;
import com.hiveworkshop.rms.parsers.slk.ObjectData;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableObjectData.MutableGameObject;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableObjectData.WorldEditorDataType;
import com.hiveworkshop.rms.util.War3ID;

public class UpgradesFieldBuilder extends AbstractLevelsFieldBuilder {
	private static final War3ID UPGRADE_MAX_LEVEL_FIELD = War3ID.fromString("glvl");
	private final ObjectData upgradeEffectMetaData;
	private final Map<String, GameObject> effectIDToUpgradeEffect = new HashMap<>();

	public UpgradesFieldBuilder(final ObjectData upgradeEffectMetaData) {
		super(new UpgradeSingleFieldFactory(upgradeEffectMetaData), WorldEditorDataType.UPGRADES,
				UPGRADE_MAX_LEVEL_FIELD);
		this.upgradeEffectMetaData = upgradeEffectMetaData;
		for (final String notEffectId : upgradeEffectMetaData.keySet()) {
			final GameObject upgradeEffect = upgradeEffectMetaData.get(notEffectId);
			effectIDToUpgradeEffect.put(upgradeEffect.getField("effectID") + upgradeEffect.getField("dataType"),
					upgradeEffect);
		}
	}

	@Override
	protected boolean includeField(final MutableGameObject gameObject, final GameObject metaDataField,
			final War3ID metaKey) {
		final String effectType = metaDataField.getField("effectType");
		if ("Base".equalsIgnoreCase(effectType) || "Mod".equalsIgnoreCase(effectType)
				|| "Code".equalsIgnoreCase(effectType)) {
            return effectIDToUpgradeEffect.containsKey(
                    gameObject.getFieldAsString(War3ID.fromString("gef" + metaDataField.getId().charAt(3)), 0)
                            + metaDataField.getField("effectType"));
		}
		return true;
	}

}
