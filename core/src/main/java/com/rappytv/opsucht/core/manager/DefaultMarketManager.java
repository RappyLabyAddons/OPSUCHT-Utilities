package com.rappytv.opsucht.core.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rappytv.opsucht.api.market.IPriceHudWidgetConfig;
import com.rappytv.opsucht.api.market.InventoryValueData;
import com.rappytv.opsucht.api.market.MarketItem;
import com.rappytv.opsucht.api.market.MarketManager;
import com.rappytv.opsucht.api.market.MarketStack;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Singleton;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.entity.player.Inventory;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.models.Implements;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
@Implements(MarketManager.class)
public class DefaultMarketManager implements MarketManager {

    private static final String ENDPOINT = "https://api.opsucht.net/market/prices";

    private final Map<String, MarketItem> items = new HashMap<>();

    @Override
    public @Nullable MarketItem getItem(String itemId) {
        return this.items.get(itemId.toLowerCase());
    }

    @Override
    public @NotNull Component formatValueComponent(
        float buyValue,
        float sellValue,
        @NotNull IPriceHudWidgetConfig config
    ) {
        String format = config.priceFormat().get();
        Component buyComponent = Component.text(format.replace(
            "{price}", this.formatFloat(buyValue)
        ), TextColor.color(config.buyPriceColor().get().get()));
        Component sellComponent = Component.text(format.replace(
            "{price}", this.formatFloat(sellValue)
        ), TextColor.color(config.sellPriceColor().get().get()));

        return switch (config.displayMode().get()) {
            case BOTH -> Component.empty()
                .append(buyComponent)
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(sellComponent);
            case ONLY_BUY -> buyComponent;
            case ONLY_SELL -> sellComponent;
        };
    }

    public void calculateInventoryValue(
        @NotNull Inventory inventory,
        boolean includeStack,
        @NotNull Consumer<@Nullable InventoryValueData> consumer
    ) {
        int items = 0;
        float totalBuyValue = 0f;
        float totalSellValue = 0f;

        for(int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.itemStackAt(i);
            if(itemStack == null || itemStack.isAir()) {
                continue;
            }

            ResourceLocation identifier = itemStack.getIdentifier();
            if(identifier == null) {
                continue;
            }

            MarketItem item = OPSuchtAddon.references().marketManager().getItem(identifier.getPath());
            if(item == null || !item.isValid()) {
                continue;
            }
            MarketStack stack = new MarketStack(item, itemStack.getSize());
            items += includeStack ? stack.getSize() : 1;
            totalBuyValue += includeStack ? stack.getStackBuyPrice() : stack.getBuyPrice();
            totalSellValue += includeStack ? stack.getStackSellPrice() : stack.getSellPrice();
        }

        consumer.accept(new InventoryValueData(items, totalBuyValue, totalSellValue));
    }

    @Override
    public @NotNull String formatFloat(float number) {
        DecimalFormat format = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(Locale.GERMANY));
        return format.format(number);
    }

    @Override
    public void cachePrices() {
        Response<JsonObject> response = Request.ofGson(JsonObject.class)
            .url(ENDPOINT)
            .addHeader("User-Agent", OPSuchtAddon.getUserAgent())
            .executeSync();

        if (response.hasException()) {
            return;
        }

        if(response.getStatusCode() != 200) {
            return;
        }

        JsonObject body = response.get();

        try {
            for (String categoryName : body.keySet()) {
                JsonObject category = body.get(categoryName).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : category.entrySet()) {
                    String itemId = entry.getKey().toLowerCase();
                    JsonArray prices = entry.getValue().getAsJsonArray();

                    if(prices.size() != 2) continue;

                    this.items.put(itemId, new MarketItem(
                        entry.getKey(),
                        prices.get(0).getAsJsonObject().get("price").getAsFloat(),
                        prices.get(1).getAsJsonObject().get("price").getAsFloat()
                    ));
                }
            }
        } catch (Exception ignored) {

        }
    }
}
