package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolSessionData;
import dev.bedcrab.stomedit.session.impl.ToolShapeSessionData;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

public class BlocktoolCommand extends SECommand {
    public BlocktoolCommand() {
        super("bltool");
        new Syntax(null, this::call);
    }

    private void call(Player player, CommandContext context, PlayerSession session) {
        player.getInventory().addItemStack(BlockTool.ITEM.withTag(Tag.Boolean("blocktool"), true));
        session.write(BLToolSessionData.DEFAULT);
        session.write(ToolShapeSessionData.DEFAULT);
    }
}
