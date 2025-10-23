package com.rappytv.opsucht.api;

import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

public class OPSuchtTextures {

    public static class SpriteHud {

        public static final ResourceLocation TEXTURE = ResourceLocation.create("opsucht", "textures/hud.png");

        public static final Icon INVENTORY_VALUE = verticalSprite64(0);
        public static final Icon ITEM_VALUE = verticalSprite64(1);
        public static final Icon PLAYER_RECORD = verticalSprite64(2);

        private static Icon verticalSprite64(int x) {
            return Icon.sprite(
                TEXTURE,
                x << 6,
                0,
                64,
                64,
                256,
                64
            );
        }
    }
}
