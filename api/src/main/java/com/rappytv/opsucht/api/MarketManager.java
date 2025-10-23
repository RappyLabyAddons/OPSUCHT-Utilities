package com.rappytv.opsucht.api;

import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface MarketManager {

    @Nullable
    MarketItem getPrice(String itemId);

    void cachePrices();

}
