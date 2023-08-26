package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

public class SetCommand extends SECommand {
    private final ArgumentBlockState blockArg = ArgumentType.BlockState("block");
    public SetCommand() {
        super("set");
        new Syntax(BlockTool.Mode.SELECT, this::call, blockArg);
    }

    private void call(Player player, CommandContext context, PlayerSession session) throws StomEditException {
        ToolShapeSessionData toolshapeData = session.read(ToolShapeSessionData.class, ToolShapeSessionData.DEFAULT);
        ToolShapeIterator iter = toolshapeData.parseIter();
        Block block = context.get(blockArg);
        try { iter.fill(player.getInstance(), () -> block); } catch (Exception e) {
            throw new StomEditException(player, "Error whilst filling selection!", e);
        }
        SEUtils.message(player, SEColorUtil.GENERIC.format("Filled %% with %%", iter.count()+(iter.count() != 0 ? " blocks" : " block"), block.name()));
    }
}
