package com.rappytv.opsucht.api.event.plotswitch;

import net.labymod.api.event.Event;

public record PlotSwitchFailEvent(String reason) implements Event {

}
