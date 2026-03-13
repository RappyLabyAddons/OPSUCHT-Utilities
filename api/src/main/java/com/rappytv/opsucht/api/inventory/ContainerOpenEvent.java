package com.rappytv.opsucht.api.inventory;

import net.labymod.api.client.component.Component;
import net.labymod.api.event.Event;

public record ContainerOpenEvent(Component title) implements Event {

}
