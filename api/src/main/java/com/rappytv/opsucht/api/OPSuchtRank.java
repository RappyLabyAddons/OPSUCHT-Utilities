package com.rappytv.opsucht.api;

public enum OPSuchtRank {
    PLAYER,
    PREMIUM,
    DIAMOND,
    ULTRA(30),
    LEGEND(14),
    SUPREME(3),
    PLATIN(3),
    OP(1);

    private final Integer skullCooldownDays;

    OPSuchtRank() {
        this(null);
    }

    OPSuchtRank(Integer skullCooldownDays) {
        this.skullCooldownDays = skullCooldownDays;
    }

    public Integer getSkullCooldownDays() {
        return skullCooldownDays;
    }

    public boolean hasSkullPermission() {
        return this.skullCooldownDays != null;
    }
}
