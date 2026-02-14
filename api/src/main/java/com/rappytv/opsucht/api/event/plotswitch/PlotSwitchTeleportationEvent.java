package com.rappytv.opsucht.api.event.plotswitch;

import net.labymod.api.event.Event;

public record PlotSwitchTeleportationEvent(String username, int previousPlot, int currentPlot) implements Event {

}
