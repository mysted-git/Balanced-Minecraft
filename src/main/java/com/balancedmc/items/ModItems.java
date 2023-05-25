package com.balancedmc.items;

import com.balancedmc.Main;
import com.balancedmc.sounds.ModSoundEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static void registerItems() {}

    public static final Item MUSIC_DISC_INTRO = register("music_disc_intro", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_INTRO, 276));
    public static final Item MUSIC_DISC_DROOPY_LIKES_RICOCHET = register("music_disc_droopy_likes_ricochet", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_RICOCHET, 95));
    public static final Item MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register("music_disc_droopy_likes_your_face", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_YOUR_FACE, 117));
    public static final Item MUSIC_DISC_DOG = register("music_disc_dog", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOG, 147));
    public static final Item MUSIC_DISC_DEATH = register("music_disc_death", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DEATH, 42));
    public static final Item MUSIC_DISC_KEY = register("music_disc_key", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_KEY, 65));
    public static final Item MUSIC_DISC_DOOR = register("music_disc_door", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOOR, 108));

    private static Item register(String name, Item item) {
        Item registeredItem;
        if (item instanceof MusicDiscItem) {
            registeredItem = Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), item);
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(registeredItem));
        } else {
            return null;
        }
        return registeredItem;
    }

}
