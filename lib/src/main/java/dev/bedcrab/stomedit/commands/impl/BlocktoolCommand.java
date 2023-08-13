package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class BlocktoolCommand extends Command {
    private final ArgumentEnum<BlockTool.Mode> modeArg = ArgumentType.Enum("mode", BlockTool.Mode.class);

    public BlocktoolCommand() {
        super("/blocktool", "/bltool");
        addSyntax(this::call, modeArg);
        addSyntax(this::call);

        setDefaultExecutor(this::call);
    }

    private void call(CommandSender sender, CommandContext context) {
        if (!(sender instanceof Player player)) return;
        player.getInventory().addItemStack(BlockTool.ITEM.withTag(Tag.String("mode"), context.has(modeArg) ? context.get(modeArg).name() : BlockTool.Mode.MODIFY.name()));
    }

}
