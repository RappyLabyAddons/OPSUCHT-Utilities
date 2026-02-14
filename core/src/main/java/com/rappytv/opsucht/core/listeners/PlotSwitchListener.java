package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchFailEvent;
import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchTeleportationEvent;
import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchTimeoutEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.client.component.Component;
import net.labymod.api.event.Subscribe;
import net.labymod.api.notification.Notification;

public class PlotSwitchListener {

    private final OPSuchtAddon addon;

    public PlotSwitchListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onTeleport(PlotSwitchTeleportationEvent event) {
        // TODO: add real logic. probably with chat messages
        System.out.println("Teleported from " + event.username() + "'s " + event.previousPlot() + ". plot to their " + event.currentPlot() + ". plot");
    }

    @Subscribe
    public void onTimeout(PlotSwitchTimeoutEvent event) {
        Notification.builder() // TODO: Translate messages
            .title(Component.translatable("Teleportation timeout"))
            .text(Component.translatable("The teleportation was not successfuly. Try again later!"))
            .buildAndPush();
    }

    @Subscribe
    public void onFail(PlotSwitchFailEvent event) {
        System.out.println("Plot switch failed: " + event.reason());
    }

}
