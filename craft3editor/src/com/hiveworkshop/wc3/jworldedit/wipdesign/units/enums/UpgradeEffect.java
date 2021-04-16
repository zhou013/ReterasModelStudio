package com.hiveworkshop.wc3.jworldedit.wipdesign.units.enums;

import com.hiveworkshop.wc3.resources.WEString;

public enum UpgradeEffect {
	APPLY_DEFENSE_UPGRADE_BONUS("rarm","WESTRING_GE_UPGRADEEFFECT_ARMOR"), APPLY_ATTACK_UPGRADE_BONUS("ratt","WESTRING_GE_UPGRADEEFFECT_ATTACK"), ATTACK_DAMAGE_BONUS("ratx","WESTRING_GE_UPGRADEEFFECT_ATTACKEX"), ATTACK_DAMAGE_LOSS_BONUS("radl","WESTRING_GE_UPGRADEEFFECT_ATTACKDMGLOSS"), ATTACK_DICE_BONUS("ratd","WESTRING_GE_UPGRADEEFFECT_ATTACKDICE"), ATTACK_RANGE_BONUS("ratr","WESTRING_GE_UPGRADEEFFECT_ATTACKRANGE"), ATTACK_SPEED_BONUS_PERCENT("rats","WESTRING_GE_UPGRADEEFFECT_ATTACKSPEED"), ATTACK_SPILL_DISTANCE_BONUS("rasd","WESTRING_GE_UPGRADEEFFECT_ATTACKSPILLDST"), ATTACK_SPILL_RADIUS_BONUS("rasr","WESTRING_GE_UPGRADEEFFECT_ATTACKSPILLRAD"), ATTACK_TARGET_COUNT_BONUS("ratc","WESTRING_GE_UPGRADEEFFECT_ATTACKTARGCOUNT"), AURA_DATA_BONUS("raud","WESTRING_GE_UPGRADEEFFECT_AURADATA"), ENABLE_ATTACKS("renw","WESTRING_GE_UPGRADEEFFECT_ENABLEWEAPON"), GOLD_HARVEST_BONUS_ENTANGLE("rent","WESTRING_GE_UPGRADEEFFECT_ENTANGLE"), HIT_POINT_BONUS_PERCENT("rhpo","WESTRING_GE_UPGRADEEFFECT_HITPOINTS"), HIT_POINT_BONUS("rhpx","WESTRING_GE_UPGRADEEFFECT_HITPOINTSEX"), HIT_POINT_REGENERATION("rhpr","WESTRING_GE_UPGRADEEFFECT_HITPOINTSREGEN"), LUMBER_HARVEST_BONUS("rlum","WESTRING_GE_UPGRADEEFFECT_LUMBERJACK"), MAGIC_IMMUNITY("rmim","WESTRING_GE_UPGRADEEFFECT_MAGICIMMUNITY"), MANA_POINT_BONUS_PERCENT("rman","WESTRING_GE_UPGRADEEFFECT_MANA"), MANA_POINT_BONUS("rmnx","WESTRING_GE_UPGRADEEFFECT_MANAEX"), MANA_REGENERATION("rmnr","WESTRING_GE_UPGRADEEFFECT_MANAREGEN"), GOLD_HARVEST_BONUS("rmin","WESTRING_GE_UPGRADEEFFECT_MINING"), MOVEMENT_SPEED_BONUS_PERCENT("rmov","WESTRING_GE_UPGRADEEFFECT_MOVESPEED"), MOVEMENT_SPEED_BONUS("rmvx","WESTRING_GE_UPGRADEEFFECT_MOVESPEEDEX"), RAISE_DEAD_DURATION_BONUS("rrai","WESTRING_GE_UPGRADEEFFECT_RAISEDEAD"), ENABLE_ATTACKS_ROOTED("rroo","WESTRING_GE_UPGRADEEFFECT_ROOTWEAPONS"), SIGHT_RANGE_BONUS("rsig","WESTRING_GE_UPGRADEEFFECT_SIGHT"), ABILITY_LEVEL_BONUS("rlev","WESTRING_GE_UPGRADEEFFECT_SPELL"), SPIKED_BARRICADES("rspi","WESTRING_GE_UPGRADEEFFECT_SPIKES"), ENABLE_ATTACKS_UPROOTED("ruro","WESTRING_GE_UPGRADEEFFECT_UPROOTWEAPONS"), UNIT_AVAILABILITY_CHANGE("rtma","WESTRING_GE_UPGRADEEFFECT_UNITAVAILABLE"), DEFENSE_TYPE_CHANGE("rart","WESTRING_GE_UPGRADEEFFECT_DEFENSETYPE"), ADD_ULTRAVISION("rauv","WESTRING_GE_UPGRADEEFFECT_ULTRAVISION");
	private final String codeName;
	private final String dispName;
	UpgradeEffect(final String codeName, final String dispName) {
		this.codeName = codeName;
		this.dispName = dispName;
	}
	public final String getDisplayName() {
		return WEString.getString(dispName);
	}
	final public String getCodeName() {
		return codeName;
	}
	public static UpgradeEffect fromCodeName(final String name) {
		for(final UpgradeEffect cat: values()) {
			if( cat.getCodeName().equals(name) ) {
				return cat;
			}
		}
		throw new IllegalArgumentException("UpgradeEffect does not exist: " + name);
	}
}