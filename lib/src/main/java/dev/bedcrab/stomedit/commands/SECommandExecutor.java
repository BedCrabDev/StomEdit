package dev.bedcrab.stomedit.commands;

import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public interface SECommandExecutor {
    void accept(Player player, CommandContext context, ItemStack item);
}
