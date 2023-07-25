package com.balancedmc.items;

import com.balancedmc.Main;
import com.balancedmc.sounds.ModSoundEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class ModItems {

    public static void registerItems() {}

    public static final Item MUSIC_DISC_INTRO;
    public static final Item MUSIC_DISC_DROOPY_LIKES_RICOCHET;
    public static final Item MUSIC_DISC_DROOPY_LIKES_YOUR_FACE;
    public static final Item MUSIC_DISC_DOG;
    public static final Item MUSIC_DISC_DEATH;
    public static final Item MUSIC_DISC_KEY;
    public static final Item MUSIC_DISC_DOOR;
    public static final Item MINGLING_POTION_ITEM;
    public static final Item ILLUSIONER_SPAWN_EGG;
    public static final Item TOOL_TRIM_SMITHING_TEMPLATE;

    static {
        MUSIC_DISC_INTRO = register("music_disc_intro", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_INTRO, 276));
        MUSIC_DISC_DROOPY_LIKES_RICOCHET = register("music_disc_droopy_likes_ricochet", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_RICOCHET, 95));
        MUSIC_DISC_DROOPY_LIKES_YOUR_FACE = register("music_disc_droopy_likes_your_face", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DROOPY_LIKES_YOUR_FACE, 117));
        MUSIC_DISC_DOG = register("music_disc_dog", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOG, 147));
        MUSIC_DISC_DEATH = register("music_disc_death", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DEATH, 42));
        MUSIC_DISC_KEY = register("music_disc_key", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_KEY, 65));
        MUSIC_DISC_DOOR = register("music_disc_door", new CustomMusicDiscItem(ModSoundEvents.MUSIC_DISC_DOOR, 108));
        MINGLING_POTION_ITEM = register("potion_mingling", new PotionItem(new Item.Settings().maxCount(16)));
        ILLUSIONER_SPAWN_EGG = register("illusioner_spawn_egg", new SpawnEggItem(EntityType.ILLUSIONER, 1706419, 13139693, new Item.Settings()));

        TOOL_TRIM_SMITHING_TEMPLATE = register("tool_trim_smithing_template", new SmithingTemplateItem(
                Text.translatable(Util.createTranslationKey("item", new Identifier(Main.MOD_ID, "smithing_template.tool_trim.applies_to"))).formatted(Formatting.BLUE),
                Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.armor_trim.ingredients"))).formatted(Formatting.BLUE),
                Text.translatable(Util.createTranslationKey("trim_pattern", new Identifier(Main.MOD_ID, "tool"))).formatted(Formatting.GRAY),
                Text.translatable(Util.createTranslationKey("item", new Identifier(Main.MOD_ID, "smithing_template.tool_trim.base_slot_description"))),
                Text.translatable(Util.createTranslationKey("item", new Identifier("smithing_template.armor_trim.additions_slot_description"))),
                List.of(new Identifier("item/empty_slot_hoe"), new Identifier("item/empty_slot_axe"), new Identifier("item/empty_slot_sword"), new Identifier("item/empty_slot_shovel"), new Identifier("item/empty_slot_pickaxe")),
                List.of(new Identifier("item/empty_slot_ingot"), new Identifier("item/empty_slot_redstone_dust"), new Identifier("item/empty_slot_quartz"), new Identifier("item/empty_slot_emerald"), new Identifier("item/empty_slot_diamond"), new Identifier("item/empty_slot_lapis_lazuli"), new Identifier("item/empty_slot_amethyst_shard"))
        ));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> content.add(ILLUSIONER_SPAWN_EGG));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> content.add(TOOL_TRIM_SMITHING_TEMPLATE));
    }

    private static Item register(String name, Item item) {
        Item registeredItem = Registry.register(Registries.ITEM, new Identifier(Main.MOD_ID, name), item);
        if (item instanceof MusicDiscItem) {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(registeredItem));
        } else if (item == MINGLING_POTION_ITEM) {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(registeredItem));
        }
        return registeredItem;
    }

}
