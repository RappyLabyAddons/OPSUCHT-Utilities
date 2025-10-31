package com.rappytv.opsucht.api.auction;

import com.rappytv.opsucht.api.market.MarketItem;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Auction(
    @NotNull Item item,
    @NotNull String category,
    float startBid,
    float currentBid,
    @NotNull Date endTime
) {

    public boolean isActive() {
        return new Date().before(this.endTime);
    }

    public String formatRelativeExpiration() {
        Duration duration = Duration.between(Instant.now(), this.endTime.toInstant());

        if (!duration.isPositive()) {
            return "0s";
        }

        long seconds = duration.getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm", minutes);
        } else {
            return "< 1m";
        }
    }

    public record Item(
        @NotNull String material,
        @Nullable String icon,
        int amount,
        @Nullable String displayName
    ) {

        public String getName() {
            return this.displayName != null ? this.displayName : MarketItem.formatName(this.material);
        }
    }
}
