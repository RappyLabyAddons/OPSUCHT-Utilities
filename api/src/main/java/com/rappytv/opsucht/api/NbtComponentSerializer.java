package com.rappytv.opsucht.api;

import net.labymod.api.client.component.Component;
import net.labymod.api.reference.annotation.Referenceable;

@Referenceable
public interface NbtComponentSerializer {

    Component deserializeComponent(String data) throws RuntimeException;

}
