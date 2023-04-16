package com.rappytv.opsucht.context;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.OPSuchtConfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.entity.player.interaction.BulletPoint;
import net.labymod.api.client.gui.icon.Icon;

public class PayContext implements BulletPoint {

    private final OPSuchtConfig config;

    public PayContext(OPSuchtAddon addon) {
        this.config = addon.configuration();
    }

    @Override
    public Component getTitle() {
        return Component.text(Util.getTranslation("opsucht.context.pay"));
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public void execute(Player player) {
        Laby.labyAPI().minecraft().executeNextTick(
            () -> Laby.labyAPI().minecraft().openChat("/pay " + player.getName() + " " + config.payDefault().get())
        );
    }

    @Override
    public boolean isVisible(Player playerInfo) {
        return config.friendRequestContext().get();
    }
}
