package com.rappytv.opsucht.core.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rappytv.opsucht.api.IPriceHudWidgetConfig;
import com.rappytv.opsucht.api.MarketItem;
import com.rappytv.opsucht.api.MarketManager;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Singleton;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextColor;
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
    public @Nullable MarketItem getPrice(String itemId) {
        return this.items.get(itemId.toLowerCase());
    }

    @Override
    public @NotNull Component formatValueComponent(float buyValue, float sellValue, IPriceHudWidgetConfig config) {
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

    private String formatFloat(float number) {
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
