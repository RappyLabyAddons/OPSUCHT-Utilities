package com.rappytv.opsucht.core.manager;

import com.rappytv.opsucht.api.OPSuchtRank;
import com.rappytv.opsucht.api.event.reminders.DailyRewardReminderEvent;
import com.rappytv.opsucht.api.event.reminders.SkullReminderEvent;
import com.rappytv.opsucht.api.generated.ReferenceStorage;
import com.rappytv.opsucht.core.OPSuchtAddon;
import com.rappytv.opsucht.core.config.subconfig.ReminderConfig;
import net.labymod.api.Laby;
import net.labymod.api.util.concurrent.task.Task;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private static final Map<String, Task> TASK_CACHE = new HashMap<>();

    private final OPSuchtAddon addon;
    private final ReferenceStorage references;

    public TaskManager(OPSuchtAddon addon) {
        this.addon = addon;
        this.references = OPSuchtAddon.references();
    }

    public void executeTasks() {
        // OPSucht HTTP API Cache
        this.cacheAuctionTask().execute();
        this.cacheMarketPriceTask().execute();
        this.cacheMerchantRateTask().execute();

        // Reward reminders
        this.compareSkullDateTask().execute();
        this.compareDailyRewardDateTask().execute();
    }

    public Task cacheAuctionTask() {
        return TASK_CACHE.computeIfAbsent("cache-auctions", (key) -> Task.builder(() -> {
            if(this.references.auctionManager().getActiveAuctions().isEmpty()
                || this.addon.server().isConnected()) {
                this.references.auctionManager().cacheAuctions();
            }
        }).repeat(2, TimeUnit.MINUTES).build());
    }

    public Task cacheMarketPriceTask() {
        return TASK_CACHE.computeIfAbsent("cache-market-prices", (key) ->
            Task.builder(this.references.marketManager()::cachePrices)
                .repeat(30, TimeUnit.MINUTES)
                .build()
        );
    }

    public Task cacheMerchantRateTask() {
        return TASK_CACHE.computeIfAbsent("cache-merchant-rates", (key) ->
            Task.builder(this.references.merchantManager()::cacheRates)
                .repeat(30, TimeUnit.MINUTES)
                .build()
        );
    }

    public Task autoFlyTask() {
        return TASK_CACHE.computeIfAbsent("auto-fly", (key) -> Task.builder(() ->
            Laby.references().chatExecutor().chat("/fly", false)
        ).delay(1, TimeUnit.SECONDS).build());
    }

    public Task compareSkullDateTask() {
        return TASK_CACHE.computeIfAbsent("compare-skull-date",(key) ->
            Task.builder(() -> {
                if(!addon.configuration().enabled().get() || !addon.server().isConnected()) {
                    return;
                }
                ReminderConfig config = addon.configuration().reminderConfig();
                if(!config.skullReminders().get() || config.lastSkullClaim().get() == -1L) {
                    return;
                }
                OPSuchtRank rank = addon.configuration().rank().get();
                if(!rank.hasSkullPermission()) {
                    return;
                }
                long nextSkull = config.lastSkullClaim().get()
                    + rank.getSkullCooldownDays()
                    * 86400000L;

                if(nextSkull > System.currentTimeMillis()) {
                    return;
                }
                Laby.fireEvent(new SkullReminderEvent());
            }).repeat(5, TimeUnit.MINUTES).build()
        );
    }

    public Task compareDailyRewardDateTask() {
        return TASK_CACHE.computeIfAbsent("compare-daily-reward-date", (key) ->
            Task.builder(() -> {
                if(!addon.configuration().enabled().get() || !addon.server().isConnected()) {
                    return;
                }
                ReminderConfig config = addon.configuration().reminderConfig();
                if(!config.dailyRewardClaimer().get()
                    || config.lastDailyRewardClaim().get() == -1L) {
                    return;
                }
                long nextDaily = config.lastDailyRewardClaim().get() + 86400000L;

                if(nextDaily > System.currentTimeMillis()) {
                    return;
                }
                Laby.fireEvent(new DailyRewardReminderEvent());
            }).repeat(5, TimeUnit.MINUTES).build()
        );
    }
}
