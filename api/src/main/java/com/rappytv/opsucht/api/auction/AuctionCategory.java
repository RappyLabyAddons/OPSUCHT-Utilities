package com.rappytv.opsucht.api.auction;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public record AuctionCategory(
    @NotNull String name,
    @NotNull String displayName,
    @NotNull String displayMaterial,
    @NotNull String icon,
    @NotNull List<String> matchTypes
) {

    public boolean matches(@NotNull Auction auction) {
        return this.matches(auction.category());
    }

    public boolean matches(@NotNull String category) {
        return this.matchTypes.contains(category.toUpperCase());
    }
}
