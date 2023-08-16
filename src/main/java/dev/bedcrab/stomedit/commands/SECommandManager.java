package dev.bedcrab.stomedit.commands;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.commands.impl.BlocktoolCommand;
import dev.bedcrab.stomedit.commands.impl.SetCommand;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.commands.impl.ToolShapeCommand;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;

public final class SECommandManager {
    public static void invalidCommand(CommandSender sender, CommandContext ctx, String syntax) {
        SEUtils.message(sender, SEColorUtil.FAIL.format("Invalid syntax for /%%", ctx.getCommandName()));
        SEUtils.message(sender, SEColorUtil.GENERIC.format("Syntax is: %%", syntax));
    }

    private final CommandManager manager;
    public SECommandManager(CommandManager manager) {
        this.manager = manager;
    }

    public void enable() {
        manager.register(new BlocktoolCommand());
        manager.register(new ToolShapeCommand());
        manager.register(new SetCommand());
    }
}
