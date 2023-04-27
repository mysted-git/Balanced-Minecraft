package com.balancedmc;

import com.balancedmc.sounds.ModSoundEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item MUSIC_DISC_INTRO = register("music_disc_intro", new MusicDiscItem(0, ModSoundEvents.MUSIC_DISC_INTRO, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 276));
    public static final Item MUSIC_DISC_DROOPY_LIKES_RICOCHET = register("music_disc_droopy_likes_ricochet", new MusicDiscItem(0, ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_RICOCHET, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 95));
    public static final Item MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register("music_disc_droopy_likes_your_face", new MusicDiscItem(0, ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_YOUR_FACE, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 117));
    public static final Item MUSIC_DISC_INTRO_DOG = register("music_disc_dog", new MusicDiscItem(0, ModSoundEvents.MUSIC_DISC_DOG, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 147));
    public static final Item MUSIC_DISC_INTRO_DEATH = register("music_disc_death", new MusicDiscItem(0, ModSoundEvents.MUSIC_DISC_DEATH, new Item.Settings().maxCount(1).rarity(Rarity.RARE), 42));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), item);
    }

    public static void registerItems() {
        groupItems();
        Main.LOGGER.info("Registered balancedmc items");
    }

    private static void groupItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(MUSIC_DISC_INTRO));
    }

}
