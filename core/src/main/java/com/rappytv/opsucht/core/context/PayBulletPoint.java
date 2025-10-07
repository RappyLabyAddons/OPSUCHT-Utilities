package com.rappytv.opsucht.core.context;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.interaction.BulletPoint;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.I18n;

public class PayBulletPoint implements BulletPoint {

    private static final String COMMAND = "/pay %s ";
    private final OPSuchtAddon addon;

    public PayBulletPoint(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Override
    public Component getTitle() {
        return Component.text(I18n.translate("opsucht.interaction.pay"));
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().openChat(String.format(COMMAND, player.getName()))
        );
    }

    @Override
    public boolean isVisible(Player playerInfo) {
        return this.addon.server().isConnected() && this.addon.configuration().interactionBulletConfig().payBullet().get();
    }
}
