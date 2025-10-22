package com.rappytv.opsucht.core.ui.hudwidget;

import com.google.gson.JsonObject;
import com.rappytv.opsucht.api.event.PlayerRecordForceRefetchEvent;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.ui.hudwidget.PlayerRecordHudWidget.PlayerRecordHudWidgetConfig;
import java.util.concurrent.TimeUnit;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.event.Subscribe;
import net.labymod.api.util.MethodOrder;
import net.labymod.api.util.concurrent.task.Task;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;

public class PlayerRecordHudWidget extends TextHudWidget<PlayerRecordHudWidgetConfig> {

    private static final String ENDPOINT = "https://prapi.rappytv.com/opsucht";
    private static final Component ERROR_COMPONENT = Component.translatable(
        "opsucht.hudWidget.player_record.apiError"
    );

    private final OPSuchtAddon addon;
    private final Task refetchTask;
    private TextLine line = null;
    private Integer lastValue = -1;

    public PlayerRecordHudWidget(OPSuchtAddon addon) {
        super("player_record", PlayerRecordHudWidgetConfig.class);
        this.addon = addon;

        this.refetchTask = Task.builder(() -> {
            this.lastValue = this.fetchPlayerRecord();
            if (this.line == null) {
                return;
            }
            Laby.labyAPI().minecraft().executeOnRenderThread(this::updateLine);
        }).repeat(1, TimeUnit.HOURS).build();
        this.refetchTask.run();
        this.refetchTask.execute();
    }

    @Override
    public void load(PlayerRecordHudWidgetConfig config) {
        super.load(config);
        this.line = this.createLine(
            Component.translatable("opsucht.hudWidget.player_record.name"),
            this.lastValue
        );
        this.updateLine();
    }

    @Override
    public void onTick(boolean isEditorContext) {
        if(isEditorContext) {
            this.line.updateAndFlush(this.lastValue != -1 ? this.lastValue : ERROR_COMPONENT);
        }
    }

    @Override
    public boolean isVisibleInGame() {
        return this.addon.server().isConnected() && super.isVisibleInGame();
    }

    @Subscribe
    public void onForceRefetch(PlayerRecordForceRefetchEvent event) {
        this.refetchTask.run();
    }

    private void updateLine() {
        this.line.setState(this.lastValue != -1 ? State.VISIBLE : State.HIDDEN);
        this.line.updateAndFlush(this.lastValue);
    }

    private int fetchPlayerRecord() {
        Response<JsonObject> response = Request.ofGson(JsonObject.class)
            .url(ENDPOINT)
            .addHeader("User-Agent", "OPSucht Addon v" + this.addon.addonInfo().getVersion())
            .executeSync();

        if(response.hasException()) {
            this.addon.logger().error("Failed to fetch player record", response.exception());
            return -1;
        }

        JsonObject body = response.get();

        if(response.getStatusCode() != 200) {
            String error = body.get("error").getAsString();
            this.addon.logger().error(String.format(
                "Failed to fetch player record with HTTP status code %s: %s",
                response.getStatusCode(),
                error
            ));
            return -1;
        }

        try {
            return body.get("player_record").getAsInt();
        } catch (Exception e) {
            this.addon.logger().error("Failed to parse player record", e);
            return -1;
        }
    }

    public static class PlayerRecordHudWidgetConfig extends TextHudWidgetConfig {

        @Exclude
        private final ConfigProperty<Boolean> dummySetting = new ConfigProperty<>(false);

        @SuppressWarnings("deprecation")
        @MethodOrder(after = "dummySetting")
        @ButtonSetting(translation = "opsucht.hudWidget.player_record.forceRefetch.text")
        public void forceRefetch() {
            Laby.fireEvent(new PlayerRecordForceRefetchEvent());
        }
    }
}
