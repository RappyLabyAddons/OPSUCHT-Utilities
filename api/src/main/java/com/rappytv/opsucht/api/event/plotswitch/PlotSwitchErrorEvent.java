package com.rappytv.opsucht.api.event.plotswitch;

import net.labymod.api.client.component.Component;
import net.labymod.api.event.Event;
import net.labymod.api.util.TextFormat;

public class PlotSwitchErrorEvent implements Event {

    private final Component reason;

    public PlotSwitchErrorEvent(String reason) {
        this.reason = Component.text(reason);
    }

    public PlotSwitchErrorEvent(Reason reason) {
        this.reason = Component.translatable("opsucht.plotswitch.errors" + reason.toString());
    }

    public Component reasonComponent() {
        return this.reason;
    }

    public enum Reason {
        NEGATIVE_PLOT,
        TIMED_OUT,
        NOT_INITIALIZED,
        ALREADY_AWAITING_TELEPORTATION;

        private final String camelCase;

        Reason() {
            this.camelCase = TextFormat.SNAKE_CASE.toLowerCamelCase(this.name());
        }

        @Override
        public String toString() {
            return this.camelCase;
        }
    }
}
