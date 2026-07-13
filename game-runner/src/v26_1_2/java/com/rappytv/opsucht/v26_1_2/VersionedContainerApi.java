package com.rappytv.opsucht.v26_1_2;

import com.rappytv.opsucht.api.inventory.ContainerApi;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;

@Singleton
@Implements(ContainerApi.class)
public class VersionedContainerApi implements ContainerApi {

  @Override
  public void clickSlot(int slot) {
    LocalPlayer player = Minecraft.getInstance().player;
    MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
    if (player == null || gameMode == null) {
      return;
    }
    gameMode.handleContainerInput(
        player.containerMenu.containerId,
        slot,
        0, // Left mouse button
        ContainerInput.PICKUP,
        player
    );
  }

  @Override
  public void closeContainer() {
    LocalPlayer player = Minecraft.getInstance().player;
    if (player == null) {
      return;
    }
    player.closeContainer();
  }
}
