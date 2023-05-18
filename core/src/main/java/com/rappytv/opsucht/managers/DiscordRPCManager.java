package com.rappytv.opsucht.managers;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.thirdparty.discord.DiscordActivity;
import net.labymod.api.thirdparty.discord.DiscordActivity.Asset;
import net.labymod.api.thirdparty.discord.DiscordActivity.Builder;
import net.labymod.api.util.I18n;
import javax.inject.Singleton;

@Singleton
public class DiscordRPCManager {

    private final OPSuchtAddon addon;
    private boolean updating;

    public DiscordRPCManager(OPSuchtAddon addon) {
        this.addon = addon;
    }

    public void removeCustomRPC() {
        addon.labyAPI().thirdPartyService().discord().displayDefaultActivity(false);
    }

    public void updateCustomRPC() {
        DiscordRPCSubconfig rpcConfig = addon.configuration().discordRPCSubconfig();
        if(!rpcConfig.enabled()) {
            removeCustomRPC();
            return;
        }
        if(!Util.isConnectedToServer() || updating) return;

        updating = true;

        DiscordActivity currentActivity = addon.labyAPI().thirdPartyService().discord().getDisplayedActivity();
        Builder builder = DiscordActivity.builder(this);

        if(currentActivity != null)
            builder.start(currentActivity.getStartTime());

        builder.largeAsset(Asset.of("https://raw.githubusercontent.com/LabyMod/server-media/master/minecraft_servers/opsucht/icon.png", "OPSUCHT.net"));
        builder.details(I18n.translate("opsucht.rpc.on", rpcConfig.showSubServer().get() ? "<subserver>" : "OPSUCHT.net"));
        builder.state(rpcConfig.showPlayerCount().get() ? I18n.translate("opsucht.rpc.players", "69", "187") : "");

        this.addon.labyAPI().thirdPartyService().discord().displayActivity(builder.build());
        updating = false;
    }
}
