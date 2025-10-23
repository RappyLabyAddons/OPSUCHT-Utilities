package com.rappytv.opsucht.api;

public class MarketStack extends MarketItem {

    private final int size;

    public MarketStack(MarketItem item, int size) {
        super(item.getId(), item.getBuyPrice(), item.getSellPrice());
        this.size = size;
    }

    public MarketStack(String id, float buyPrice, float sellPrice, int size) {
        super(id, buyPrice, sellPrice);
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public float getStackBuyPrice() {
        return this.getBuyPrice() * this.size;
    }

    public float getStackSellPrice() {
        return this.getSellPrice() * this.size;
    }

    public boolean isValid() {
        return this.size > 0 && super.isValid();
    }
}