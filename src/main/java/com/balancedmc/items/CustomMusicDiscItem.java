package com.balancedmc.items;

import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Rarity;

public class CustomMusicDiscItem extends MusicDiscItem {

    public CustomMusicDiscItem(SoundEvent sound, int lengthInSeconds) {
        super(15, sound, new Item.Settings().maxCount(1).rarity(Rarity.RARE), lengthInSeconds);
    }

}
