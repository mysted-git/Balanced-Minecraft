package com.balancedmc;

import com.balancedmc.enchantments.ModEnchantments;
import com.balancedmc.entity.EntitySpawns;
import com.balancedmc.entity.ModEntities;
import com.balancedmc.items.ModItems;
import com.balancedmc.loot_tables.ModLootTables;
import com.balancedmc.potion.ModPotionRecipes;
import com.balancedmc.sounds.ModSoundEvents;
import com.balancedmc.villagers.VillagerHelper;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {

	public static final String MOD_ID = "balancedmc";
	public static final String MOD_VERSION = "1.2";
	public static final Logger LOGGER = LoggerFactory.getLogger("BalancedMinecraft");

	@Override
	public void onInitialize() {

		ModSoundEvents.registerSoundEvents();
		ModItems.registerItems();
		ModEnchantments.registerEnchantments();
		ModPotionRecipes.registerPotionRecipes();
		ModLootTables.registerLootTables();
		EntitySpawns.registerEntitySpawns();
		ModEntities.registerEntities();

		VillagerHelper.registerReloadListener();
	}

	public static void log(Object ... objs) {
		StringBuilder builder = new StringBuilder();
		for (Object o : objs) {
			builder.append(" ");
			builder.append(o);
		}
		Main.LOGGER.info(builder.substring(1));
	}
}
