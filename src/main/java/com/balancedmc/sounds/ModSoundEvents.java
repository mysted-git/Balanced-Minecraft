package com.balancedmc.sounds;

import com.balancedmc.Main;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;

public class ModSoundEvents {

    public ModSoundEvents() {}

    public static SoundEvent MUSIC_DISC_INTRO;
    public static SoundEvent MUSIC_DISC_DROOPY_LIKES_RICOCHET;
    public static SoundEvent MUSIC_DISC_DROOPY_LIKES_YOUR_FACE;
    public static SoundEvent MUSIC_DISC_DOG;
    public static SoundEvent MUSIC_DISC_DEATH;

    private static SoundEvent registerRecord(String name) {
        Identifier id = new Identifier(Main.MOD_ID, "music_disc." + name);
        SoundEvent event = SoundEvent.of(id, 75.0F);
        return Registry.register(Registries.SOUND_EVENT, id, event);
    }

    public static void registerSoundEvents() {
        MUSIC_DISC_INTRO = registerRecord("intro");
        MUSIC_DISC_DROOPY_LIKES_RICOCHET = registerRecord("droopy_likes_ricochet");
        MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = registerRecord("droopy_likes_your_face");
        MUSIC_DISC_DOG = registerRecord("dog");
        MUSIC_DISC_DEATH = registerRecord("death");
    }


}
