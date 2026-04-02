package com.rappytv.opsucht.api;

import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

public class OPSuchtTextures {

    public static class SpriteHud {

        public static final ResourceLocation TEXTURE = ResourceLocation.create("opsucht", "textures/hud.png");

        public static final Icon AUCTIONS = Icon.sprite32(TEXTURE, 0, 0);
        public static final Icon INVENTORY_VALUE = Icon.sprite32(TEXTURE, 1, 0);
        public static final Icon ITEM_VALUE = Icon.sprite32(TEXTURE, 2, 0);
        public static final Icon PLAYER_RECORD = Icon.sprite32(TEXTURE, 3, 0);
        public static final Icon MERCHANT = Icon.sprite32(TEXTURE, 0, 1);
    }
}
