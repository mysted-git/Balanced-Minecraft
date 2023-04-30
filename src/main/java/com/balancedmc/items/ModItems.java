package com.balancedmc.items;

import com.balancedmc.Main;
import com.balancedmc.sounds.ModSoundEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static void registerItems() {}

    public static final Item MUSIC_DISC_INTRO;
    public static final Item MUSIC_DISC_DROOPY_LIKES_RICOCHET;
    public static final Item MUSIC_DISC_DROOPY_LIKES_YOUR_FACE;
    public static final Item MUSIC_DISC_INTRO_DOG;
    public static final Item MUSIC_DISC_INTRO_DEATH;

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
    
    static {
        MUSIC_DISC_INTRO = register("music_disc_intro", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_INTRO, 10000));
        MUSIC_DISC_DROOPY_LIKES_RICOCHET = register("music_disc_droopy_likes_ricochet", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_RICOCHET, 10000));
        MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register("music_disc_droopy_likes_your_face", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_YOUR_FACE, 10000));
        MUSIC_DISC_INTRO_DOG = register("music_disc_dog", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOG, 10000));
        MUSIC_DISC_INTRO_DEATH = register("music_disc_death", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DEATH, 10000));
    }

    /*
           MUSIC_DISC_INTRO = register("music_disc_intro", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_INTRO, 276));
        MUSIC_DISC_DROOPY_LIKES_RICOCHET = register("music_disc_droopy_likes_ricochet", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_RICOCHET, 95));
        MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register("music_disc_droopy_likes_your_face", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_YOUR_FACE, 117));
        MUSIC_DISC_INTRO_DOG = register("music_disc_dog", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOG, 147));
        MUSIC_DISC_INTRO_DEATH = register("music_disc_death", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DEATH, 42));
     */

}
