package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class BlocktoolCommand extends SECommand {
    private final ArgumentEnum<BlockTool.Mode> modeArg = ArgumentType.Enum("mode", BlockTool.Mode.class);

    public BlocktoolCommand() {
        super("bltool", "", null);
        SE_addSyntax(this::call, modeArg);
        SE_addSyntax(this::call);
    }

    private void call(Player player, CommandContext context) {
        player.getInventory().addItemStack(BlockTool.ITEM.withTag(Tag.String("mode"), context.has(modeArg) ? context.get(modeArg).name() : BlockTool.Mode.MODIFY.name()));
    }

}
