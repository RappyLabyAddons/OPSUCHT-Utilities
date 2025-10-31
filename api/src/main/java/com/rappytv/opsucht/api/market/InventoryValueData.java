package com.rappytv.opsucht.api.market;

public record InventoryValueData(int itemAmount, float buyValue, float sellValue) {

    public boolean isEmpty() {
        return this.itemAmount == 0;
    }

    public boolean isValid() {
        return this.itemAmount >= 0 && this.buyValue >= 0 && this.sellValue >= 0;
    }
}