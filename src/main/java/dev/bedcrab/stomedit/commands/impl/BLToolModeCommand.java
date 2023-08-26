package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.BLToolSessionData;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class BLToolModeCommand extends SECommand {
    private final ArgumentEnum<BlockTool.Mode> modeArg = ArgumentType.Enum("mode", BlockTool.Mode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
    public BLToolModeCommand() {
        super("bltoolmode");
        new Syntax(null, this::call, modeArg);
    }

    private void call(Player player, CommandContext context, PlayerSession session) {
        BLToolSessionData data = session.read(BLToolSessionData.class, BLToolSessionData.DEFAULT);
        BlockTool.Mode newMode = context.get(modeArg);
        session.write(data.withMode(newMode));
        SEUtils.message(player, SEColorUtil.GENERIC.format("Using mode %%", newMode.name()));
    }
}
