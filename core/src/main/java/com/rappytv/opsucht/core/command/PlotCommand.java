package com.rappytv.opsucht.core.command;

import com.rappytv.opsucht.core.OPSuchtAddon;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;

public class PlotCommand extends Command {

    public PlotCommand() {
        super("plot", "p");

        this.withSubCommand(new VisitCommand());
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
        return false;
    }

    public static class VisitCommand extends SubCommand {

        private VisitCommand() {
            super("visit", "v");
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
            OPSuchtAddon.references().plotSwitchManager().init(username, plot);
            return false;
        }
    }
}
