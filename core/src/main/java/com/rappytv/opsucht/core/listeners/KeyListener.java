package com.rappytv.opsucht.core.listeners;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;

public class KeyListener {

    private final OPSuchtAddon addon;

    public KeyListener(OPSuchtAddon addon) {
        this.addon = addon;
    }

    @Subscribe
    public void onKeyDown(KeyEvent event) {
        if(!this.addon.server().isConnected()
            || !Key.L_SHIFT.isPressed()
            || event.state() != State.PRESS
            || Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened()) {
            return;
        }

        if(OPSuchtAddon.references().plotSwitchManager().getCurrentPlayer() == null) {
            return;
        }
        if(event.key() == Key.ARROW_LEFT) {
            System.out.println("previous");
        } else if(event.key() == Key.ARROW_RIGHT) {
            System.out.println("next");
        }
    }

}
