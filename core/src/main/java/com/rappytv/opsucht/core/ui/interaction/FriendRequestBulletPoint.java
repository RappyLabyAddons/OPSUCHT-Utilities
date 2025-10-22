package com.rappytv.opsucht.core.ui.interaction;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.interaction.BulletPoint;
import net.labymod.api.client.gui.icon.Icon;

public class FriendRequestBulletPoint implements BulletPoint {

    private static final String COMMAND = "/freund hinzufügen %s";
    private final OPSuchtAddon addon;

    public FriendRequestBulletPoint(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("opsucht.interaction.friendRequest");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().chatExecutor().chat(String.format(COMMAND, player.getName()))
        );
    }

    @Override
    public boolean isVisible(Player playerInfo) {
        return this.addon.server().isConnected() && this.addon.configuration().interactionBulletConfig().friendRequestBullet().get();
    }
}
