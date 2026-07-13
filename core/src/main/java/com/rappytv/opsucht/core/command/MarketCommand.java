package com.rappytv.opsucht.core.command;

import com.rappytv.opsucht.api.market.ValueFormatter;
import com.rappytv.opsucht.api.market.MarketItem;
import com.rappytv.opsucht.api.market.MarketManager;
import com.rappytv.opsucht.api.market.MarketStack;
import com.rappytv.opsucht.core.OPSuchtAddon;
import java.util.ArrayList;
import java.util.List;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.command.Command;
import net.labymod.api.client.chat.command.SubCommand;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MarketCommand extends Command {

  public MarketCommand(OPSuchtAddon addon) {
    super("opmarket", "opmarkt");

    this.messagePrefix(OPSuchtAddon.prefix());
    this.translationKey("opsucht.command");
    this.withSubCommand(new InventoryValueCommand(addon, this));
    this.withSubCommand(new ItemValueCommand(addon, this));
  }

  @Override
  public boolean execute(String prefix, String[] arguments) {
    List<String> subCommands = new ArrayList<>();
    for (SubCommand subCommand : this.getSubCommands()) {
      subCommands.add(subCommand.getPrefix());
    }

    this.displayTranslatable(
        "usage",
        NamedTextColor.RED,
        Component.text(prefix),
        Component.text(String.join("/", subCommands))
    );
    return true;
  }

  public static class InventoryValueCommand extends SubCommand {

    private final OPSuchtAddon addon;
    private final MarketManager manager;
    private final ValueFormatter formatter;

    public InventoryValueCommand(OPSuchtAddon addon, MarketCommand command) {
      super("inventory", "inventar");
      this.addon = addon;
      this.manager = OPSuchtAddon.references().marketManager();
      this.formatter = OPSuchtAddon.references().valueFormatter();

      this.messagePrefix(command.messagePrefix);
      this.translationKey("opsucht.command");
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
      if (!this.addon.server().isConnected()) {
        this.displayTranslatable("notConnected", NamedTextColor.RED);
        return true;
      }
      ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
      if (player == null) {
        this.displayTranslatable("noPlayer", NamedTextColor.RED);
        return true;
      }

      String priceFormat = this.addon.configuration().priceFormat().get();
      this.manager.calculateInventoryValue(
          player.inventory(),
          true,
          (data) -> {
            if (data == null || !data.isValid()) {
              this.displayTranslatable("inventory.invalidData", NamedTextColor.RED);
              return;
            }
            if (data.isEmpty()) {
              this.displayTranslatable("inventory.empty", NamedTextColor.RED);
              return;
            }
            Component component = Component.empty()
                .append(OPSuchtAddon.prefix())
                .append(Component.translatable(
                    this.getTranslationKey("inventory.value"),
                    NamedTextColor.GRAY,
                    Component.text(data.itemAmount(), NamedTextColor.AQUA)
                ))
                .append(Component.newline())
                .append(OPSuchtAddon.prefix())
                .append(Component.translatable(
                    this.getTranslationKey("buyValue"),
                    NamedTextColor.GRAY,
                    this.formatter.formatSingleValueComponent(
                        priceFormat,
                        data.buyValue(),
                        NamedTextColor.AQUA
                    )
                ))
                .append(Component.newline())
                .append(OPSuchtAddon.prefix())
                .append(Component.translatable(
                    this.getTranslationKey("sellValue"),
                    NamedTextColor.GRAY,
                    this.formatter.formatSingleValueComponent(
                        priceFormat,
                        data.sellValue(),
                        NamedTextColor.RED
                    )
                ));

            this.displayMessage(component);
          }
      );
      return true;
    }
  }

  public static class ItemValueCommand extends SubCommand {

    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 128;

    private final OPSuchtAddon addon;
    private final MarketManager manager;
    private final ValueFormatter formatter;

    public ItemValueCommand(OPSuchtAddon addon, MarketCommand command) {
      super("item");
      this.addon = addon;
      this.manager = OPSuchtAddon.references().marketManager();
      this.formatter = OPSuchtAddon.references().valueFormatter();

      this.messagePrefix(command.messagePrefix);
      this.translationKey("opsucht.command");
    }

    @Override
    public boolean execute(String prefix, String[] arguments) {
      if (!this.addon.server().isConnected()) {
        this.displayTranslatable("notConnected", NamedTextColor.RED);
        return true;
      }
      ClientPlayer player = Laby.labyAPI().minecraft().getClientPlayer();
      if (player == null) {
        this.displayTranslatable("noPlayer", NamedTextColor.RED);
        return true;
      }

      ItemStack currentItem = player.getMainHandItemStack();
      MarketStack stack;

      if (arguments.length == 0) {
        if (currentItem == null || currentItem.isAir()) {
          this.displayTranslatable("item.emptySlot", NamedTextColor.RED);
          return true;
        }

        MarketItem item = this.manager.getItem(currentItem.getIdentifier().getPath());
        if (item == null) {
          this.displayTranslatable("item.holdingItemNoPrice", NamedTextColor.RED);
          return true;
        }

        stack = new MarketStack(item, currentItem.getSize());
      } else if (arguments.length == 1) {
        Integer amount = this.tryAmount(arguments[0]);

        if (amount != null) {
          if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            this.displayTranslatable(
                "item.invalidAmount",
                NamedTextColor.RED,
                Component.text(MIN_AMOUNT, NamedTextColor.AQUA),
                Component.text(MAX_AMOUNT, NamedTextColor.AQUA)
            );
            return true;
          }
          if (currentItem == null || currentItem.isAir()) {
            this.displayTranslatable("item.emptySlot", NamedTextColor.RED);
            return true;
          }

          MarketItem item = this.manager.getItem(currentItem.getIdentifier().getPath());
          if (item == null) {
            this.displayTranslatable("item.holdingItemNoPrice", NamedTextColor.RED);
            return true;
          }

          stack = new MarketStack(item, amount);
        } else {
          MarketItem item = this.manager.getItem(arguments[0].toLowerCase());
          if (item == null) {
            this.displayTranslatable("item.itemNoPrice", NamedTextColor.RED);
            return true;
          }

          stack = new MarketStack(item, 1);
        }
      } else {
        MarketItem item = this.manager.getItem(arguments[0].toLowerCase());
        if (item == null) {
          this.displayTranslatable("item.itemNoPrice", NamedTextColor.RED);
          return true;
        }

        int amount = this.tryAmountSafe(arguments[1]);
        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
          this.displayTranslatable("item.invalidAmount",
              NamedTextColor.RED,
              Component.text(MIN_AMOUNT, NamedTextColor.AQUA),
              Component.text(MAX_AMOUNT, NamedTextColor.AQUA)
          );
          return true;
        }

        stack = new MarketStack(item, amount);
      }

      if (!stack.isValid()) {
        this.displayTranslatable("item.invalidData", NamedTextColor.RED);
        return true;
      }

      Component[] valueComponents = this.getValueComponents(stack);
      Component component = Component.empty()
          .append(OPSuchtAddon.prefix())
          .append(Component.translatable(
              this.getTranslationKey("item.value"),
              NamedTextColor.GOLD,
              Component.text(stack.getName(), NamedTextColor.WHITE)
          ).decorate(TextDecoration.BOLD));

      for (Component valueComponent : valueComponents) {
        component.append(Component.newline())
            .append(OPSuchtAddon.prefix())
            .append(valueComponent);
      }

      this.displayMessage(component);
      return true;
    }

    private Component[] getValueComponents(MarketStack stack) {
      String priceFormat = this.addon.configuration().priceFormat().get();
      Component[] components = new Component[2];

      components[0] = Component.translatable(
          this.getTranslationKey("buyValue"),
          NamedTextColor.GRAY,
          this.formatter.formatSingleValueComponent(
              priceFormat,
              stack.getBuyPrice(),
              NamedTextColor.AQUA
          )
      );
      components[1] = Component.translatable(
          this.getTranslationKey("sellValue"),
          NamedTextColor.GRAY,
          this.formatter.formatSingleValueComponent(
              priceFormat,
              stack.getSellPrice(),
              NamedTextColor.RED
          )
      );

      if (stack.getSize() > 1) {
        components[0].append(Component.space()).append(Component.translatable(
            this.getTranslationKey("item.stackPrice"),
            this.formatter.formatSingleValueComponent(
                priceFormat,
                stack.getStackBuyPrice(),
                NamedTextColor.AQUA
            ),
            Component.text(stack.getSize(), NamedTextColor.YELLOW)
        ));
        components[1].append(Component.space()).append(Component.translatable(
            this.getTranslationKey("item.stackPrice"),
            this.formatter.formatSingleValueComponent(
                priceFormat,
                stack.getStackSellPrice(),
                NamedTextColor.RED
            ),
            Component.text(stack.getSize(), NamedTextColor.YELLOW)
        ));
      }

      return components;
    }

    private int tryAmountSafe(String amount) {
      Integer number = this.tryAmount(amount);
      return number != null ? number : 1;
    }

    @Nullable
    private Integer tryAmount(String amount) {
      try {
        return Integer.parseInt(amount);
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
  }
}
