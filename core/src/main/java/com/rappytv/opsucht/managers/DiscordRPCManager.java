package com.rappytv.opsucht.managers;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import com.rappytv.opsucht.util.Util;
import net.labymod.api.Laby;
import net.labymod.api.client.scoreboard.DisplaySlot;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.scoreboard.ScoreboardScore;
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
        builder.details(I18n.translate("opsucht.rpc.on", rpcConfig.showSubServer().get() ? getSubServer() : "OPSUCHT.net"));
        builder.state(rpcConfig.showPlayerCount().get() ? I18n.translate("opsucht.rpc.players", getPlayerCount()) : "");

        this.addon.labyAPI().thirdPartyService().discord().displayActivity(builder.build());
        updating = false;
    }

    public String getSubServer() {
        int line = isOnLobby() ? 5 : 8;
        String value = getScoreboardScoreByValue(line);
        return value != null ? value : "OPSUCHT.net";
    }

    public String getPlayerCount() {
        int line = isOnLobby() ? 2 : 5;
        String value = getScoreboardScoreByValue(line);
        return value != null ? value : "0/0";
    }

    private String getScoreboardScoreByValue(int line) {
        Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
        if(scoreboard == null) return null;

        ScoreboardScore scoreboardScore = scoreboard.getScores(scoreboard.getObjective(DisplaySlot.SIDEBAR)).stream()
            .filter(score -> score.getValue() == line)
            .findFirst().orElse(null);
        if(scoreboardScore == null) return null;
        return scoreboardScore.getName();
    }

    private boolean isOnLobby() {
        Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
        if(scoreboard == null) return false;

        return scoreboard.getScores(scoreboard.getObjective(DisplaySlot.SIDEBAR)).size() == 7;
    }
}
