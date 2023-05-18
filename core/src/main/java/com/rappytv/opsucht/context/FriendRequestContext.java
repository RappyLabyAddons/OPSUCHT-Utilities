package com.rappytv.opsucht.context;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.interaction.BulletPoint;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.I18n;

public class FriendRequestContext implements BulletPoint {

    private final OPSuchtConfig config;

    public FriendRequestContext(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Override
    public Component getTitle() {
        return Component.text(I18n.translate("opsucht.context.friendRequest"));
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().chatExecutor().chat("/freund hinzufügen " + player.getName())
        );
    }

    @Override
    public boolean isVisible(Player playerInfo) {
        return Util.isConnectedToServer() && config.friendRequestContext().get();
    }
}
