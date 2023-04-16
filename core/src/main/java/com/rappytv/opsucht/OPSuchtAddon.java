package com.rappytv.opsucht;

import net.labymod.api.addon.LabyAddon;

public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    @Override
    protected void enable() {
        registerSettingCategory();
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
