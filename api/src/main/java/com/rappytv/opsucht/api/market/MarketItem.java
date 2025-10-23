package com.rappytv.opsucht.api.market;

public class MarketItem {

    private final String id;
    private final String name;
    private final float buyPrice;
    private final float sellPrice;

    public MarketItem(String id, float buyPrice, float sellPrice) {
        this.id = id.toLowerCase();
        this.name = formatName(this.id);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public float getBuyPrice() {
        return this.buyPrice;
    }

    public float getSellPrice() {
        return this.sellPrice;
    }

    @SuppressWarnings("all")
    public boolean isValid() {
        return this.buyPrice >= 0 && this.sellPrice >= 0;
    }

    private static String formatName(String id) {
        String[] parts = id.split("_");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if(i > 0) {
                builder.append(" ");
            }
            String part = parts[i];
            builder.append(part.substring(0, 1).toUpperCase())
                .append(part.substring(1));
        }

        return builder.toString();
    }
}
