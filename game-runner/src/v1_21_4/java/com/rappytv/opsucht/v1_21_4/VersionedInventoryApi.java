package com.rappytv.opsucht.v1_21_4;

import com.rappytv.opsucht.api.inventory.InventoryApi;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ClickType;

@Singleton
@Implements(InventoryApi.class)
public class VersionedInventoryApi implements InventoryApi {

    @Override
    public void clickSlot(int slot) { // TODO: Implement other versions
        LocalPlayer player = Minecraft.getInstance().player;
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        if(player == null || gameMode == null) {
            return;
        }
        gameMode.handleInventoryMouseClick(
            player.containerMenu.containerId,
            slot,
            0,
            ClickType.PICKUP,
            player
        );
    }
}
