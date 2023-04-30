package com.balancedmc.mixins.client.village;

import com.balancedmc.villagers.VillagerHelper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(VillagerEntity.class)
public abstract class M_VillagerEntity {

    /**
     * @author HB0P
     * @reason Custom random villager trades
     */

    @Overwrite
    public void fillRecipes() {
        VillagerEntity villager = (VillagerEntity) (Object) this;
        TradeOfferList offerList = villager.getOffers();
        VillagerProfession profession = villager.getVillagerData().getProfession();

        if (profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT) {
            TradeOffer[] trades = VillagerHelper.generateTrades(villager, 2);
            offerList.addAll(List.of(trades));
        }
    }
}
