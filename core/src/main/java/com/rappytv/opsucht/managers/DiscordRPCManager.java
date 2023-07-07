package com.rappytv.opsucht.managers;

import com.rappytv.opsucht.OPSuchtAddon;
import com.rappytv.opsucht.config.subconfig.DiscordRPCSubconfig;
import net.labymod.api.Laby;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.thirdparty.discord.DiscordActivity;
import net.labymod.api.thirdparty.discord.DiscordActivity.Asset;
import net.labymod.api.thirdparty.discord.DiscordActivity.Builder;
import net.labymod.api.util.I18n;
import javax.inject.Singleton;

@Singleton
public class DiscordRPCManager {

    private final OPSuchtAddon addon;
    private boolean updating;
    private String subserver = "OPSUCHT.net";
    private String players = "0/0";

    public DiscordRPCManager(OPSuchtAddon addon) {
        this.addon = addon;
    }

    public void removeCustomRPC() {
        addon.labyAPI().thirdPartyService().discord().displayDefaultActivity();
    }

    public void updateCustomRPC() {
        if(!OPSuchtAddon.isConnected() || updating) return;
        DiscordRPCSubconfig rpcConfig = addon.configuration().discordRPCSubconfig();
        if(!rpcConfig.enabled()) {
            removeCustomRPC();
            return;
        }

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
        String value = getScoreboardScoreByValue("server");
        if(value == null || value.equals(subserver)) return subserver;
        subserver = value.toLowerCase();
        return subserver;
    }

    public String getPlayerCount() {
        String value = getScoreboardScoreByValue("players");
        if(value == null || value.equals(players)) return players;
        players = value;
        return players;
    }

    private String getScoreboardScoreByValue(String teamname) {
        Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
        if(scoreboard == null) return null;

        ScoreboardTeam scoreboardTeam = scoreboard.getTeams().stream()
            .filter(team -> team.getTeamName().equals(teamname))
            .findFirst().orElse(null);
        if(scoreboardTeam == null) return null;

        if(scoreboardTeam.getSuffix() instanceof TextComponent suffixComponent) {
            if(suffixComponent.getChildren().size() == 0)
                return null;
            TextComponent suffixChild = (TextComponent) suffixComponent.getChildren().get(0);
            return suffixChild.getText();
        }
        return null;
    }
}
