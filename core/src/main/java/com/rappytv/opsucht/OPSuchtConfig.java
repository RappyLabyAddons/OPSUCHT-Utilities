package com.rappytv.opsucht;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class OPSuchtConfig extends AddonConfig {

    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
}
