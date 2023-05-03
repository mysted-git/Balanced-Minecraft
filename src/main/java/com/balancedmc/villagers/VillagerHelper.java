package com.balancedmc.villagers;

import com.balancedmc.Main;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.*;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.gen.structure.Structure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility classes to generate random villager trades
 */
public class VillagerHelper {

    static ArrayList<TradeItem> buyItems;
    static ArrayList<TradeItem> sellItems;
    static final int tolerance = 5;
    static final HashMap<VillagerProfession, ArrayList<Conversion>> conversions = new HashMap<>();
    static final HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    static HashMap<String, Integer> potions;
    static final HashMap<String, String[]> potionVariants = new HashMap<>();
    static final HashMap<StatusEffect, Integer> suspiciousStewEffects = new HashMap<>();
    static TradeItem emeraldItem;

    /**
     * Generate all trades for a villager
     */
    public static TradeOffer[] generateTrades(VillagerEntity villager, int count) {
        int level = villager.getVillagerData().getLevel();
        VillagerProfession profession = villager.getVillagerData().getProfession();
        TradeOffer[] trades = new TradeOffer[level == 5 || level == 1 ? count + 1 : count];
        if (level == 1) {
            trades[count] = null;
            while (trades[count] == null) {
                trades[count] = generateTrade(villager, emeraldItem);
            }
        }
        for (int i = 0; i < count;) {
            TradeOffer trade = generateTrade(villager);
            if (trade != null) {
                trades[i] = trade;
                i++;
            }
        }
        if (level == 5) {
            trades[count] = generateConversion(profession);
        }
        return trades;
    }

    /**
     * Get all items linked to a profession
     */
    private static ArrayList<TradeItem> getProfessionItems(VillagerProfession profession, ArrayList<TradeItem> items) {
        ArrayList<TradeItem> result = new ArrayList<>();
        for (TradeItem item : items) {
            if (item.professions.contains(profession) || item.professions.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Generate a normal trade
     */
    private static TradeOffer generateTrade(VillagerEntity villager) {
        // match items to profession for buying or selling
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Slot match = Slot.random();
        ArrayList<TradeItem> buys1 = match == Slot.BUY1 ? getProfessionItems(profession, buyItems)  : buyItems;
        ArrayList<TradeItem> buys2 = match == Slot.BUY2 ? getProfessionItems(profession, buyItems)  : buyItems;
        ArrayList<TradeItem> sells = match == Slot.SELL ? getProfessionItems(profession, sellItems) : sellItems;

        return generateTrade(villager, buys1, buys2, sells, match);
    }

    /**
     * Generate a trade with a specific sell item
     */
    private static TradeOffer generateTrade(VillagerEntity villager, TradeItem forceSell) {
        // match items to profession for buying or selling
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Slot match = Slot.randomBuy();
        ArrayList<TradeItem> buys1 = match == Slot.BUY1 ? getProfessionItems(profession, buyItems)  : buyItems;
        ArrayList<TradeItem> buys2 = match == Slot.BUY2 ? getProfessionItems(profession, buyItems)  : buyItems;

        ArrayList<TradeItem> sells = new ArrayList<>();
        sells.add(forceSell);

        return generateTrade(villager, buys1, buys2, sells, match);
    }

    private static TradeOffer generateTrade(Entity entity, ArrayList<TradeItem> buys1, ArrayList<TradeItem> buys2, ArrayList<TradeItem> sells, Slot match) {
        // generate random trades
        Collections.shuffle(buys1);
        Collections.shuffle(buys2);
        Collections.shuffle(sells);

        // get sell item
        TradeItem sellItem;
        try {
            sellItem = sells.get(0);
        }
        catch (IndexOutOfBoundsException e) {
            Main.LOGGER.error("generateTrade() forced to generate dummy trade");
            return new TradeOffer(new ItemStack(Items.AIR), new ItemStack(Items.AIR), 0, 0, 0);
        }

        Pair<ItemStack, Integer> pair = sellItem.getItemStack(entity);
        ItemStack sellStack = pair.getLeft();
        int sellPrice = pair.getRight();

        // get first buy item
        TradeItem buyItem1 = null;
        int buyCount1 = 0;
        int buyPrice1 = 0;
        for (TradeItem item : buys1) {
            int count = sellPrice / item.price;
            if (count == 0) continue;
            if (count > item.getMaxCount()) count = item.getMaxCount();
            buyItem1 = item;
            buyCount1 = count;
            buyPrice1 = item.price * count;
            break;
        }
        if (buyItem1 == null) return null;

        // get second buy item
        TradeItem buyItem2 = null;
        int buyCount2 = 0;
        for (TradeItem item : buys2) {
            int count = (sellPrice - buyPrice1) / item.price;
            if (count > item.getMaxCount()) count = item.getMaxCount();
            int price = item.price * count;
            if (buyPrice1 + price >= sellPrice - tolerance) {
                buyItem2 = item;
                buyCount2 = count;
                break;
            }
            count++;
            if (count > item.getMaxCount()) continue;
            price = item.price * count;
            if (buyPrice1 + price <= sellPrice + tolerance) {
                buyItem2 = item;
                buyCount2 = count;
                break;
            }
        }
        if (buyItem2 == null) return null;
        if (buyCount2 == 0 && match == Slot.BUY2) return null;

        // swap buy items with random chance
        if (buyCount2 > 0 && Math.random() < 0.5) {
            TradeItem temp = buyItem1;
            buyItem1 = buyItem2;
            buyItem2 = temp;
            int t = buyCount1;
            buyCount1 = buyCount2;
            buyCount2 = t;
        }

        // create trade offer
        return new TradeOffer(
                buyItem1.getItemStack(buyCount1),
                buyItem2.getItemStack(buyCount2),
                sellStack,
                10, 10, 0.05F
        );
    }

    /**
     * Generate a conversion trade
     */
    private static TradeOffer generateConversion(VillagerProfession profession) {
        Conversion conversion = conversions.get(profession).get((int) (Math.random() * conversions.get(profession).size()));
        return new TradeOffer(
                conversion.buyItem,
                new ItemStack(Items.EMERALD, conversion.price),
                conversion.sellItem,
                Integer.MAX_VALUE, 0, 0.05F
        );
    }

    public static void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(Main.MOD_ID, "");
            }

            @Override
            public void reload(ResourceManager manager) {
                Gson gson = new Gson();
                // trades.json
                try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/trades.json")).get().getInputStream()) {
                    ItemInfo[] itemInfo = gson.fromJson(new InputStreamReader(stream), ItemInfo[].class);
                    VillagerHelper.buyItems = new ArrayList<>();
                    VillagerHelper.sellItems = new ArrayList<>();
                    for (ItemInfo info : itemInfo) {
                        TradeItem item = new TradeItem(info.items, info.price, info.professions, info.buy, info.sell);
                        if (item.canBuy) buyItems.add(item);
                        if (item.canSell) sellItems.add(item);
                        if (item.isEmerald()) emeraldItem = item;
                    }
                    if (emeraldItem == null) {
                        throw new RuntimeException("No emerald item found in trades.json");
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to read trades.json", e);
                }
                // conversions.json
                try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/conversions.json")).get().getInputStream()) {
                    ConversionInfo[] conversionInfo = gson.fromJson(new InputStreamReader(stream), ConversionInfo[].class);
                    for (ConversionInfo info : conversionInfo) {
                        VillagerProfession profession = Registries.VILLAGER_PROFESSION.get(new Identifier(info.profession));
                        Conversion conversion = new Conversion(info.buy, info.sell, info.price);
                        if (!conversions.containsKey(profession)) {
                            conversions.put(profession, new ArrayList<>());
                        }
                        conversions.get(profession).add(conversion);
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to read conversions.json", e);
                }
                // enchantments.json
                try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/enchantments.json")).get().getInputStream()) {
                    HashMap<String, Integer> enchantmentInfo = gson.fromJson(new InputStreamReader(stream), new TypeToken<HashMap<String, Integer>>(){}.getType());
                    for (String name : enchantmentInfo.keySet()) {
                        Enchantment enchantment = Registries.ENCHANTMENT.get(new Identifier(name));
                        enchantments.put(enchantment, enchantmentInfo.get(name));
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to read enchantments.json", e);
                }
                // potions.json
                try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/potions.json")).get().getInputStream()) {
                    potions = gson.fromJson(new InputStreamReader(stream), new TypeToken<HashMap<String, Integer>>(){}.getType());
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to read potions.json", e);
                }
            }
        });
    }

    static {
        String[] strong = new String[]{"strong_"};
        String[] length = new String[]{"long_"};
        String[] both = new String[]{"strong_", "long_"};
        potionVariants.put("night_vision", length);
        potionVariants.put("invisibility", length);
        potionVariants.put("leaping", both);
        potionVariants.put("fire_resistance", both);
        potionVariants.put("swiftness", both);
        potionVariants.put("slowness", both);
        potionVariants.put("water_breathing", length);
        potionVariants.put("healing", strong);
        potionVariants.put("harming", strong);
        potionVariants.put("poison", both);
        potionVariants.put("regeneration", both);
        potionVariants.put("strength", both);
        potionVariants.put("weakness", length);
        potionVariants.put("luck", new String[]{});
        potionVariants.put("turtle_master", both);
        potionVariants.put("slow_falling", length);

        suspiciousStewEffects.put(StatusEffects.NIGHT_VISION, 100);
        suspiciousStewEffects.put(StatusEffects.JUMP_BOOST, 160);
        suspiciousStewEffects.put(StatusEffects.WEAKNESS, 140);
        suspiciousStewEffects.put(StatusEffects.BLINDNESS, 120);
        suspiciousStewEffects.put(StatusEffects.POISON, 280);
        suspiciousStewEffects.put(StatusEffects.SATURATION, 7);
    }
}

class TradeItem {
    private final ArrayList<Item> items = new ArrayList<>();
    final int price;
    final ArrayList<VillagerProfession> professions = new ArrayList<>();
    final boolean canBuy;
    final boolean canSell;

    TradeItem(String[] items, int price, String[] professions, boolean canBuy, boolean canSell) {
        for (String item : items) {
            this.items.add(Registries.ITEM.get(new Identifier(item)));
        }
        this.price = price;
        for (String prof : professions) {
            if (prof.equals("ANY")) break;
            this.professions.add(Registries.VILLAGER_PROFESSION.get(new Identifier(prof)));
        }
        this.canBuy = canBuy;
        this.canSell = canSell;
    }

    int getMaxCount() {
        return items.get(0).getMaxCount();
    }

    /**
     * Get a single item to sell
     * <p>
     * Includes special items such as:<br>
     * Enchantable items<br>
     * Potions and tipped arrows<br>
     * Suspicious stew<br>
     * Explorer maps<br>
     */
    Pair<ItemStack, Integer /*price*/> getItemStack(Entity entity) {
        Item item = items.get((int) (Math.random() * items.size()));
        int count = (int) (Math.random() * getMaxCount()) + 1;
        ItemStack itemStack = new ItemStack(item, count);
        int priceBonus = 0;

        // enchantments
        if (itemStack.isEnchantable()) {
            List<Enchantment> enchantments = Arrays.asList(VillagerHelper.enchantments.keySet().toArray(new Enchantment[0]));
            Collections.shuffle(enchantments);
            for (Enchantment enchantment : enchantments) {
                if (enchantment.isAcceptableItem(itemStack)) {
                    int level = (int) (Math.random() * (enchantment.getMaxLevel() + 1));
                    if (level == 0) break;
                    itemStack.addEnchantment(enchantment, level);
                    priceBonus = VillagerHelper.enchantments.get(enchantment) * level;
                    break;
                }
            }
        }

        // potion effects
        else if (item instanceof PotionItem || item == Items.TIPPED_ARROW) {
            List<String> names = VillagerHelper.potions.keySet().stream().toList();
            String name = names.get((int) (Math.random() * names.size()));
            ArrayList<String> prefixes = new ArrayList<>(List.of(VillagerHelper.potionVariants.get(name)));
            prefixes.add("");
            String prefix = prefixes.get((int) (Math.random() * prefixes.size()));
            priceBonus = VillagerHelper.potions.get(name) * (prefix.isEmpty() ? 1 : 2);
            PotionUtil.setPotion(itemStack, Registries.POTION.get(new Identifier(prefix + name)));
        }

        // suspicious stew
        else if (item == Items.SUSPICIOUS_STEW) {
            List<StatusEffect> effects = VillagerHelper.suspiciousStewEffects.keySet().stream().toList();
            StatusEffect effect = effects.get((int) (Math.random() * effects.size()));
            SuspiciousStewItem.addEffectToStew(itemStack, effect, VillagerHelper.suspiciousStewEffects.get(effect));
        }

        // explorer maps
        else if (item == Items.FILLED_MAP) {
            boolean bool = Math.random() < 0.5;
            TagKey<Structure> structure = bool ? StructureTags.ON_OCEAN_EXPLORER_MAPS : StructureTags.ON_WOODLAND_EXPLORER_MAPS;
            String nameKey = bool ? "filled_map.monument" : "filled_map.mansion";
            MapIcon.Type iconType = bool ? MapIcon.Type.MONUMENT : MapIcon.Type.MANSION;
            if (entity.world instanceof ServerWorld serverWorld) {
                BlockPos blockPos = serverWorld.locateStructure(structure, entity.getBlockPos(), 100, true);
                if (blockPos != null) {
                    itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
                    FilledMapItem.fillExplorationMap(serverWorld, itemStack);
                    MapState.addDecorationsNbt(itemStack, blockPos, "+", iconType);
                    itemStack.setCustomName(Text.translatable(nameKey));
                }
            }
            count = 1;
        }

        return new Pair<>(itemStack, (price + priceBonus) * count);
    }

    /**
     * Get a stack of items to buy
     */
    ItemStack getItemStack(int count) {
        Item item = items.get((int) (Math.random() * items.size()));
        return new ItemStack(item, count);
    }

    boolean isEmerald() {
        return items.contains(Items.EMERALD);
    }
}

class Conversion {

    ItemStack buyItem;
    ItemStack sellItem;
    int price;

    Conversion(ConversionItemInfo buy, ConversionItemInfo sell, int price) {
        this.buyItem = new ItemStack(Registries.ITEM.get(new Identifier(buy.item)), buy.count);
        this.sellItem = new ItemStack(Registries.ITEM.get(new Identifier(sell.item)), sell.count);
        this.price = price;
    }
}

enum Slot {
    BUY1, BUY2, SELL;

    static Slot random() {
        int r = (int) (Math.random() * 4);
        if (r == 3) return SELL;
        return values()[r];
    }

    static Slot randomBuy() {
        return values()[(int)(Math.random() * 2)];
    }
}

/**
 * Intermediate classes for json parsing
 */
class ItemInfo { String[] items; int price; String[] professions; boolean buy; boolean sell; }

class ConversionInfo { String profession; ConversionItemInfo buy; int price; ConversionItemInfo sell; }

class ConversionItemInfo { String item ; int count; }
