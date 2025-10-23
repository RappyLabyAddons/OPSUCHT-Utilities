package com.rappytv.opsucht.core.manager;

import com.rappytv.opsucht.api.rpc.IRichPresenceConfig;
import com.rappytv.opsucht.api.rpc.RichPresenceManager;
import net.labymod.api.Laby;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.scoreboard.Scoreboard;
import net.labymod.api.client.scoreboard.ScoreboardTeam;
import net.labymod.api.models.Implements;
import net.labymod.api.thirdparty.discord.DiscordActivity;
import net.labymod.api.thirdparty.discord.DiscordActivity.Asset;
import net.labymod.api.thirdparty.discord.DiscordActivity.Builder;
import net.labymod.api.thirdparty.discord.DiscordApp;
import net.labymod.api.util.I18n;
import javax.inject.Singleton;

@Singleton
@Implements(RichPresenceManager.class)
public class DefaultRichPresenceManager implements RichPresenceManager {

    private static final String DEFAULT_SERVER = "OPSUCHT.net";
    private static final Asset ASSET = Asset.of("https://raw.githubusercontent.com/LabyMod/server-media/master/minecraft_servers/opsucht/icon.png", DEFAULT_SERVER);

    private boolean updating;
    private String subserver = DEFAULT_SERVER;
    private String players = "0/0";

    @Override
    public void updateCustomRPC(IRichPresenceConfig config) {
        this.updateCustomRPC(config, false);
    }

    @Override
    public void updateCustomRPC(IRichPresenceConfig config, boolean joining) {
        if(this.updating) return;
        if(!config.enabled().get()) {
            this.removeCustomRPC();
            return;
        }

        this.updating = true;

        DiscordActivity currentActivity = this.discord().getDisplayedActivity();
        Builder builder = DiscordActivity.builder(this);

        if(currentActivity != null) {
            builder.start(currentActivity.getStartTime());
        }

        builder.largeAsset(ASSET);
        builder.details(I18n.translate(
            "opsucht.rpc.subserver",
            config.showSubServer().get()
                ? this.getSubServer()
                : DEFAULT_SERVER
        ));
        builder.state(
            joining
                ? I18n.translate("opsucht.rpc.loading")
                : config.showPlayerCount().get()
                    ? I18n.translate("opsucht.rpc.players", this.getPlayerCount())
                    : null
        );

        this.discord().displayActivity(builder.build());
        this.updating = false;
    }

    @Override
    public void removeCustomRPC() {
        this.discord().displayDefaultActivity();
    }

    private DiscordApp discord() {
        return Laby.labyAPI().thirdPartyService().discord();
    }

    private String getSubServer() {
        String value = this.getScoreboardScoreByValue("server");
        if(value == null || value.equals(this.subserver)) return this.subserver;
        return this.subserver = value.toLowerCase();
    }

    private String getPlayerCount() {
        String value = this.getScoreboardScoreByValue("players");
        if(value == null || value.equals(this.players)) return this.players;
        return this.players = value;
    }

    private String getScoreboardScoreByValue(String teamname) {
        Scoreboard scoreboard = Laby.labyAPI().minecraft().getScoreboard();
        if(scoreboard == null) return null;

        ScoreboardTeam scoreboardTeam = scoreboard.getTeams().stream()
            .filter(team -> team.getTeamName().equals(teamname))
            .findFirst().orElse(null);
        if(scoreboardTeam == null) return null;

        if(scoreboardTeam.getSuffix() instanceof TextComponent suffixComponent) {
            if(suffixComponent.getChildren().isEmpty()) {
                return null;
            }
            TextComponent suffixChild = (TextComponent) suffixComponent.getChildren().getFirst();
            return suffixChild.getText();
        }
        return null;
    }
}
