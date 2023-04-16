package com.rappytv.opsucht;

import com.rappytv.opsucht.context.ClanInviteContext;
import com.rappytv.opsucht.context.FriendRequestContext;
import com.rappytv.opsucht.context.PayContext;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class OPSuchtAddon extends LabyAddon<OPSuchtConfig> {

    public static final String ip = "162.19.233.3";

    @Override
    protected void enable() {
        registerSettingCategory();
        labyAPI().interactionMenuRegistry().register(new ClanInviteContext(this));
        labyAPI().interactionMenuRegistry().register(new FriendRequestContext(this));
        labyAPI().interactionMenuRegistry().register(new PayContext(this));
    }

    @Override
    protected Class<? extends OPSuchtConfig> configurationClass() {
        return OPSuchtConfig.class;
    }
}
