package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchErrorEvent;
import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchTeleportationEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.event.Subscribe;
import net.labymod.api.notification.Notification;

public class PlotSwitchListener {

    private final OPSuchtAddon addon;

    public PlotSwitchListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onTeleport(PlotSwitchTeleportationEvent event) {
        Laby.references().chatExecutor().displayClientMessage(
            OPSuchtAddon.prefix().append(Component.translatable(
                "opsucht.plotSwitch.teleported",
                Component.text(event.username(), NamedTextColor.AQUA),
                Component.text(event.previousPlot(), NamedTextColor.AQUA),
                Component.text(event.currentPlot(), NamedTextColor.AQUA)
            ))
        );
    }

    @Subscribe
    public void onFail(PlotSwitchErrorEvent event) {
        if(!this.addon.configuration().plotSwitch().get()) {
            return;
        }
        if(Laby.labyAPI().minecraft().isIngame()) {
            Laby.references().chatExecutor().displayClientMessage(
                OPSuchtAddon.prefix().append(event.reasonComponent().color(NamedTextColor.RED))
            );
        } else {
            Notification.builder()
                .title(Component.translatable("opsucht.plotSwitch.error"))
                .text(event.reasonComponent())
                .buildAndPush();
        }
    }

}
