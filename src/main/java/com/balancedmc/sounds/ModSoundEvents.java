package com.balancedmc.sounds;

import com.balancedmc.Main;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static SoundEvent MUSIC_DISC_INTRO;
    public static final Identifier MUSIC_DISC_INTRO_ID = new Identifier(Main.MOD_ID + ":music_disc.intro");
    public static final SoundEvent MUSIC_DISC_INTRO_EVENT = SoundEvent.of(MUSIC_DISC_INTRO_ID);
    public static SoundEvent MUSIC_DISC_DROOPY_LIKES_RICOCHET;
    public static final Identifier MUSIC_DISC_DROOPY_LIKES_RICOCHET_ID = new Identifier(Main.MOD_ID + ":music_disc.droopy_likes_ricochet");
    public static final SoundEvent MUSIC_DISC_DROOPY_LIKES_RICOCHET_EVENT = SoundEvent.of(MUSIC_DISC_DROOPY_LIKES_RICOCHET_ID);
    public static SoundEvent MUSIC_DISC_DROOPY_LIKES_YOUR_FACE;
    public static final Identifier MUSIC_DISC_DROOPY_LIKES_YOUR_FACE_ID = new Identifier(Main.MOD_ID + ":music_disc.droopy_likes_your_face");
    public static final SoundEvent MUSIC_DISC_DROOPY_LIKES_YOUR_FACE_EVENT = SoundEvent.of(MUSIC_DISC_DROOPY_LIKES_YOUR_FACE_ID);
    public static SoundEvent MUSIC_DISC_DOG;
    public static final Identifier MUSIC_DISC_DOG_ID = new Identifier(Main.MOD_ID + ":music_disc.dog");
    public static final SoundEvent MUSIC_DISC_DOG_EVENT = SoundEvent.of(MUSIC_DISC_DOG_ID);
    public static SoundEvent MUSIC_DISC_DEATH;
    public static final Identifier MUSIC_DISC_DEATH_ID = new Identifier(Main.MOD_ID + ":music_disc.death");
    public static final SoundEvent MUSIC_DISC_DEATH_EVENT = SoundEvent.of(MUSIC_DISC_DEATH_ID);

    private static SoundEvent register(SoundEvent event, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, soundId, event);
    }

    public static void registerSoundEvents() {
        MUSIC_DISC_INTRO = register(MUSIC_DISC_INTRO_EVENT, MUSIC_DISC_INTRO_ID);
        MUSIC_DISC_DROOPY_LIKES_RICOCHET = register(MUSIC_DISC_DROOPY_LIKES_RICOCHET_EVENT, MUSIC_DISC_DROOPY_LIKES_RICOCHET_ID);
        MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register(MUSIC_DISC_DROOPY_LIKES_YOUR_FACE_EVENT, MUSIC_DISC_DROOPY_LIKES_YOUR_FACE_ID);
        MUSIC_DISC_DOG = register(MUSIC_DISC_DOG_EVENT, MUSIC_DISC_DOG_ID);
        MUSIC_DISC_DEATH = register(MUSIC_DISC_DEATH_EVENT, MUSIC_DISC_DEATH_ID);
    }

}
