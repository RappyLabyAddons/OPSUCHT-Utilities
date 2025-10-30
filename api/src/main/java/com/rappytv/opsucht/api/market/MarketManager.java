package com.rappytv.opsucht.api.market;

import java.util.function.Consumer;
import net.labymod.api.client.entity.player.Inventory;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface MarketManager {

    @Nullable
    MarketItem getItem(String itemId);

    void calculateInventoryValue(@NotNull Inventory inventory, boolean includeStack, @NotNull Consumer<@Nullable InventoryValueData> consumer);

    void cachePrices();

    enum DisplayMode {
        BOTH,
        ONLY_BUY,
        ONLY_SELL
    }
}
