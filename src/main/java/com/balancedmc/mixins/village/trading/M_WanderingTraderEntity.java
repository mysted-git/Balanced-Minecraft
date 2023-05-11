package com.balancedmc.mixins.village.trading;

import com.balancedmc.villagers.VillagerHelper;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(WanderingTraderEntity.class)
public abstract class M_WanderingTraderEntity {

    /**
     * @author HB0P
     * @reason Custom random wandering trader trades
     */

    @Overwrite
    public void fillRecipes() {
        WanderingTraderEntity trader = (WanderingTraderEntity) (Object) this;
        TradeOfferList offerList = trader.getOffers();

        TradeOffer[] trades = VillagerHelper.generateTrades(trader, 6);
        offerList.addAll(List.of(trades));
    }
}
