package net.daanlokdrog.vampirismthemasquerade.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MasqueradeConfigConfiguration {
	public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;

	public static final ModConfigSpec.ConfigValue<Double> NATURAL_DECAY_INTERVAL_SECONDS;
	public static final ModConfigSpec.ConfigValue<Double> HUNTER_INCREASE_INTERVAL_SECONDS;
	public static final ModConfigSpec.ConfigValue<Double> EXPOSURE_FACTOR_IN_VAMPIRE_FOG;
	public static final ModConfigSpec.ConfigValue<Double> EXPOSURE_FACTOR_IN_VAMPIRE_FOREST;
	public static final ModConfigSpec.ConfigValue<Double> INVISIBILITY_DECAY_RATE;
	public static final ModConfigSpec.ConfigValue<Double> VILLAGER_OBSERVE_RADIUS;
	public static final ModConfigSpec.ConfigValue<Double> HUNTER_OBSERVE_RADIUS;
	public static final ModConfigSpec.ConfigValue<Double> HUNTER_BAR_FILLING_SPEED;
	public static final ModConfigSpec.ConfigValue<Double> HUNT_REINFORCEMENT_FACTOR;
	public static final ModConfigSpec.ConfigValue<Double> SIEGE_THRESHOLD;
	public static final ModConfigSpec.ConfigValue<Double> PANIC_CALCULATION_ADJUSTMENT_VALUE;
	public static final ModConfigSpec.ConfigValue<Boolean> VILLAGE_SCAN;
	public static final ModConfigSpec.ConfigValue<Double> VILLAGE_SCAN_INTERVAL;
	public static final ModConfigSpec.ConfigValue<Double> MAX_DAMAGE_FOR_PANIC;
	public static final ModConfigSpec.ConfigValue<Double> PANIC_BEHAVIOR_INTERVAL;
	public static final ModConfigSpec.ConfigValue<Double> CONVERTED_VILLAGER_SPAWN;
	public static final ModConfigSpec.ConfigValue<Boolean> VILLAGE_FX;
	public static final ModConfigSpec.ConfigValue<Double> MAXIMUM_HEALTH_BONUS;
	public static final ModConfigSpec.ConfigValue<Double> MAXIMUM_ARMOR_BONUS;
	public static final ModConfigSpec.ConfigValue<Double> MAXIMUM_DAMAGE_BONUS;
	public static final ModConfigSpec.ConfigValue<Boolean> DUNAMIS;
	public static final ModConfigSpec.ConfigValue<Boolean> BEAST_MODE;
	public static final ModConfigSpec.ConfigValue<Boolean> BLOODY_FEED;
	public static final ModConfigSpec.ConfigValue<Integer> EYE_TYPE;
	public static final ModConfigSpec.ConfigValue<Integer> FANG_TYPE;
	static {
		BUILDER.push("exposure");
		NATURAL_DECAY_INTERVAL_SECONDS = BUILDER.comment("Exposure natural decay interval, default: 2 (sec)").define("natural_decay_interval_seconds", (double) 2);
		HUNTER_INCREASE_INTERVAL_SECONDS = BUILDER.comment("Exposure increase interval when watched by hunters, default: 2 (sec)").define("hunter_increase_interval_seconds", (double) 2);
		EXPOSURE_FACTOR_IN_VAMPIRE_FOG = BUILDER.comment("Exposure growth adjustment factor in vampire fog, including village. default: 0.5").define("exposure_in_vampire_fog", (double) 0.5);
		EXPOSURE_FACTOR_IN_VAMPIRE_FOREST = BUILDER.comment("Exposure growth adjustment factor in vampire forest biome, default: 0").define("exposure_in_vampire_forest", (double) 0);
		INVISIBILITY_DECAY_RATE = BUILDER.comment("Exposure decay value in invisible. default:3").define("invisibility_decay_rate", (double) 3);
		VILLAGER_OBSERVE_RADIUS = BUILDER.comment("villagers' observe range. default:12").define("villager_observe_radius", (double) 12);
		HUNTER_OBSERVE_RADIUS = BUILDER.comment("hunters' observe range. default:16").define("hunter_observe_radius", (double) 16);
		HUNTER_BAR_FILLING_SPEED = BUILDER.comment("The hunter's gathering speed during the hunting phase. default:0.0417").define("hunter_bar_filling_speed", (double) 0.045);
		HUNT_REINFORCEMENT_FACTOR = BUILDER.comment("The factor by which hunters become stronger as players complete more hunts. default:0.4 (40%)").define("hunt_reinforcement_factor", (double) 0.4);
		SIEGE_THRESHOLD = BUILDER.comment("Intelligence threshold required for hunters to raid your lair").define("siege_threshold", (double) 100);
		BUILDER.pop();
		BUILDER.push("village");
		PANIC_CALCULATION_ADJUSTMENT_VALUE = BUILDER.comment("The proliferation of numbers is directly proportional to the spread of panic. default:1").define("panic_calculation_adjustment_value", (double) 1);
		VILLAGE_SCAN = BUILDER.comment("Enable village scanning? It will mark the village as a Masquerade village so that panic works properly.").define("village_scan", true);
		VILLAGE_SCAN_INTERVAL = BUILDER.comment("For better performance, Masquerade scans the village at intervals.").define("village_scan_interval", (double) 12);
		MAX_DAMAGE_FOR_PANIC = BUILDER.comment("The panic damage cap. Even if the damage goes beyond this value, it won’t cause any extra panic.").define("max_damage_for_panic", (double) 20);
		PANIC_BEHAVIOR_INTERVAL = BUILDER.comment("Scanning intervals of villagers' unusual behavior during a state of panic.").define("panic_behavior_interval", (double) 10);
		CONVERTED_VILLAGER_SPAWN = BUILDER.comment("Converted Villager replaces Villager spawning. (Probability)").define("converted_villager_spawn", (double) 0.05);
		VILLAGE_FX = BUILDER.comment("Play different village sound effects dynamically based on the level of panic. for atmosphere.").define("village_fx", true);
		BUILDER.pop();
		BUILDER.push("territory");
		MAXIMUM_HEALTH_BONUS = BUILDER.comment("Maximum Health Bonus from Habitability.").define("maximum_health_bonus", (double) 40);
		MAXIMUM_ARMOR_BONUS = BUILDER.comment("Maximum Armor Bonus from Local vampire defense.").define("maximum_armor_bonus", (double) 20);
		MAXIMUM_DAMAGE_BONUS = BUILDER.comment("Maximum Damage Bonus from Local vampire damage.").define("maximum_damage_bonus", (double) 10);
		BUILDER.pop();
		BUILDER.push("power");
		DUNAMIS = BUILDER.comment("Whether to enable Dunamis.(A vampire power system)").define("dunamis", false);
		BEAST_MODE = BUILDER.comment("Whether to enable Beast Mode for vampires.(need Dunamis)").define("beast_mode", false);
		BUILDER.pop();
		BUILDER.push("misc");
		BLOODY_FEED = BUILDER.comment("Apply bloodstain overlay after feeding.").define("bloody_feed", true);
		EYE_TYPE = BUILDER.comment("If you add some new eye types, remember to update this value.").define("eye_type", 19);
		FANG_TYPE = BUILDER.comment("If you add some new fang types, remember to update this value.").define("fang_type", 7);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}