package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.StomEditException;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.SEColorUtil;
import dev.bedcrab.stomedit.executor.JobWorker;
import dev.bedcrab.stomedit.session.PlayerSession;
import dev.bedcrab.stomedit.session.impl.HistoryData;
import dev.bedcrab.stomedit.session.impl.ToolShapeData;
import dev.bedcrab.stomedit.toolshapes.ToolShapeIterator;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class SetCommand extends SECommand {
    private final ArgumentBlockState blockArg = ArgumentType.BlockState("block");
    public SetCommand(JobWorker worker) {
        super(worker, "set");
        new Syntax(BlockTool.Mode.SELECT, this::call, blockArg);
    }

    private void call(Player player, CommandContext context, PlayerSession session) throws StomEditException {
        if (worker == null) throw new NullPointerException("No job worker!");
        ToolShapeData data = session.read(ToolShapeData.class, ToolShapeData::defaultFunc);
        HistoryData history = session.read(HistoryData.class, HistoryData::defaultFunc);
        ToolShapeIterator iter = data.parseIter();
        Block block = context.get(blockArg);
        Instance instance = player.getInstance();
        session.write(history.save(worker.newJob(
            iter, bPos -> new JobWorker.Change(instance, bPos, block)
        ), "Fill "+iter.count()+SEUtils.plural(iter.count(), "block", "blocks")));
        SEUtils.message(player, SEColorUtil.GENERIC.format("Filled %% with %%", iter.count()+(iter.count() != 0 ? " blocks" : " block"), block.name()));
    }
}
