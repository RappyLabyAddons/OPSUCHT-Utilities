package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.api.event.plotswitch.PlotSwitchFailEvent;
import com.rappytv.opsucht.api.plotswitch.PlotSwitchDirection;
import com.rappytv.opsucht.api.plotswitch.PlotSwitchManager;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.util.function.IntConsumer;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;
import net.labymod.api.util.Debounce;

public class KeyListener {

    private static final String DEBOUNCE_ID = "opsucht-plot-teleportation";

    private final OPSuchtAddon addon;
    private final PlotSwitchManager manager;

    private PlotSwitchDirection direction = null;
    private int amount = 0;

    public KeyListener(OPSuchtAddon addon) {
        this.addon = addon;
        this.manager = OPSuchtAddon.references().plotSwitchManager();
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if(!this.addon.server().isConnected()
            || !Key.L_SHIFT.isPressed()
            || event.state() != State.PRESS
            || Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }

        if(this.manager.getCurrentPlayer() == null || this.manager.isAwaitingTeleportation()) {
            return;
        }
        if(event.key() == Key.ARROW_LEFT) {
            // To prevent changing the direction
            if(this.direction == PlotSwitchDirection.NEXT) {
                return;
            }
            if(this.manager.getCurrentPlot() == 1) { // Can't teleport to negative plots
                Laby.fireEvent(new PlotSwitchFailEvent("Cannot teleport to a negative plot"));
                return;
            }
            this.direction = PlotSwitchDirection.PREVIOUS;
            this.amount++;
            this.runDebounce();
        } else if(event.key() == Key.ARROW_RIGHT) {
            if(this.direction == PlotSwitchDirection.PREVIOUS) {
                return;
            }
            this.direction = PlotSwitchDirection.NEXT;
            this.amount++;
            this.runDebounce();
        }
    }

    // Using a Debounce to be able to jump over multiple plots without command spam
    private void runDebounce() {
        IntConsumer teleport = this.direction == PlotSwitchDirection.NEXT
            ? this.manager::teleportNext
            : this.manager::teleportPrevious;

        Debounce.of(DEBOUNCE_ID, 700, () -> {
            try {
                teleport.accept(this.amount);
            } catch (IllegalArgumentException e) {
                Laby.fireEvent(new PlotSwitchFailEvent(e.getMessage()));
            }
            this.reset();
        });
    }

    private void reset() {
        this.amount = 0;
        this.direction = null;
    }

}
