package com.rappytv.opsucht.context;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.OPSuchtConfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.interaction.BulletPoint;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.I18n;

public class ClanInviteContext implements BulletPoint {

    private final OPSuchtConfig config;

    public ClanInviteContext(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Override
    public Component getTitle() {
        return Component.text(I18n.translate("opsucht.context.clanInvite"));
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().chatExecutor().chat("/clan verwalten einladen " + player.getName())
        );
    }

    @Override
    public boolean isVisible(Player playerInfo) {
        return OPSuchtAddon.isConnected() && config.contextSubconfig().clanInviteContext().get();
    }
}
