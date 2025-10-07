package com.rappytv.opsucht.api;

import net.labymod.api.configuration.loader.property.ConfigProperty;

public interface IRichPresenceConfig {

    ConfigProperty<Boolean> enabled();

    ConfigProperty<Boolean> showSubServer();

    ConfigProperty<Boolean> showPlayerCount();
}
