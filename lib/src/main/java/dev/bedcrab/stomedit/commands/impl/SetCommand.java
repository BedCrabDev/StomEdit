package dev.bedcrab.stomedit.commands.impl;

import dev.bedcrab.stomedit.SEUtils;
import dev.bedcrab.stomedit.blocktool.BlockTool;
import dev.bedcrab.stomedit.commands.SECommand;
import dev.bedcrab.stomedit.SEColorUtil;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBTIntArray;

public class SetCommand extends SECommand {
    private final ArgumentBlockState blockArg = ArgumentType.BlockState("block");
    public SetCommand() {
        super("set", "<block>", BlockTool.Mode.SELECT);
        SE_addSyntax(this::call, blockArg);
    }

    private void call(Player player, CommandContext context, ItemStack item) {
        NBTIntArray from = (NBTIntArray) item.getTag(Tag.NBT("from"));
        int fromX = from.get(0); int fromY = from.get(1); int fromZ = from.get(2);

        NBTIntArray to = (NBTIntArray) item.getTag(Tag.NBT("to"));
        int toX = to.get(0); int toY = to.get(1); int toZ = to.get(2);

        Block block = context.get(blockArg);
        Instance instance = player.getInstance();
        int count = 0;
        for (int x = Math.max(toX, fromX); x >= Math.min(fromX, toX); x--) {
            for (int y = Math.max(toY, fromY); y >= Math.min(fromY, toY); y--) {
                for (int z = Math.max(toZ, fromZ); z >= Math.min(fromZ, toZ); z--) {
                    instance.setBlock(x, y, z, block);
                    count++;
                }
            }
        }
        SEUtils.message(player, SEColorUtil.GENERIC.format("Filled %% with %%", count+(count > 0 ? " blocks" : " block"), block.name()));
    }
}
