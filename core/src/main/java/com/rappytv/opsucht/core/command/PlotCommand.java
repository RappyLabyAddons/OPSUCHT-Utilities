package com.rappytv.opsucht.core.command;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;

public class PlotCommand extends Command {

    public PlotCommand(OPSuchtAddon addon) {
        super("plot", "p");

        this.withSubCommand(new HomeCommand(addon));
        this.withSubCommand(new VisitCommand(addon));
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        return false;
    }

    public static class HomeCommand extends SubCommand {

        private final OPSuchtAddon addon;

        private HomeCommand(OPSuchtAddon addon) {
            super("home", "h");
            this.addon = addon;
        }

        @Override
        public boolean execute(String prefix, String[] arguments) {
            String username = Laby.labyAPI().getName();
            int plot = 1;
            if(arguments.length == 1) {
                try {
                    plot = Integer.parseInt(arguments[0]);
                } catch (NumberFormatException ignored) {}
            } else if(arguments.length == 2) {
                username = arguments[0];
                try {
                    plot = Integer.parseInt(arguments[1]);
                } catch (NumberFormatException ignored) {}
            }
            OPSuchtAddon.references().plotSwitchManager().setData(username, plot);
            this.addon.logger().info(
                "Remembered own ({}) {}. plot for plotswitch",
                username,
                plot
            );
            return false;
        }
    }

    public static class VisitCommand extends SubCommand {

        private final OPSuchtAddon addon;

        private VisitCommand(OPSuchtAddon addon) {
            super("visit", "v", "tp", "teleport", "goto", "warp");
            this.addon = addon;
        }

        @Override
        public boolean execute(String prefix, String[] arguments) {
            if(arguments.length == 0) {
                return false;
            }
            String username = arguments[0];
            int plot = 1;
            if(arguments.length > 1) {
                try {
                    plot = Integer.parseInt(arguments[1]);
                } catch (NumberFormatException ignored) {}
            }
            OPSuchtAddon.references().plotSwitchManager().setData(username, plot);
            this.addon.logger().info(
                "Remembered {}'s {}. plot for plotswitch",
                username,
                plot
            );
            return false;
        }
    }
}
