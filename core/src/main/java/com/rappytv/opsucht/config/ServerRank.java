package com.rappytv.opsucht.config;

public enum ServerRank {
    PLAYER(0),
    PREMIUM(1),
    DIAMOND(2),
    ULTRA(3),
    LEGEND(4),
    SUPREME(5),
    PLATIN(6),
    OP(7);

    private final int position;

    ServerRank(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public boolean hasPermission(ServerRank rank) {
        return position >= rank.getPosition();
    }
}
