package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class BlocktoolCommand extends SECommand {
    private final Argument<BlockTool.Mode> modeArg = ArgumentType.Enum("mode", BlockTool.Mode.class).setDefaultValue(BlockTool.Mode.SELECT);

    public BlocktoolCommand() {
        super("bltool");
        new Syntax(null, this::call, modeArg);
        new Syntax(null, this::call);
    }

    private void call(Player player, CommandContext context) {
        player.getInventory().addItemStack(BlockTool.ITEM.withTag(Tag.String("mode"), context.get(modeArg).name()));
    }

}
