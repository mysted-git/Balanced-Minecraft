package com.balancedmc.villagers;

import com.balancedmc.Main;
import com.balancedmc.structure_tags.ModStructureTags;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.gen.structure.Structure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility classes to generate random villager trades<p>
 * The following methods generate all trades for a
 * {@link #generateTrades(VillagerEntity, int) villager} or
 * {@link #generateTrades(WanderingTraderEntity, int) wandering trader}<p>
 * The following methods generate a single trade for a villager with
 * {@link #generateTrade(VillagerEntity) normal generation},
 * {@link #generateTrade(VillagerEntity, TradeItem) a specific sell item}, or
 * {@link #generateConversion(VillagerEntity) a conversion}<p>
 * {@link #generateTrade(WanderingTraderEntity) This method} generates a single trade for a wandering trader<p>
 * The main logic is in the method which {@link #generateTrade(Entity, ArrayList, ArrayList, ArrayList, Slot) generates a trade for any merchant}
 */
public class VillagerHelper {

    static ArrayList<TradeItem> buyItems;
    static ArrayList<TradeItem> sellItems;
    static final int tolerance = 5;
    static final HashMap<VillagerProfession, ArrayList<Conversion>> conversions = new HashMap<>();
    static final HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    static HashMap<String, Integer> potions;
    static final HashMap<String, String[]> potionVariants = new HashMap<>();
    static TradeItem emeraldItem;

    /**
     * Generate all trades<br>
     * For VILLAGER
     */
    public static TradeOffer[] generateTrades(VillagerEntity villager, int count) {
        int level = villager.getVillagerData().getLevel();
        TradeOffer[] trades = new TradeOffer[level == 5 || level == 1 ? count + 1 : count];
        // standard trades
        for (int i = 0; i < count;) {
            TradeOffer trade = generateTrade(villager);
            if (trade != null) {
                trades[level == 1 ? i + 1 : i] = trade;
                i++;
            }
        }
        // sell emerald trade for novice villagers
        if (level == 1) {
            trades[0] = null;
            while (trades[0] == null) {
                trades[0] = generateTrade(villager, emeraldItem);
            }
        }
        // conversion trade for master villagers
        if (level == 5) {
            trades[count] = generateConversion(villager);
        }
        return trades;
    }

    /**
     * Generate all trades<br>
     * For WANDERING TRADER
     */
    public static TradeOffer[] generateTrades(WanderingTraderEntity trader, int count) {
        TradeOffer[] trades = new TradeOffer[count];
        for (int i = 0; i < count;) {
            TradeOffer trade = generateTrade(trader);
            if (trade != null) {
                trades[i] = trade;
                i++;
            }
        }
        return trades;
    }

    /**
     * UTILITY<br>
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
     * Generate a trade<br>
     * For VILLAGER<br>
     * Normal generation
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
     * Generate a trade<br>
     * For VILLAGER<br>
     * Specific sell item
     */
    private static TradeOffer generateTrade(VillagerEntity villager, TradeItem forceSell) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Slot match = Slot.randomBuy();
        ArrayList<TradeItem> buys1 = match == Slot.BUY1 ? getProfessionItems(profession, buyItems) : buyItems;
        ArrayList<TradeItem> buys2 = match == Slot.BUY2 ? getProfessionItems(profession, buyItems) : buyItems;

        ArrayList<TradeItem> sells = new ArrayList<>();
        sells.add(forceSell);

        return generateTrade(villager, buys1, buys2, sells, match);
    }

    /**
     * Generate a trade<br>
     * For WANDERING TRADER<br>
     * Normal generation
     */
    private static TradeOffer generateTrade(WanderingTraderEntity trader) {
        Slot emeraldSlot = Slot.randomIO();
        ArrayList<TradeItem> emerald = new ArrayList<>();
        emerald.add(emeraldItem);
        ArrayList<TradeItem> buys1 = emeraldSlot == Slot.BUY1 ? emerald : buyItems;
        ArrayList<TradeItem> buys2 = new ArrayList<>();
        ArrayList<TradeItem> sells = emeraldSlot == Slot.SELL ? emerald : sellItems;

        return generateTrade(trader, buys1, buys2, sells, null);
    }

    /**
     * Generate a trade<br>
     * For ANY<br>
     * From item lists
     */
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

        SellItemContainer.SellItemResult sellItemResult = sellItem.getSellItemStack(entity);
        ItemStack sellStack = sellItemResult.itemStack;
        int sellPrice = sellItemResult.price;

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
        if (buyItem1 == null) {
            Main.LOGGER.warn("Failed to generate trade with " + sellStack + " (1)");
            return null;
        }

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
        if (buyItem2 == null && !buys2.isEmpty()) {
            Main.LOGGER.warn("Failed to generate trade with " + sellStack + " (2)");
            return null;
        }
        if (buyCount2 == 0 && match == Slot.BUY2) {
            Main.LOGGER.warn("Failed to generate trade with " + sellStack + " (3)");
            return null;
        }

        // create trade offer
        int maxUses = 12 + (int) (Math.random() * 5); // 12 to 16
        final int experience = Math.min(sellPrice / 2, 30);
        final float multiplier = 0.05F;
        if (buyItem2 == null || buyCount2 == 0) {
            // divide by HCF
            int hcf = VillagerMath.hcf(buyCount1, sellStack.getCount());
            if (hcf != 1) {
                buyCount1 /= hcf;
                sellStack.setCount(sellStack.getCount() / hcf);
                maxUses *= hcf;
            }
            // confirm buy and sell are not the same
            ItemStack buyStack1 = buyItem1.getBuyItemStack(buyCount1, sellStack.getItem());
            if (buyStack1 == null) {
                Main.LOGGER.warn("Failed to generate trade with " + sellStack + " (4)");
                return null;
            }
            // return
            return new TradeOffer(
                    buyStack1,
                    sellStack,
                    maxUses, experience, multiplier
            );
        }
        else {
            // swap buy items with random chance
            if (buyItem1 != buyItem2 && Math.random() < 0.5) {
                TradeItem temp = buyItem1;
                buyItem1 = buyItem2;
                buyItem2 = temp;
                int t = buyCount1;
                buyCount1 = buyCount2;
                buyCount2 = t;
            }
            // divide by HCF
            int hcf = VillagerMath.hcf(buyCount1, buyCount2, sellStack.getCount());
            if (hcf != 1) {
                buyCount1 /= hcf;
                buyCount2 /= hcf;
                sellStack.setCount(sellStack.getCount() / hcf);
                maxUses *= hcf;
            }
            // confirm buy and sell are not the same
            ItemStack buyStack1 = buyItem1.getBuyItemStack(buyCount1, sellStack.getItem());
            ItemStack buyStack2 = buyItem2.getBuyItemStack(buyCount2, sellStack.getItem());
            if (buyStack1 == null || buyStack2 == null) {
                Main.LOGGER.warn("Failed to generate trade with " + sellStack + " (5)");
                return null;
            }
            // merge buy items if they are the same
            if (buyStack1.getItem() == buyStack2.getItem() && buyStack1.getCount() + buyStack2.getCount() <= 64) {
                buyStack1.setCount(buyStack1.getCount() + buyStack2.getCount());
                return new TradeOffer(
                        buyStack1,
                        sellStack,
                        maxUses, experience, multiplier
                );
            }
            // return
            return new TradeOffer(
                    buyStack1,
                    buyStack2,
                    sellStack,
                    maxUses, experience, multiplier
            );
        }
    }

    /**
     * Generate a trade<br>
     * For VILLAGER<br>
     * Conversion
     */
    private static TradeOffer generateConversion(VillagerEntity villager) {
        VillagerProfession profession = villager.getVillagerData().getProfession();
        Conversion conversion = conversions.get(profession).get((int) (Math.random() * conversions.get(profession).size()));
        return new TradeOffer(
                conversion.getBuyItemStack(),
                new ItemStack(Items.EMERALD, conversion.price),
                conversion.getSellItemStack(villager),
                Integer.MAX_VALUE, 0, 0
        );
    }

    public static void registerReloadListener() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ReloadListener());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ReloadListener());
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
    }
}

abstract class SellItemContainer {

    int price;

    protected SellItemResult applyEffectsToSellItem(Item item, int count, Entity entity) {
        int priceBonus = 0;
        ItemStack itemStack = new ItemStack(item, count);

        // enchantments
        if (itemStack.isEnchantable() || itemStack.isOf(Items.ENCHANTED_BOOK)) {
            List<Enchantment> enchantments = Arrays.asList(VillagerHelper.enchantments.keySet().toArray(new Enchantment[0]));
            Collections.shuffle(enchantments);
            for (Enchantment enchantment : enchantments) {
                if (enchantment.isAcceptableItem(itemStack) || itemStack.isOf(Items.ENCHANTED_BOOK)) {
                    int level;
                    if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
                        level = (int) ((Math.random() * enchantment.getMaxLevel()) + 1);
                        itemStack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
                    }
                    else {
                        level = (int) (Math.random() * (enchantment.getMaxLevel() + 1));
                        if (level == 0) break;
                        itemStack.addEnchantment(enchantment, level);
                    }
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
            List<SuspiciousStewIngredient> ingredients = SuspiciousStewIngredient.getAll();
            SuspiciousStewIngredient ingredient = ingredients.get((int) (Math.random() * ingredients.size()));
            SuspiciousStewItem.addEffectToStew(itemStack, ingredient.getEffectInStew(), ingredient.getEffectInStewDuration());
        }

        // explorer maps
        else if (item == Items.FILLED_MAP) {
            int n = (int) (Math.random() * 3);
            TagKey<Structure> structure = n == 0 ? StructureTags.ON_OCEAN_EXPLORER_MAPS : n == 1 ? StructureTags.ON_WOODLAND_EXPLORER_MAPS : ModStructureTags.ON_DEEP_DARK_EXPLORER_MAPS;
            String nameKey = n == 0 ? "filled_map.monument" : n == 1 ? "filled_map.mansion" : "filled_map.ancient_city";
            MapIcon.Type iconType = n == 0 ? MapIcon.Type.MONUMENT : n == 1 ? MapIcon.Type.MANSION : MapIcon.Type.BANNER_MAGENTA;
            if (entity.getWorld() instanceof ServerWorld serverWorld) {
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

        return new SellItemResult((price + priceBonus) * count, itemStack);
    }

    protected static class SellItemResult {
        int price;
        final ItemStack itemStack;

        private SellItemResult(int price, ItemStack itemStack) {
            this.price = price;
            this.itemStack = itemStack;
        }

        protected void applyMultiplierToPrice(float multiplier) {
            price *= multiplier;
        }
    }
}

class TradeItem extends SellItemContainer {
    private final ArrayList<Item> items = new ArrayList<>();
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
     * Get a stack of items to buy
     * @param count The number of items in the stack
     * @param banned An item which cannot be returned
     */
    ItemStack getBuyItemStack(int count, Item banned) {
        List<Item> validItems = items.stream().filter((i) -> i != banned).toList();
        if (validItems.isEmpty()) return null;
        Item item = validItems.get((int) (Math.random() * validItems.size()));
        return new ItemStack(item, count);
    }

    /**
     * Get a single item to sell
     * <p>
     * Includes special items such as:
     * <p>
     * Enchantable items<br>
     * Potions and tipped arrows<br>
     * Suspicious stew<br>
     * Explorer maps<br>
     */
    SellItemResult getSellItemStack(Entity entity) {
        Item item = items.get((int) (Math.random() * items.size()));
        int count = (int) (Math.pow(Math.random(), 2) * getMaxCount()) + 1; // favours lower counts

        SellItemResult result = applyEffectsToSellItem(item, count, entity);

        // wandering traders have better deals
        float priceMultiplier = 1;
        if (entity instanceof WanderingTraderEntity) {
            priceMultiplier = 0.8f;
        }
        result.applyMultiplierToPrice(priceMultiplier);

        return result;
    }

    boolean isEmerald() {
        return items.contains(Items.EMERALD);
    }
}

class Conversion extends SellItemContainer {

    private final Item buyItem;
    private final Item sellItem;
    private final int buyCount;
    private final int sellCount;

    Conversion(ConversionItemInfo buy, ConversionItemInfo sell, int price) {
        this.buyItem = Registries.ITEM.get(new Identifier(buy.item));
        this.sellItem = Registries.ITEM.get(new Identifier(sell.item));
        this.buyCount = buy.count;
        this.sellCount = sell.count;
        this.price = price;
    }

    ItemStack getBuyItemStack() {
        return new ItemStack(buyItem, buyCount);
    }

    ItemStack getSellItemStack(Entity entity) {
        return applyEffectsToSellItem(sellItem, sellCount, entity).itemStack;
    }
}

enum Slot {
    BUY1, BUY2, SELL;

    // return BUY1, BUY2, SELL, SELL
    static Slot random() {
        int r = (int) (Math.random() * 4);
        if (r == 3) return SELL;
        return values()[r];
    }

    // return BUY1, BUY2
    static Slot randomBuy() {
        return values()[(int)(Math.random() * 2)];
    }

    // return BUY1, SELL
    static Slot randomIO() {
        return values()[((int) (Math.random() * 2)) * 2];
    }
}

/**
 * Intermediate classes for json parsing
 */
class ItemInfo { String[] items; int price; int weight; String[] professions; boolean buy; boolean sell; }

class ConversionInfo { String profession; ConversionItemInfo buy; int price; ConversionItemInfo sell; }

class ConversionItemInfo { String item ; int count; }

class ReloadListener implements SimpleSynchronousResourceReloadListener {
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
                for (int i = 0; i < info.weight; i++) {
                    if (item.canBuy) VillagerHelper.buyItems.add(item);
                    if (item.canSell) VillagerHelper.sellItems.add(item);
                }
                if (item.isEmerald()) VillagerHelper.emeraldItem = item;
            }
            if (VillagerHelper.emeraldItem == null) {
                throw new RuntimeException("No emerald item found in trades.json");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read trades.json", e);
        }
        // conversions.json
        try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/conversions.json")).get().getInputStream()) {
            ConversionInfo[] conversionInfo = gson.fromJson(new InputStreamReader(stream), ConversionInfo[].class);
            for (ConversionInfo info : conversionInfo) {
                VillagerProfession profession = Registries.VILLAGER_PROFESSION.get(new Identifier(info.profession));
                Conversion conversion = new Conversion(info.buy, info.sell, info.price);
                if (!VillagerHelper.conversions.containsKey(profession)) {
                    VillagerHelper.conversions.put(profession, new ArrayList<>());
                }
                VillagerHelper.conversions.get(profession).add(conversion);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read conversions.json", e);
        }
        // enchantments.json
        try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/enchantments.json")).get().getInputStream()) {
            HashMap<String, Integer> enchantmentInfo = gson.fromJson(new InputStreamReader(stream), new TypeToken<HashMap<String, Integer>>() {
            }.getType());
            for (String name : enchantmentInfo.keySet()) {
                Enchantment enchantment = Registries.ENCHANTMENT.get(new Identifier(name));
                if (enchantment == null) {
                    Main.LOGGER.error("Invalid enchantment " + name);
                    continue;
                }
                VillagerHelper.enchantments.put(enchantment, enchantmentInfo.get(name));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read enchantments.json", e);
        }
        // potions.json
        try (InputStream stream = manager.getResource(new Identifier(Main.MOD_ID, "villagers/potions.json")).get().getInputStream()) {
            VillagerHelper.potions = gson.fromJson(new InputStreamReader(stream), new TypeToken<HashMap<String, Integer>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read potions.json", e);
        }
    }
}

/**
 * Utility maths class
 */
class VillagerMath {

    /**
     * Find the highest common factor
     */
    public static int hcf(int a, int b) {
        int result = Math.min(a, b);
        while (result > 0) {
            if (a % result == 0 && b % result == 0) {
                break;
            }
            result--;
        }
        return result;
    }

    public static int hcf(int a, int b, int c) {
        return hcf(hcf(a, b), c);
    }
}